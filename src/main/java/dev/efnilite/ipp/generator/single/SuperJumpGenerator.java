package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.lib.vilib.util.Locations;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Generator for speed jump mode
 */
public final class SuperJumpGenerator extends PlusGenerator {

    // the platform radius, excluding the inner block
    private static final int PLATFORM_RADIUS = 2;
    private static final Supplier<Double> DEFAULT_JUMP_DISTANCE = () -> 3.0 + PLATFORM_RADIUS;
    private final List<List<Block>> history = new ArrayList<>();
    private double jumpDistance = DEFAULT_JUMP_DISTANCE.get();

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

        player.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, (int) level, false, false));
    }

    @Override
    public List<Block> selectBlocks() {
        List<Location> locations = getLatestBlocks().stream()
                .map(Block::getLocation)
                .toList();

        Location min = locations.stream().reduce(Locations::min).orElseThrow();
        Location max = locations.stream().reduce(Locations::max).orElseThrow();

        Location center = min.add(max).multiply(.5);
        Location next = center.add(heading.clone().multiply(jumpDistance));

        List<Block> blocks = getBlocksAround(next.getBlock());
        history.add(blocks);
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

        getPlayers().forEach(other -> {
            updateVisualTime(other, other.selectedTime);
            other.updateScoreboard(this);
            other.player.setSaturation(20);
        });

        getSpectators().forEach(ParkourSpectator::update);

        if (player.getLocation().subtract(lastStandingPlayerLocation).getY() < -10) { // fall check
            fall();
            return;
        }

        List<Block> platformBelowPlayer = match(player.getLocation().subtract(0, 1, 0).getBlock());

        if (!history.contains(platformBelowPlayer)) {
            return; // player is on an unknown block
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
            generate(blockLead - deltaCurrentTotal); // generate the remaining amount so it will match
        }
        lastPositionIndexPlayer = currentIndex;

        for (int i = currentIndex - BLOCK_TRAIL; i >= currentIndex - 2 * BLOCK_TRAIL; i--) {
            // avoid setting beginning block to air
            if (i <= 0) {
                continue;
            }

            history.get(i).forEach(block -> block.setType(Material.AIR));
        }

        score();

        if (start == null) { // start stopwatch when first point is achieved
            start = Instant.now();
        }
    }

    private @Nullable List<Block> match(Block current) {
        return history.stream().filter(blocks -> blocks.contains(current)).findFirst().orElse(null);
    }

    @Override
    public void reset(boolean regenerate) {
        player.player.removePotionEffect(PotionEffectType.SPEED);
        if (regenerate) {
            jumpDistance = DEFAULT_JUMP_DISTANCE.get();
            updateJumpDistance();
        }

        // clear history
        history.remove(0);
        history.forEach(blocks -> blocks.forEach(block -> block.setType(Material.AIR)));
        history.clear();

        super.reset(regenerate);
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
            int[] xz = spiralAt(i);

            blocks.add(world.getBlockAt(xz[0] + baseX, baseY, xz[1] + baseZ));
        }
        return blocks;
    }

    private List<Block> getLatestBlocks() {
        return history.get(history.size() - 1);
    }

    @Override
    public void generateFirst(Location spawn, Location block) {
        history.add(List.of(block.getBlock()));
        super.generateFirst(spawn, block);
        history.remove(0);
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

    /**
     * Gets a spiral
     *
     * @param n The number of  value
     * @return the coords of this value
     */
    // https://math.stackexchange.com/a/163101
    private int[] spiralAt(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid n bound: %d".formatted(n));
        }

        n++; // one-index
        int k = (int) Math.ceil((Math.sqrt(n) - 1) / 2);
        int t = 2 * k + 1;
        int m = t * t;
        t--;

        if (n > m - t) {
            return new int[]{k - (m - n), -k};
        } else {
            m -= t;
        }

        if (n > m - t) {
            return new int[]{-k, -k + (m - n)};
        } else {
            m -= t;
        }

        if (n > m - t) {
            return new int[]{-k + (m - n), k};
        } else {
            return new int[]{k, k - (m - n - t)};
        }
    }
}