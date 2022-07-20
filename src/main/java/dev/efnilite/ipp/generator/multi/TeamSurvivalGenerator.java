package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.session.MultiSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Generator for the Team Survival gamemode.
 *
 */
public final class TeamSurvivalGenerator extends MultiplayerGenerator {

    // where every player is
    private final Map<ParkourPlayer, Block> lastPlayerBlockMap = new HashMap<>();

    public TeamSurvivalGenerator(MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    @Override
    public void tick() {
        ParkourPlayer trailer = null;
        int trailing = Integer.MAX_VALUE;

        for (ParkourPlayer pp : session.getPlayers()) {
            Location location = pp.getLocation();
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock(); // Get the block below

            if (location.getWorld() != playerSpawn.getWorld()) { // sometimes player worlds don't match (somehow)
                pp.teleport(playerSpawn);
                return;
            }

            Block last = lastPlayerBlockMap.get(pp);
            if (last != null && last.getY() - location.getY() > 10 && playerSpawn.distance(location) > 5) { // Fall check
                fall();
                return;
            }

            int currentIndex;

            // get last index for player
            if (!positionIndexMap.containsKey(blockBelow) || blockBelow.getType() == Material.AIR) {
                currentIndex = positionIndexMap.get(lastPlayerBlockMap.get(player));
            } else {
                currentIndex = positionIndexMap.get(blockBelow); // current index of the player
                lastPlayerBlockMap.put(pp, blockBelow);
            }

            if (trailing >= currentIndex) {
                trailing = currentIndex;
                trailer = pp;
                player.blockLead = session.getPlayers().get(0).blockLead;
                player.showFallMessage = false;
            }
        }
        // only update if leader is not null
        player = trailer != null ? trailer : player;

        System.out.println("Trailer: " + player.getName());

        super.tick();
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
