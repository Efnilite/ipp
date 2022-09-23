package dev.efnilite.ipp.config;

import dev.efnilite.ipp.IPP;
import org.bukkit.configuration.file.FileConfiguration;

public class PlusConfigOption {

    public static boolean UPDATE_CHECKER;
    public static boolean SEND_BACK_AFTER_MULTIPLAYER;
    public static int DUELS_ISLAND_DISTANCE;

    public static void init() {
        FileConfiguration config = IPP.getConfiguration().getFile("config");

        UPDATE_CHECKER = config.getBoolean("update_checker");
        SEND_BACK_AFTER_MULTIPLAYER = config.getBoolean("send_back_after_multiplayer");
        DUELS_ISLAND_DISTANCE = config.getInt("gamemodes.duels.island_distance");
        if (DUELS_ISLAND_DISTANCE < 1) {
            IPP.logging().stack("Invalid duels island distance dimension! ", DUELS_ISLAND_DISTANCE + " is not supported. The value must be above 1.", new IllegalArgumentException());
        }
    }
}