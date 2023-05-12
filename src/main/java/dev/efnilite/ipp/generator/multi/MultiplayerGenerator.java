package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.schematic.Schematic;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.generator.single.PlusGenerator;

import java.util.List;

public abstract class MultiplayerGenerator extends PlusGenerator {

    protected ParkourSettingsMenu menu;

    public MultiplayerGenerator(Session session, GeneratorOption... options) {
        super(session, options);
    }

    public MultiplayerGenerator(Session session, Schematic schematic, GeneratorOption... options) {
        super(session, schematic, options);
    }

    @Override
    public void menu(ParkourPlayer player) {
        menu.open(player);
    }
}