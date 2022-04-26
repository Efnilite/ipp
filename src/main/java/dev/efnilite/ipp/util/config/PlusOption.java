package dev.efnilite.ipp.util.config;

import dev.efnilite.ipp.IPP;
import dev.efnilite.vilib.config.ConfigOption;
import org.bukkit.configuration.file.FileConfiguration;

public class PlusOption {

    public static ConfigOption<Boolean> SEND_BACK_AFTER_MULTIPLAYER;

    public static void init() {
        FileConfiguration config = IPP.getConfiguration().getFile("config");

        SEND_BACK_AFTER_MULTIPLAYER = new ConfigOption<>(config, "send-back-after-multiplayer");
    }
}