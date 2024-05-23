package dev.efnilite.ipp;

import dev.efnilite.ip.api.Registry;
import dev.efnilite.ip.config.Config;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.lib.vilib.ViPlugin;
import dev.efnilite.ip.lib.vilib.util.Logging;
import dev.efnilite.ip.lib.vilib.util.UpdateChecker;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ipp.config.PlusConfig;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.single.PracticeGenerator;
import dev.efnilite.ipp.menu.ActiveMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.mode.PlusMode;
import dev.efnilite.ipp.mode.lobby.Lobby;
import dev.efnilite.ipp.mode.multi.DuelsMode;
import dev.efnilite.ipp.mode.multi.TeamSurvivalMode;
import dev.efnilite.ipp.mode.single.*;
import dev.efnilite.ipp.style.IncrementalStyle;
import org.bukkit.plugin.Plugin;

public final class IPP extends ViPlugin {

    // TODO UPDATE THIS!
    public static final String REQUIRED_IP_VERSION = "5.2.5";
    public static final String PREFIX = "<gradient:#ff5050:#ff66cc>Infinite Parkour+<reset><gray> ";
    private static IPP instance;
    private static Logging logging;
    private static PlusConfig configuration;

    /**
     * Returns the {@link Logging} belonging to this plugin.
     *
     * @return this plugin's {@link Logging} instance.
     */
    public static Logging logging() {
        return logging;
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

    @Override
    public void enable() {
        instance = this;
        logging = new Logging(this);

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

        if (isLower(ip.getDescription().getVersion(), REQUIRED_IP_VERSION)) {
            getLogger().severe("##");
            getLogger().severe("## Infinite Parkour+ requires *a newer version* of Infinite Parkour to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here: ");
            getLogger().severe("## https://github.com/Efnilite/Walk-in-the-Park/releases/latest");
            getLogger().severe("##");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configuration = new PlusConfig(this);
        configuration.reload();

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

        Lobby.read();
        PlusMode.init();

        // Register stuff for main menu
        // Multiplayer if player is not found
        Menus.PLAY.registerMainItem(1, 5,
                (player, user) -> PlusLocales.getItem(player, "play.multi.item")
                        .click(event -> MultiplayerMenu.open(event.getPlayer())),
                PlusOption.MULTIPLAYER::mayPerform);

        // practice settings only if player's generator is of this instance
        Menus.SETTINGS.registerMainItem(1, 3,
                (player, user) -> PlusLocales.getItem(player, "play.single.practice.items.settings").click(
                        event -> {
                            ParkourPlayer pp = ParkourPlayer.getPlayer(event.getPlayer());
                            if (pp != null && pp.session.generator instanceof PracticeGenerator generator) {
                                generator.open();
                            }
                        }),
                player -> {
                    ParkourPlayer pp = ParkourPlayer.getPlayer(player);
                    return PlusOption.PRACTICE_SETTINGS.mayPerform(player) &&
                            pp != null &&
                            pp.session.generator instanceof PracticeGenerator;
                });

        Menus.LOBBY.registerMainItem(1, 2,
                (player, user) -> PlusLocales.getItem(player, "invite.item")
                        .click(event -> InviteMenu.open(event.getPlayer())),
                player -> {
                    ParkourUser user = ParkourUser.getUser(player);

                    // only show is user is parkourplayer and first player in session (the owner)
                    return PlusOption.INVITE.mayPerform(player) &&
                            user instanceof ParkourPlayer &&
                            user.session.generator.getMode() instanceof MultiMode &&
                            user.session.getPlayers().get(0) == user;
                });

        Menus.COMMUNITY.registerMainItem(1, 1,
                (player, user) -> PlusLocales.getItem(player, "active.item")
                        .click(event -> ActiveMenu.open(event.getPlayer(), ActiveMenu.MenuSort.LEAST_OPEN_FIRST)),
                player -> PlusOption.ACTIVE.mayPerform(player) && Config.CONFIG.getBoolean("joining"));

        if (configuration.getFile("config").getBoolean("styles.incremental.enabled")) {
            Option.initStyles("styles.incremental.list", configuration.getFile("config"), IncrementalStyle::new)
                    .forEach(Registry::register);
        }

        UpdateChecker.check(this, 105019);
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

    private void registerMode(Mode gamemode) {
        if (configuration.getFile("config").getBoolean("gamemodes.%s.enabled".formatted(gamemode.getName().toLowerCase()))) {
            Registry.register(gamemode);
        }
    }

    // returns true if current mc version is lower than compare
    private boolean isLower(String current, String compare) {
        var cur = getNumbers(current); // 1.19.4
        var com = getNumbers(compare); // 2.1.5

        if (com[0] > cur[0]) {
            return false;
        } else if (com[0] == cur[0]) {
            if (com[1] > cur[1]) {
                return false;
            } else if (com[1] == cur[1]) {
                return com[2] > cur[2];
            }
        }

        return true;
    }

    private int[] getNumbers(String string) {
        var parts = string.split("\\.");

        return new int[] {
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        };
    }
}