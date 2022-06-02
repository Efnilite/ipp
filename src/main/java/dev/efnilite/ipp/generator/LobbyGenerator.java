package dev.efnilite.ipp.generator;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.mode.LobbyMode;
import org.bukkit.Location;

/**
 * Generator for Lobby mode
 */
public final class LobbyGenerator extends DefaultGenerator {

    public LobbyGenerator(SingleSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    @Override
    public boolean isNearingEdge(Location location) {
        double[] distances = zone.distanceToBoundaries(location);
        System.out.println("Distances? x:" + distances[0] + " y: " + distances[1] + " z: " + distances[2]);
        System.out.println("Nearing range? " + (distances[0] < LobbyMode.LOBBY_SAFE_RANGE || distances[2] < LobbyMode.LOBBY_SAFE_RANGE));
        return distances[0] < LobbyMode.LOBBY_SAFE_RANGE || distances[2] < LobbyMode.LOBBY_SAFE_RANGE;
    }

    @Override
    public void fall() {
        heading = Option.HEADING;
        super.fall();
    }

    @Override
    public void menu() {
        SettingsMenu.open(player, ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.LEADS);
    }
}
