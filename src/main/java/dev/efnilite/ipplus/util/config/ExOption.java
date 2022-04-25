package dev.efnilite.ipplus.util.config;

import dev.efnilite.ipplus.IPPlus;
import dev.efnilite.vilib.config.ConfigOption;
import org.bukkit.configuration.file.FileConfiguration;

public class ExOption {

    public static ConfigOption<Boolean> SEND_BACK_AFTER_MULTIPLAYER;

    public static void init() {
        FileConfiguration config = IPPlus.getConfiguration().getFile("config");

        SEND_BACK_AFTER_MULTIPLAYER = new ConfigOption<>(config, "send-back-after-multiplayer");
    }
}