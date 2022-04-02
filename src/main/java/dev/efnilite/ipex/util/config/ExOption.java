package dev.efnilite.ipex.util.config;

import dev.efnilite.fycore.config.ConfigOption;
import dev.efnilite.ipex.IPEx;
import org.bukkit.configuration.file.FileConfiguration;

public class ExOption {

    public static ConfigOption<Boolean> SEND_BACK_AFTER_MULTIPLAYER;

    public static void init() {
        FileConfiguration config = IPEx.getConfiguration().getFile("config");

        SEND_BACK_AFTER_MULTIPLAYER = new ConfigOption<>(config, "send-back-after-multiplayer");
    }
}