package dev.efnilite.ipp;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.ParkourAPI;
import dev.efnilite.ip.menu.MainMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.gamemode.*;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.mode.LobbyMode;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.ipp.style.IncrementalStyle;
import dev.efnilite.ipp.util.config.PlusConfiguration;
import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Time;
import org.bukkit.Material;

import java.io.File;

public final class IPP extends ViPlugin {

    private static IPP instance;
    private static PlusConfiguration configuration;

    @Override
    public void enable() {
        instance = this;
        Time.timerStart("enable");

        configuration = new PlusConfiguration(this);

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
                .lore(MainMenu.formatSynonyms("多人遊戲 %s マルチプレイヤー")).click(
                event -> MultiplayerMenu.open(event.getPlayer())),
                player -> {
                    ParkourUser user = ParkourUser.getUser(player);
                    // if user is null display item or if the player isn't already playing single player
                    return user == null || !(user instanceof ParkourPlayer) && PlusOption.MULTIPLAYER.check(player)
                            && !(user.getSession() instanceof MultiSession);
                });

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

    public static PlusConfiguration getConfiguration() {
        return configuration;
    }

    public static File getFolder() {
        return instance.getDataFolder();
    }
}