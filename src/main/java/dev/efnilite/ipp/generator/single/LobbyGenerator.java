package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Material;

/**
 * Generator for Lobby mode
 */
public final class LobbyGenerator extends PlusGenerator {

    public LobbyGenerator(Session session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC, ParkourOption.LEADS);

        // remove -2 jumps
        heightChances.clear();
        heightChances.put(1, Option.NORMAL_UP);
        heightChances.put(0, Option.NORMAL_LEVEL);
        heightChances.put(-1, Option.NORMAL_DOWN);

        // remove 1 block jumps
        distanceChances.clear();
        distanceChances.put(2, Option.NORMAL_TWO_BLOCK);
        distanceChances.put(3, Option.NORMAL_THREE_BLOCK);
        distanceChances.put(4, Option.NORMAL_FOUR_BLOCK);
    }

    @Override
    public void overrideProfile() {
        profile.set("blockLead", "4");
        profile.set("useSchematic", "false");
    }

    @Override
    public void generate() {
        super.generate();

        island.blocks.get(0).setType(Material.SMOOTH_QUARTZ);
    }

    @Override
    public Mode getMode() {
        return PlusMode.LOBBY;
    }
}