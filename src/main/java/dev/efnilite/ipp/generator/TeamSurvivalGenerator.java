package dev.efnilite.ipp.generator;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Generator for the Team Survival gamemode.
 *
 */
public final class TeamSurvivalGenerator extends DefaultGenerator {

    // where every player is
    private final Map<ParkourPlayer, Block> lastPlayerBlockMap = new HashMap<>();

    public TeamSurvivalGenerator(Session session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    @Override
    public void tick() {
        int trailing = Integer.MAX_VALUE;
        for (ParkourPlayer pp : session.getPlayers()) {
            Location location = pp.getLocation();
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock(); // Get the block below

            if (blockBelow.getType() == Material.AIR) {
                continue;
            }

            if (!positionIndexMap.containsKey(blockBelow)) {
                continue;
            }

            int currentIndex = positionIndexMap.get(blockBelow); // current index of the player

            lastPlayerBlockMap.put(pp, blockBelow);

            if (trailing > currentIndex) {
                player = pp;
                player.blockLead = 4;
            }
        }

        super.tick();
    }
}
