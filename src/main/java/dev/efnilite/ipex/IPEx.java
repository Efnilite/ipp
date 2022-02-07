package dev.efnilite.ipex;

import dev.efnilite.fycore.FyPlugin;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.util.Timer;
import dev.efnilite.ipex.gamemode.*;
import dev.efnilite.ipex.util.CuboidArea;
import dev.efnilite.ipex.util.config.ExConfiguration;
import dev.efnilite.ipex.util.config.ExOption;
import dev.efnilite.witp.api.WITPAPI;

import java.io.File;

public final class IPEx extends FyPlugin {

    private static CuboidArea cuboidArea;
    private static IPEx instance;
    private static ExConfiguration configuration;

    @Override
    public void enable() {
        instance = this;
        Timer.start("enable");


        configuration = new ExConfiguration(this);

        // Events
        registerListener(new ExHandler());
        registerCommand("ipex", IPExCommand.class);

        // Gamemode register
        WITPAPI.getRegistry().register(new PracticeGamemode());
        WITPAPI.getRegistry().register(new TeamSurvivalGamemode());
        WITPAPI.getRegistry().register(new SpeedGamemode());
        WITPAPI.getRegistry().register(new SpeedJumpGamemode());
        WITPAPI.getRegistry().register(new HourglassGamemode());
        WITPAPI.getRegistry().register(new TimeTrialGamemode());
        WITPAPI.getRegistry().register(new DuelGamemode());


        File file = new File(IPEx.getInstance().getDataFolder() + "/data/", "cuboid.json");
        if (file.exists() && ExOption.CUBOID_MODE.get()) {
            cuboidArea = new CuboidArea();
        }

        Logging.info("Loaded WITPEx in " + Timer.end("enable") + "ms!");
    }

    @Override
    public void disable() {

    }

    public static void setCuboidArea(CuboidArea cuboidArea) {
        IPEx.cuboidArea = cuboidArea;
    }

    public static CuboidArea getCuboidArea() {
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