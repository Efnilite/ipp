package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.base.Direction;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.gamemode.PlusGamemodes;

/**
 * Generator for Lobby mode
 */
public final class LobbyGenerator extends PlusGenerator {

    public LobbyGenerator(SingleSession session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);

        // setup menu
        menu = new SettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.LEADS);

        profile.setSetting("blockLead", "512");
        profile.setSetting("useSchematic", "false");
    }

    // remove -2 height jumps
    @Override
    protected void calculateHeight() {
        heightChances.clear();

        int percentage = 0;
        for (int i = 0; i < Option.NORMAL_UP.get(); i++) {
            heightChances.put(percentage, 1);
            percentage++;
        }
        for (int i = 0; i < Option.NORMAL_LEVEL.get(); i++) {
            heightChances.put(percentage, 0);
            percentage++;
        }
        for (int i = 0; i < Option.NORMAL_DOWN.get(); i++) {
            heightChances.put(percentage, -1);
            percentage++;
        }
    }

    // remove 1 block jumps
    @Override
    protected void calculateDistance() {
        distanceChances.clear();

        int two = Option.NORMAL_TWO_BLOCK.get();
        int three = Option.NORMAL_THREE_BLOCK.get();
        int four = Option.NORMAL_FOUR_BLOCK.get();

        int percentage = 0;
        for (int i = 0; i < two; i++) {
            distanceChances.put(percentage, 2);
            percentage++;
        }
        for (int i = 0; i < three; i++) {
            distanceChances.put(percentage, 3);
            percentage++;
        }
        for (int i = 0; i < four; i++) {
            distanceChances.put(percentage, 4);
            percentage++;
        }
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.LOBBY;
    }
}