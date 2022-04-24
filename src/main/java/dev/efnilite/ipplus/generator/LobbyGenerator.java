package dev.efnilite.ipplus.generator;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Generator for Lobby mode
 */
public class LobbyGenerator extends DefaultGenerator {

    public LobbyGenerator(@NotNull ParkourPlayer player) {
        super(player, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    @Override
    public boolean isNearingEdge(Location location) {
        double[] distances = zone.distanceToAxes(location);
        return distances[0] < 10 || distances[2] < 10;
    }
}
