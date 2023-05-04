package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generator for speed jump mode
 */
public final class SuperJumpGenerator extends PlusGenerator {

    // the platform radius, excluding the inner block
    private static final int PLATFORM_RADIUS = 2;
    private double jumpDistance = 4;
    private final List<List<Block>> history = new ArrayList<>();

    public SuperJumpGenerator(Session session) {
        // setup settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.REDUCE_RANDOM_BLOCK_SELECTION_ANGLE);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.LEADS, ParkourOption.SCHEMATICS, ParkourOption.SPECIAL_BLOCKS);

        updateJumpDistance();

        heightChances.clear();
        heightChances.put(0, 1.0);

        player.player.setMaximumAir(100_000_000);
    }

    @Override
    public void overrideProfile() {
        profile.set("blockLead", "1");
    }

    // update jump distance
    private void updateJumpDistance() {
        jumpDistance += 0.3;

        distanceChances.clear();
        distanceChances.put((int) jumpDistance, 1.0);

        Player bPlayer = player.player;

        // According to gathered data, the potion effect line goes roughly like this:
        // y = 3.93x - 34.9, where x is the jump distance
        // however players should start with speed 2
        // to make every jump doable shift the line left
        // level = 3.93 * distance - 31
        double level = 3.93 * jumpDistance - 31;
        if (level < 4) {
            level = 1.5 * jumpDistance; // in the beginning
        } else if (level > 255) {
            level = 255;
        }

        if (level < 255) { // player has potion and new level will be under 255
            bPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, (int) level, false, false));
        }
    }

    @Override
    public List<Block> selectBlocks() {
        Block next = selectNext(getLatest(), (int) jumpDistance, 0);// no difference in height

        if (next == null) {
            return Collections.emptyList();
        }

        return getBlocksAround(next);
    }

    private List<Block> getBlocksAround(Block base) {
        int lastOfRadius = 2 * PLATFORM_RADIUS + 1;
        int baseX = base.getX();
        int baseY = base.getY();
        int baseZ = base.getZ();

        List<Block> blocks = new ArrayList<>();
        World world = base.getWorld();
        int amount = lastOfRadius * lastOfRadius;
        for (int i = 0; i < amount; i++) {
            int[] xz = Util.spiralAt(i);
            int x = xz[0];
            int z = xz[1];

            x += baseX;
            z += baseZ;

            blocks.add(world.getBlockAt(x, baseY, z));
        }
        return blocks;
    }

    /**
     * Starts the check
     */
    @Override
    public void tick() {
        if (stopped) {
            task.cancel();
            return;
        }

        session.getPlayers().forEach(other -> {
            updateVisualTime(other, other.selectedTime);
            other.updateScoreboard(this);
            other.player.setSaturation(20);
        });

        session.getSpectators().forEach(ParkourSpectator::update);

        if (player.getLocation().subtract(lastStandingPlayerLocation).getY() < -10) { // fall check
            fall();
            return;
        }

        List<Block> platformBelowPlayer = match(player.getLocation().subtract(0, 1, 0).getBlock());

        if (platformBelowPlayer == null) { // player is presumably midair or in schematic
            return;
        }

        int currentIndex = history.indexOf(platformBelowPlayer); // current index of the player
        int deltaFromLast = currentIndex - lastPositionIndexPlayer;

        if (deltaFromLast <= 0) { // the player is actually making progress and not going backwards (current index is higher than the previous)
            return;
        }

        lastStandingPlayerLocation = player.getLocation();

        int blockLead = profile.get("blockLead").asInt();

        int deltaCurrentTotal = history.size() - currentIndex; // delta between current index and total
        if (deltaCurrentTotal <= blockLead) {
            generate(); // generate the remaining amount so it will match
        }
        lastPositionIndexPlayer = currentIndex;

        // delete trailing blocks
        for (int idx = 0; idx < history.size(); idx++) {
            if (currentIndex - idx > BLOCK_TRAIL) {
                platformBelowPlayer.forEach(block -> block.setType(Material.AIR));
            }
        }

        for (int i = 0; i < (Option.ALL_POINTS ? deltaFromLast : 1); i++) { // score the difference
            score();
        }

        if (start == null) { // start stopwatch when first point is achieved
            start = Instant.now();
        }
    }

    private @Nullable List<Block> match(Block current) {
        for (List<Block> blocks : history) {
            if (blocks.contains(current)) {
                return blocks;
            }
        }
        return null;
    }

    @Override
    public void reset(boolean regenerate) {
        jumpDistance = 4;

        player.player.removePotionEffect(PotionEffectType.SPEED);
        if (regenerate) {
            updateJumpDistance();
        }

        // clear history
        for (List<Block> blocks : history) {
            blocks.forEach(block -> block.setType(Material.AIR));
        }
        history.clear();

        super.reset(regenerate);
    }

    @Override
    public void score() {
        super.score();

        updateJumpDistance();
    }

    @Override
    public Mode getMode() {
        return PlusMode.SUPER_JUMP;
    }
}