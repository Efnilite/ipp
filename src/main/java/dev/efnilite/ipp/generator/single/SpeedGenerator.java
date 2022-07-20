package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Class for multiplayer
 */
public final class SpeedGenerator extends PlusGenerator {

    public SpeedGenerator(Session session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.REDUCE_RANDOM_BLOCK_SELECTION_ANGLE);

        // setup menu
        menu = new SettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.SPECIAL_BLOCKS);

        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 2, false, false));

        distanceChances.clear();
        distanceChances.put(0, 2);

        heightChances.clear();
        heightChances.put(0, 0);
    }

    @Override
    protected void calculateDistance() {
        // do nothing
    }

    @Override
    protected void calculateHeight() {
        // do nothing
    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.SPEED;
    }
}