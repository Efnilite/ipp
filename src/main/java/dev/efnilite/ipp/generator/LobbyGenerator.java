package dev.efnilite.ipp.generator;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.mode.LobbyMode;
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
        double[] distances = zone.distanceToBoundaries(location);
        return distances[0] < LobbyMode.LOBBY_SAFE_RANGE || distances[2] < LobbyMode.LOBBY_SAFE_RANGE;
    }

    @Override
    public void menu() {
        SettingsMenu.open(player, ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.LEADS);
    }
}
