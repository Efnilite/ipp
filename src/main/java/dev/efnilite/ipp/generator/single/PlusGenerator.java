package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.generator.ParkourGenerator;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.schematic.Schematic;
import dev.efnilite.ip.session.Session;
import org.jetbrains.annotations.NotNull;

/**
 * DefaultGenerator wrap for IP+, with
 * - common variables
 * - common methods.
 */
public abstract class PlusGenerator extends ParkourGenerator {

    protected ParkourSettingsMenu menu;

    public PlusGenerator(@NotNull Session session, GeneratorOption... generatorOptions) {
        super(session, generatorOptions);
    }

    public PlusGenerator(@NotNull Session session, @NotNull Schematic schematic, GeneratorOption... generatorOptions) {
        super(session, schematic, generatorOptions);
    }

    @Override
    public void menu(ParkourPlayer player) {
        menu.open(player);
    }
}
