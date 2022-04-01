package dev.efnilite.ipex;

import dev.efnilite.fycore.FyPlugin;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.util.Time;
import dev.efnilite.ipex.gamemode.*;
import dev.efnilite.ipex.mode.LobbyArea;
import dev.efnilite.ipex.util.config.ExConfiguration;
import dev.efnilite.witp.api.ParkourAPI;

import java.io.File;

public final class IPEx extends FyPlugin {

    private static LobbyArea cuboidArea;
    private static IPEx instance;
    private static ExConfiguration configuration;

    @Override
    public void enable() {
        instance = this;
        Time.timerStart("enable");

        configuration = new ExConfiguration(this);

        // Events
        registerListener(new ExHandler());
        registerCommand("ipex", new ExCommand());

        // Gamemode register
        ParkourAPI.getRegistry().register(new PracticeGamemode());
        ParkourAPI.getRegistry().register(new TeamSurvivalGamemode());
        ParkourAPI.getRegistry().register(new SpeedGamemode());
        ParkourAPI.getRegistry().register(new SpeedJumpGamemode());
        ParkourAPI.getRegistry().register(new HourglassGamemode());
        ParkourAPI.getRegistry().register(new TimeTrialGamemode());
        ParkourAPI.getRegistry().register(new DuelGamemode());

        cuboidArea = new LobbyArea();

        Logging.info("Loaded WITPEx in " + Time.timerEnd("enable") + "ms!");
    }

    @Override
    public void disable() {

    }

    public static void setCuboidArea(LobbyArea cuboidArea) {
        IPEx.cuboidArea = cuboidArea;
    }

    public static LobbyArea getCuboidArea() {
        return cuboidArea;
    }

    public static IPEx getInstance() {
        return instance;
    }

    public static ExConfiguration getConfiguration() {
        return configuration;
    }

    public static File getFolder() {
        return instance.getDataFolder();
    }
}