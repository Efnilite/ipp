package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.session.Session;
import org.jetbrains.annotations.NotNull;

/**
 * DefaultGenerator wrap for IP+, with
 * - common variables
 * - common methods.
 */
public abstract class PlusGenerator extends DefaultGenerator {

    protected ParkourSettingsMenu menu;

    public PlusGenerator(@NotNull Session session, GeneratorOption... generatorOptions) {
        super(session, generatorOptions);
    }

    @Override
    public void menu() {
        menu.open(player);
    }
}
