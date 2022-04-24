package dev.efnilite.ipplus.generator;

import dev.efnilite.ip.ParkourMenu;
import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Class for multiplayer
 */
public final class SpeedGenerator extends DefaultGenerator {

    public SpeedGenerator(ParkourPlayer player) {
        super(player, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.REDUCE_RANDOM_BLOCK_SELECTION_ANGLE);

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
    public void menu() {
        ParkourMenu.openMainMenu(player, ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.SPECIAL_BLOCKS);
    }
}