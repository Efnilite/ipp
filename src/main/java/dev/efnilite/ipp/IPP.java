package dev.efnilite.ipp;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ipp.config.Locales;
import dev.efnilite.ipp.config.PlusConfig;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.gamemode.multi.DuelsGamemode;
import dev.efnilite.ipp.gamemode.multi.TeamSurvivalGamemode;
import dev.efnilite.ipp.gamemode.single.*;
import dev.efnilite.ipp.generator.single.PracticeGenerator;
import dev.efnilite.ipp.menu.ActiveMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.mode.LobbyMode;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.ipp.style.IncrementalStyle;
import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.elevator.GitElevator;
import org.jetbrains.annotations.Nullable;

public final class IPP extends ViPlugin {

    private static IPP instance;
    private static PlusConfig configuration;

    @Override
    public void enable() {
        instance = this;
        Time.timerStart("enable ipp");

        configuration = new PlusConfig(this);
        PlusConfigOption.init();

        // Events
        registerListener(new PlusHandler());
        registerCommand("ipp", new PlusCommand());

        // Gamemode register
        registerGamemode(new PracticeGamemode());
        registerGamemode(new TeamSurvivalGamemode());
        registerGamemode(new LobbyGamemode());
        registerGamemode(new SpeedGamemode());
        registerGamemode(new SuperJumpGamemode());
        registerGamemode(new HourglassGamemode());
        registerGamemode(new TimeTrialGamemode());
        registerGamemode(new DuelsGamemode());

        // Style register
        IP.getRegistry().registerType(new IncrementalStyle());
        IP.getRegistry().getStyleType("incremental").addConfigStyles("styles.incremental.list", configuration.getFile("config"));

        LobbyMode.read();
        PlusGamemodes.init();
        Locales.init(this);

        // Register stuff for main menu
        // Multiplayer if player is not found
        Menus.PLAY.registerMainItem(1, 1,
                user -> Locales.getItem(user == null ? Option.DEFAULT_LOCALE : user.getPlayer().getLocale(), "multiplayer.item")
                        .click(event -> MultiplayerMenu.open(event.getPlayer())),
                player -> true);

        // practice settings only if player's generator is of this instance
        Menus.SETTINGS.registerMainItem(1, 3,
                user -> Locales.getItem(user.getPlayer(), "singleplayer.practice.items.settings").click(
                        event -> {
                            ParkourPlayer pp = ParkourPlayer.getPlayer(event.getPlayer());
                            if (pp != null && pp.getGenerator() instanceof PracticeGenerator generator) {
                                generator.open();
                            }
                        }),
                player -> {
                    ParkourPlayer pp = ParkourPlayer.getPlayer(player);
                    return pp != null && pp.getGenerator() instanceof PracticeGenerator;
                });

        Menus.LOBBY.registerMainItem(1, 2,
                user -> Locales.getItem(user.getPlayer(), "invite.item").click(
                        event -> InviteMenu.open(event.getPlayer())),
                player -> {
                    ParkourUser user = ParkourUser.getUser(player);

                    // only show is user is parkourplayer and first player in session (the owner)
                    return user instanceof ParkourPlayer && user.getSession() instanceof MultiSession && user.getSession().getPlayers().get(0) == user;
                });

        Menus.COMMUNITY.registerMainItem(1, 1,
                user -> Locales.getItem(user == null ? Option.DEFAULT_LOCALE : user.getPlayer().getLocale(), "active.item")
                    .click(event -> ActiveMenu.open(event.getPlayer(), ActiveMenu.MenuSort.LEAST_OPEN_FIRST)),
                player -> true);

        logging().info("Loaded Infinite Parkour Plus in " + Time.timerEnd("enable ipp") + "ms!");
    }

    @Override
    public void disable() {
        // save all gamemodes
        PlusGamemodes.TIME_TRIAL.getLeaderboard().write(false);
        PlusGamemodes.SUPER_JUMP.getLeaderboard().write(false);
        PlusGamemodes.HOURGLASS.getLeaderboard().write(false);
        PlusGamemodes.SPEED.getLeaderboard().write(false);

        PlusGamemodes.DUELS.getLeaderboard().write(false);
        PlusGamemodes.TEAM_SURVIVAL.getLeaderboard().write(false);
    }

    @Override
    public @Nullable GitElevator getElevator() {
        return null;
    }

    private void registerGamemode(Gamemode gamemode) {
        if (configuration.getFile("config").getBoolean("gamemodes." + gamemode.getName().toLowerCase() + ".enabled")) {
            IP.getRegistry().register(gamemode);
        }
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

    public static PlusConfig getConfiguration() {
        return configuration;
    }
}