package dev.efnilite.ipp;

import dev.efnilite.ip.api.Registry;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ipp.config.PlusConfig;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.mode.PlusMode;
import dev.efnilite.ipp.mode.multi.DuelsMode;
import dev.efnilite.ipp.mode.multi.TeamSurvivalMode;
import dev.efnilite.ipp.mode.single.*;
import dev.efnilite.ipp.generator.single.PracticeGenerator;
import dev.efnilite.ipp.menu.ActiveMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.ipp.style.IncrementalStyle;
import dev.efnilite.ipp.util.PlusHandler;
import dev.efnilite.ipp.util.UpdateChecker;
import dev.efnilite.vilib.ViPlugin;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.elevator.GitElevator;
import dev.efnilite.vilib.util.elevator.VersionComparator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public final class IPP extends ViPlugin {

    private static IPP instance;
    private static PlusConfig configuration;
    public static final String REQUIRED_VILIB_VERSION = "1.1.0";
    public static final String REQUIRED_IP_VERSION = "4.1.1";

    public static final String PREFIX = "<gradient:#ff5050:#ff66cc>Infinite Parkour+<reset><gray> ";

    @Override
    public void enable() {
        instance = this;

        Plugin vilib = getServer().getPluginManager().getPlugin("vilib");
        if (vilib == null || !vilib.isEnabled()) {
            getLogger().severe("##");
            getLogger().severe("## Infinite Parkour+ requires vilib to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here:");
            getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!VersionComparator.FROM_SEMANTIC.isLatest(REQUIRED_VILIB_VERSION, vilib.getDescription().getVersion())) {
            getLogger().severe("##");
            getLogger().severe("## Infinite Parkour+ requires *a newer version* of vilib to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here: ");
            getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Plugin ip = getServer().getPluginManager().getPlugin("IP");
        if (ip == null || !ip.isEnabled()) {
            getLogger().severe("##");
            getLogger().severe("## Infinite Parkour+ requires Infinite Parkour to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here:");
            getLogger().severe("## https://github.com/Efnilite/Walk-in-the-Park/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!VersionComparator.FROM_SEMANTIC.isLatest(REQUIRED_IP_VERSION, ip.getDescription().getVersion())) {
            getLogger().severe("##");
            getLogger().severe("## Infinite Parkour+ requires *a newer version* of Infinite Parkour to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here: ");
            getLogger().severe("## https://github.com/Efnilite/Walk-in-the-Park/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Time.timerStart("enable ipp");

        configuration = new PlusConfig(this);
        PlusConfigOption.init();
        PlusLocales.init(this);

        // Events
        registerListener(new PlusHandler());
        registerCommand("ipp", new PlusCommand());

        // Gamemode register
        registerMode(new PracticeMode());
        registerMode(new TeamSurvivalMode());
        registerMode(new LobbyMode());
        registerMode(new SpeedMode());
        registerMode(new SuperJumpMode());
        registerMode(new HourglassMode());
        registerMode(new TimeTrialMode());
        registerMode(new DuelsMode());

        registerMode(new WaveTrialMode());

        // Style register
        Registry.register(new IncrementalStyle());
        Registry.getStyleType("incremental").addConfigStyles("styles.incremental.list", configuration.getFile("config"));

        dev.efnilite.ipp.mode.lobby.LobbyMode.read();
        PlusMode.init();

        // Register stuff for main menu
        // Multiplayer if player is not found
        Menus.PLAY.registerMainItem(1, 1,
                (player, user) -> PlusLocales.getItem(player, "play.multi.item")
                        .click(event -> MultiplayerMenu.open(event.getPlayer())),
                PlusOption.MULTIPLAYER::mayPerform);

        // practice settings only if player's generator is of this instance
        Menus.SETTINGS.registerMainItem(1, 3,
                (player, user) -> PlusLocales.getItem(player, "play.single.practice.items.settings").click(
                        event -> {
                            ParkourPlayer pp = ParkourPlayer.getPlayer(event.getPlayer());
                            if (pp != null && pp.generator instanceof PracticeGenerator generator) {
                                generator.open();
                            }
                        }),
                player -> {
                    ParkourPlayer pp = ParkourPlayer.getPlayer(player);
                    return PlusOption.PRACTICE_SETTINGS.mayPerform(player) &&
                            pp != null &&
                            pp.generator instanceof PracticeGenerator;
                });

        Menus.LOBBY.registerMainItem(1, 2,
                (player, user) -> PlusLocales.getItem(player, "invite.item")
                        .click(event -> InviteMenu.open(event.getPlayer())),
                player -> {
                    ParkourUser user = ParkourUser.getUser(player);

                    // only show is user is parkourplayer and first player in session (the owner)
                    return PlusOption.INVITE.mayPerform(player) &&
                            user instanceof ParkourPlayer &&
                            user.session instanceof MultiSession &&
                            user.session.getPlayers().get(0) == user;
                });

        Menus.COMMUNITY.registerMainItem(1, 1,
                (player, user) -> PlusLocales.getItem(player, "active.item")
                        .click(event -> ActiveMenu.open(event.getPlayer(), ActiveMenu.MenuSort.LEAST_OPEN_FIRST)),
                player -> PlusOption.ACTIVE.mayPerform(player) && Option.JOINING);

        if (PlusConfigOption.UPDATE_CHECKER) {
            Task.create(this)
                    .async()
                    .execute(() -> UpdateChecker.check(this))
                    .delay(5 * 20)
                    .repeat(8 * 60 * 60 * 20)
                    .run();
        }

        logging().info("Loaded Infinite Parkour Plus in " + Time.timerEnd("enable ipp") + "ms!");
    }

    @Override
    public void disable() {
        // save all gamemodes
        if (PlusMode.TIME_TRIAL != null) {
            PlusMode.TIME_TRIAL.getLeaderboard().write(false);
        }
        if (PlusMode.SUPER_JUMP != null) {
            PlusMode.SUPER_JUMP.getLeaderboard().write(false);
        }
        if (PlusMode.HOURGLASS != null) {
            PlusMode.HOURGLASS.getLeaderboard().write(false);
        }
        if (PlusMode.SPEED != null) {
            PlusMode.SPEED.getLeaderboard().write(false);
        }
        if (PlusMode.WAVE_TRIAL != null) {
            PlusMode.WAVE_TRIAL.getLeaderboard().write(false);
        }
        if (PlusMode.TEAM_SURVIVAL != null) {
            PlusMode.TEAM_SURVIVAL.getLeaderboard().write(false);
        }
    }

    @Override
    public @Nullable GitElevator getElevator() {
        return null;
    }

    private void registerMode(Mode gamemode) {
        if (configuration.getFile("config").getBoolean("gamemodes.%s.enabled".formatted(gamemode.getName().toLowerCase()))) {
            Registry.register(gamemode);
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