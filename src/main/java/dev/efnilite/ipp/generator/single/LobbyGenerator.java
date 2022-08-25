package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import org.bukkit.Material;

/**
 * Generator for Lobby mode
 */
public final class LobbyGenerator extends PlusGenerator {

    public LobbyGenerator(SingleSession session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.LEADS);
    }

    @Override
    public void updatePreferences() {
        profile.setSetting("blockLead", "4");
        profile.setSetting("useSchematic", "false");
    }

    @Override
    public void generate() {
        super.generate();

        data.blocks().get(0).setType(Material.SMOOTH_QUARTZ);
    }

    // remove -2 height jumps
    @Override
    public void calculateHeight() {
        heightChances.clear();

        int percentage = 0;
        for (int i = 0; i < Option.NORMAL_UP; i++) {
            heightChances.put(percentage, 1);
            percentage++;
        }
        for (int i = 0; i < Option.NORMAL_LEVEL; i++) {
            heightChances.put(percentage, 0);
            percentage++;
        }
        for (int i = 0; i < Option.NORMAL_DOWN; i++) {
            heightChances.put(percentage, -1);
            percentage++;
        }
    }

    // remove 1 block jumps
    @Override
    public void calculateDistance() {
        distanceChances.clear();

        int two = Option.NORMAL_TWO_BLOCK;
        int three = Option.NORMAL_THREE_BLOCK;
        int four = Option.NORMAL_FOUR_BLOCK;

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