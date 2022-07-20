package dev.efnilite.ipp;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.menu.DynamicMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.gamemode.multi.DuelGamemode;
import dev.efnilite.ipp.gamemode.multi.TeamSurvivalGamemode;
import dev.efnilite.ipp.gamemode.single.*;
import dev.efnilite.ipp.menu.CreationMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.LobbyMenu;
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
        dev.efnilite.ipp.util.config.PlusOption.init();

        // Events
        registerListener(new PlusHandler());
        registerCommand("ipp", new PlusCommand());

        // Gamemode register
        IP.getRegistry().register(new PracticeGamemode());
        IP.getRegistry().register(new TeamSurvivalGamemode());
        IP.getRegistry().register(new LobbyGamemode());
        IP.getRegistry().register(new SpeedGamemode());
        IP.getRegistry().register(new SpeedJumpGamemode());
        IP.getRegistry().register(new HourglassGamemode());
        IP.getRegistry().register(new TimeTrialGamemode());
        IP.getRegistry().register(new DuelGamemode());

        // Style register
        IP.getRegistry().registerType(new IncrementalStyle());
        IP.getRegistry().getStyleType("incremental").addConfigStyles("styles.incremental.list", configuration.getFile("config"));

        LobbyMode.read();
        PlusGamemodes.init();

        // Register stuff for main menu
        // Multiplayer if player is not found
        DynamicMenu.Reg.MAIN.registerMainItem(1, 1,
                user -> new Item(Material.OAK_BOAT, "<#0088CB><bold>Multiplayer").lore("<dark_gray>多人遊戲 • マルチプレイヤー").click(
                event -> LobbyMenu.open(event.getPlayer())),
                PlusOption.MULTIPLAYER::check);

        DynamicMenu.Reg.MAIN.registerMainItem(1, 8,
                user -> new Item(Material.ELYTRA, "<#ECE228><bold>Invite").click(
                event -> InviteMenu.open(event.getPlayer())),
                player -> {
                    ParkourUser user = ParkourUser.getUser(player);
                    // only show is user is parkourplayer and first player in session (the owner)
                    return user instanceof ParkourPlayer && user.getSession() instanceof MultiSession
                            && user.getSession().getPlayers().get(0) == user;
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