package dev.efnilite.ipplus;

import dev.efnilite.ip.api.ParkourAPI;
import dev.efnilite.ip.menu.MainMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ipplus.gamemode.*;
import dev.efnilite.ipplus.menu.MultiplayerMenu;
import dev.efnilite.ipplus.mode.LobbyMode;
import dev.efnilite.ipplus.style.IncrementalStyle;
import dev.efnilite.ipplus.util.config.ExConfiguration;
import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Time;
import org.bukkit.Material;

import java.io.File;

public final class IPP extends ViPlugin {

    private static IPP instance;
    private static ExConfiguration configuration;

    @Override
    public void enable() {
        instance = this;
        Time.timerStart("enable");

        configuration = new ExConfiguration(this);

        // Events
        registerListener(new PlusHandler());
        registerCommand("ipp", new PlusCommand());

        // Gamemode register
        ParkourAPI.getRegistry().register(new PracticeGamemode());
        ParkourAPI.getRegistry().register(new TeamSurvivalGamemode());
        ParkourAPI.getRegistry().register(new LobbyGamemode());
        ParkourAPI.getRegistry().register(new SpeedGamemode());
        ParkourAPI.getRegistry().register(new SpeedJumpGamemode());
        ParkourAPI.getRegistry().register(new HourglassGamemode());
        ParkourAPI.getRegistry().register(new TimeTrialGamemode());
        ParkourAPI.getRegistry().register(new DuelGamemode());

        // Style register
        ParkourAPI.getRegistry().registerType(new IncrementalStyle());
        ParkourAPI.getRegistry().getStyleType("incremental").addConfigStyles("styles.incremental.list", configuration.getFile("config"));


        LobbyMode.read();

        // Register stuff for main menu
        // Multiplayer if player is not found
        MainMenu.registerMainItem(1, 1, new Item(Material.OAK_BOAT, "<#0088CB><bold>Multiplayer")
                .lore("<gray>Play with other players.").click(
                event -> MultiplayerMenu.open(event.getPlayer())),
                player -> !ParkourPlayer.isActive(player) && PlusOption.MULTIPLAYER.check(player));

        logging().info("Loaded Infinite Parkour Plus in " + Time.timerEnd("enable") + "ms!");
    }

    @Override
    public void disable() {

    }

    /**
     * Returns the {@link Logging} belonging to this plugin.
     *
     * @return this plugin's {@link Logging} instance.
     */
    public static Logging logging() {
        return getPlugin().logging;
    }

    /**
     * Returns this plugin instance.
     *
     * @return the plugin instance.
     */
    public static IPP getPlugin() {
        return instance;
    }

    public static ExConfiguration getConfiguration() {
        return configuration;
    }

    public static File getFolder() {
        return instance.getDataFolder();
    }
}