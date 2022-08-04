package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.session.MultiSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Generator for the Team Survival gamemode.
 */
public final class TeamSurvivalGenerator extends MultiplayerGenerator {

    // where every player is
    private final Map<ParkourPlayer, Block> lastPlayerBlockMap = new HashMap<>();

    public TeamSurvivalGenerator(MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.IGNORE_CHECK_FOR_PROGRESS);

        menu = new SettingsMenu(ParkourOption.SCHEMATICS);
    }

    @Override
    public void updatePreferences() {
        profile.setSetting("useSchematic", "false");
    }

    @Override
    public void updateScoreboard() {
        super.updateScoreboard();

        for (ParkourPlayer pp : session.getPlayers()) {
            if (pp == player || player.getBoard() == null || pp.getBoard() == null) {
                continue;
            }

            pp.getBoard().updateTitle(player.getBoard().getTitle());
            pp.getBoard().updateLines(player.getBoard().getLines());
        }

    }

    @Override
    protected void registerScore() {
        for (ParkourPlayer pp : session.getPlayers()) {
            getGamemode().getLeaderboard().put(pp.getUUID(),
                    new Score(pp.getName(), stopwatch.toString(), player.calculateDifficultyScore(), score));
        }
    }

    @Override
    public void tick() {
        // the index of the last person
        int lastIndex = Integer.MAX_VALUE;
        ParkourPlayer lastPlayer = null;
        ParkourPlayer owner = session.getPlayers().get(0);

        for (ParkourPlayer pp : session.getPlayers()) {
            Location location = pp.getLocation();
            // get the block below player
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock();

            // teleport player if worlds don't match
            if (location.getWorld() != playerSpawn.getWorld()) {
                pp.teleport(playerSpawn);
                continue;
            }

            // get the last solid block that the player was standing on
            Block last = lastPlayerBlockMap.get(pp);

            // if the difference in height is more than 10, reset
            if (last != null && last.getY() - location.getY() > 10 && playerSpawn.distance(location) > 5) {
                fall();
                return;
            }

            int currentIndex;

            // get the index for the current player

            // player is at an unknown block, use the last confirmed solid block
            if (!positionIndexMap.containsKey(blockBelow) || blockBelow.getType() == Material.AIR) {
                Block block = lastPlayerBlockMap.get(player);

                // if last registered block is null, it means the player isn't on the parkour yet, so just skip this check
                if (block == null || !positionIndexMap.containsKey(block)) {
                    continue;
                }

                currentIndex = positionIndexMap.get(block);
            // player is at a known block, so just use the index belonging to that block
            } else {
                currentIndex = positionIndexMap.get(blockBelow); // current index of the player
                lastPlayerBlockMap.put(pp, blockBelow);
            }

            if (lastIndex >= currentIndex) {
                lastIndex = currentIndex;
                lastPlayer = pp;
            }
        }

        if (lastPlayer != null) {
            player = lastPlayer;
        }

        super.tick();
        player = owner;
    }

    @Override
    public void reset(boolean regenerate) {
        super.reset(regenerate);

        lastPlayerBlockMap.clear();

        session.getPlayers().forEach(player -> player.teleport(playerSpawn));
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.TEAM_SURVIVAL;
    }
}
