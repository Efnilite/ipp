package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Generator for the Team Survival gamemode.
 */
public final class TeamSurvivalGenerator extends MultiplayerGenerator {

    // where every player is
    private final Map<ParkourPlayer, Block> lastPlayerBlockMap = new HashMap<>();

    public TeamSurvivalGenerator(Session session) {
        super(session, GeneratorOption.DISABLE_SCHEMATICS);

        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATICS);
    }

    @Override
    public void overrideProfile() {
        profile.set("useSchematic", "false");
    }

    @Override
    public void tick() {
        if (stopped || session == null || getPlayers().isEmpty()) {
            return;
        }

        // the index of the last person
        int lastIndex = Integer.MAX_VALUE;
        ParkourPlayer lastPlayer = null;

        for (ParkourPlayer pp : getPlayers()) {
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
            if (history.contains(blockBelow)) {
                // player is at a known position

                // register as the last player block
                lastPlayerBlockMap.put(pp, blockBelow);
                // set the current index
                currentIndex = history.indexOf(blockBelow);
            } else {
                // player is not at a known position
                if (last != null) {
                    currentIndex = history.indexOf(last);
                } else {
                    currentIndex = -1;
                }
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

        if (lastPlayer != null) {
            player = lastPlayer;
        }
    }

    @Override
    public void reset(boolean regenerate) {
        if (!regenerate) {
            if (getPlayers().size() == 1) {
                player = getPlayers().get(0);
                regenerate = true;
            }
        }

        super.reset(regenerate);

        if (regenerate) {
            lastPlayerBlockMap.clear();

            getPlayers().forEach(player -> player.teleport(playerSpawn));
        }
    }

    @Override
    public Mode getMode() {
        return PlusMode.TEAM_SURVIVAL;
    }
}
