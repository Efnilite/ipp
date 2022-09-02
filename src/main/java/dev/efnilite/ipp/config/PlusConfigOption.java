package dev.efnilite.ipp.config;

import dev.efnilite.ipp.IPP;
import org.bukkit.configuration.file.FileConfiguration;

public class PlusConfigOption {

    public static boolean UPDATE_CHECKER;
    public static boolean SEND_BACK_AFTER_MULTIPLAYER;

    public static void init() {
        FileConfiguration config = IPP.getConfiguration().getFile("config");

        UPDATE_CHECKER = config.getBoolean("update_checker");
        SEND_BACK_AFTER_MULTIPLAYER = config.getBoolean("send_back_after_multiplayer");
    }
}