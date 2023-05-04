package dev.efnilite.ipp.config;

import dev.efnilite.ipp.IPP;
import org.bukkit.configuration.file.FileConfiguration;

public class PlusConfigOption {

    public static boolean UPDATE_CHECKER;
    public static boolean SEND_BACK_AFTER_MULTIPLAYER;
    public static int DUELS_ISLAND_DISTANCE;
    public static int DUELS_MAX_COUNT;
    public static int HOURGLASS_TIME;
    public static int TEAM_SURVIVAL_MAX_COUNT;

    public static void init() {
        FileConfiguration config = IPP.getConfiguration().getFile("config");

        UPDATE_CHECKER = config.getBoolean("update_checker");
        SEND_BACK_AFTER_MULTIPLAYER = config.getBoolean("send_back_after_multiplayer");
        DUELS_ISLAND_DISTANCE = config.getInt("gamemodes.duels.island_distance");

        if (DUELS_ISLAND_DISTANCE < 1) {
            DUELS_ISLAND_DISTANCE = 10;

            IPP.logging().stack("Invalid duels island distance dimension! ", "%d is not supported. The value must be above 1.".formatted(DUELS_ISLAND_DISTANCE), new IllegalArgumentException());
        }

        HOURGLASS_TIME = config.getInt("gamemodes.hourglass.time");
        DUELS_MAX_COUNT = config.getInt("gamemodes.duels.max");
        TEAM_SURVIVAL_MAX_COUNT = config.getInt("gamemodes.team_survival.max");
    }
}