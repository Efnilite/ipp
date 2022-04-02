package dev.efnilite.ipex.generator;

import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.generator.base.GeneratorOption;
import dev.efnilite.witp.player.ParkourPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Generator for Lobby mode
 */
public class LobbyGenerator extends DefaultGenerator {

    public LobbyGenerator(@NotNull ParkourPlayer player) {
        super(player, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);

        borderWarning = 10;
    }
}
