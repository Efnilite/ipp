package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.generator.Direction;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.mode.LobbyMode;
import org.bukkit.Location;

/**
 * Generator for Lobby mode
 */
public final class LobbyGenerator extends PlusGenerator {

    public LobbyGenerator(SingleSession session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);

        // setup menu
        menu = new SettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.LEADS);
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
        // random direction
        Direction[] values = Direction.values();
        heading = values[random.nextInt(values.length)];

        super.fall();
    }
}