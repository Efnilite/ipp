package dev.efnilite.ipp.config;

import dev.efnilite.ipp.IPP;
import dev.efnilite.vilib.config.ConfigOption;
import org.bukkit.configuration.file.FileConfiguration;

public class PlusOption {

    public static boolean SEND_BACK_AFTER_MULTIPLAYER;

    public static void init() {
        FileConfiguration config = IPP.getConfiguration().getFile("config");

        SEND_BACK_AFTER_MULTIPLAYER = config.getBoolean("send-back-after-multiplayer");
    }
}