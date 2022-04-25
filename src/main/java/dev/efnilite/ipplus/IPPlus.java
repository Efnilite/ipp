package dev.efnilite.ipplus;

import dev.efnilite.ip.api.ParkourAPI;
import dev.efnilite.ip.menu.MainMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipplus.gamemode.*;
import dev.efnilite.ipplus.menu.SessionMenu;
import dev.efnilite.ipplus.style.IncrementalStyle;
import dev.efnilite.ipplus.util.config.ExConfiguration;
import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Time;
import org.bukkit.Material;

import java.io.File;

public final class IPPlus extends ViPlugin {

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

        // Style register
        ParkourAPI.getRegistry().registerType(new IncrementalStyle());
        ParkourAPI.getRegistry().getStyleType("incremental").addConfigStyles("styles.incremental.list", configuration.getFile("config"));

        // Register stuff for main menu
        // Multiplayer if player is not found
        MainMenu.registerMainItem(1, 1, new Item(Material.OAK_BOAT, "<#0088CB><bold>Multiplayer")
                .lore("<gray>Play with other players.").click(
                event -> SessionMenu.open(event.getPlayer())),
                player -> !ParkourPlayer.isActive(player) && PlusOption.MULTIPLAYER.check(player));

        Logging.info("Loaded Infinite Parkour Plus in " + Time.timerEnd("enable") + "ms!");
    }

    @Override
    public void disable() {

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