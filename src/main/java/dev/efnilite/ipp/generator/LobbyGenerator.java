package dev.efnilite.ipp.generator;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.session.Session;
import org.bukkit.Location;

/**
 * Generator for Lobby mode
 */
public class LobbyGenerator extends DefaultGenerator {

    public LobbyGenerator(Session session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    @Override
    public boolean isNearingEdge(Location location) {
        double[] distances = zone.distanceToAxes(location);
        return distances[0] < 10 || distances[2] < 10;
    }
}
