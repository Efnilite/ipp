package dev.efnilite.ipplus;

import dev.efnilite.ip.api.ParkourAPI;
import dev.efnilite.ipplus.gamemode.*;
import dev.efnilite.ipplus.mode.LobbyArea;
import dev.efnilite.ipplus.style.IncrementalStyle;
import dev.efnilite.ipplus.util.config.ExConfiguration;
import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Time;

import java.io.File;

public final class IPPlus extends ViPlugin {

    private static LobbyArea cuboidArea;
    private static IPPlus instance;
    private static ExConfiguration configuration;

    @Override
    public void enable() {
        instance = this;
        Time.timerStart("enable");

        configuration = new ExConfiguration(this);

        // Events
        registerListener(new PlusHandler());
        registerCommand("ipex", new PlusCommand());

        // Gamemode register
        ParkourAPI.getRegistry().register(new PracticeGamemode());
        ParkourAPI.getRegistry().register(new TeamSurvivalGamemode());
        ParkourAPI.getRegistry().register(new SpeedGamemode());
        ParkourAPI.getRegistry().register(new SpeedJumpGamemode());
        ParkourAPI.getRegistry().register(new HourglassGamemode());
        ParkourAPI.getRegistry().register(new TimeTrialGamemode());
        ParkourAPI.getRegistry().register(new DuelGamemode());

        ParkourAPI.getRegistry().registerType(new IncrementalStyle());
        ParkourAPI.getRegistry().getStyleType("incremental").addConfigStyles("styles.incremental.list", configuration.getFile("config"));

        cuboidArea = new LobbyArea();

        Logging.info("Loaded Infinite Parkour Plus in " + Time.timerEnd("enable") + "ms!");
    }

    @Override
    public void disable() {

    }

    public static void setCuboidArea(LobbyArea cuboidArea) {
        IPPlus.cuboidArea = cuboidArea;
    }

    public static LobbyArea getCuboidArea() {
        return cuboidArea;
    }

    public static IPPlus getInstance() {
        return instance;
    }

    public static ExConfiguration getConfiguration() {
        return configuration;
    }

    public static File getFolder() {
        return instance.getDataFolder();
    }
}