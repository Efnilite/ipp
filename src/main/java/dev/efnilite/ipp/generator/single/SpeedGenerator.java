package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Class for multiplayer
 */
public final class SpeedGenerator extends PlusGenerator {

    public SpeedGenerator(Session session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.REDUCE_RANDOM_BLOCK_SELECTION_ANGLE);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC, ParkourOption.SPECIAL_BLOCKS);

        player.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 2, false, false));

        distanceChances.clear();
        distanceChances.put(2, 1.0);

        heightChances.clear();
        heightChances.put(0, 1.0);
    }

    @Override
    public void overrideProfile() {
        profile.set("blockLead", "10");
    }

    @Override
    public Mode getMode() {
        return PlusMode.SPEED;
    }
}