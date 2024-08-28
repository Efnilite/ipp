package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.lib.vilib.schematic.Schematic;
import dev.efnilite.ip.lib.vilib.util.Strings;
import dev.efnilite.ip.lib.vilib.util.Task;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.mode.Modes;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.world.Divider;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class DuelsGenerator extends MultiplayerGenerator {

    private static Schematic SCHEMATIC;

    static {
        try {
            SCHEMATIC = Schematic.load(IP.getInFolder("schematics/spawn-island-duels"), IP.getPlugin());
        } catch (IOException | ClassNotFoundException ex) {
            IP.logging().stack("Failed to load spawn island for duels", ex);
        }
    }

    public boolean allowJoining;
    private final ParkourPlayer owner;
    public final Map<ParkourPlayer, SingleDuelsGenerator> playerGenerators = new HashMap<>();
    private final Map<ParkourPlayer, SpawnData> spawnData = new HashMap<>();
    public final int goal = IPP.getConfiguration().getFile("config").getInt("gamemodes.%s.goal".formatted(getMode().getName().toLowerCase()));

    public DuelsGenerator(@NotNull Session session) {
        super(session, (Schematic) null, GeneratorOption.DISABLE_SCHEMATICS);

        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.STYLES, ParkourOption.SPECIAL_BLOCKS);

        allowJoining = true;
        owner = player;

        playerSpawn = session.getSpawnLocation();
        playerSpawn.setYaw(-90);

        addPlayer(player);
        player.setup(null);

        Task.create(IPP.getPlugin()).delay(5)
                .execute(() -> player.player.getInventory().addItem(PlusLocales.getItem(player.locale, "play.multi.duels.start").build()))
                .run();
    }

    public void addPlayer(ParkourPlayer player) {
        // only allow joining if the game hasn't started yet and max players
        if (!allowJoining) {
            return;
        }

        // setup generator
        SingleDuelsGenerator generator = new SingleDuelsGenerator(session);
        generator.player = player;
        generator.owningGenerator = this;

        generator.setPlayerIndex(playerGenerators.size());

        // setup where:
        // - schematic gets pasted
        // - player gets teleported
        // - block starts generating
        Location spawn = playerSpawn.clone().add(0, 0, (SCHEMATIC.getDimensions().getX() + PlusConfigOption.DUELS_ISLAND_DISTANCE) * (getPlayers().size() - 1));
        List<Block> blocks = SCHEMATIC.paste(spawn);

        Location playerSpawn = null;
        Location blockSpawn = null;

        for (Block block : blocks) {
            switch (block.getType()) {
                case EMERALD_BLOCK -> {
                    blockSpawn = block.getLocation().add(0.5, 0, 0.5);
                    block.setType(Material.AIR);
                }
                case DIAMOND_BLOCK -> {
                    playerSpawn = block.getLocation().add(0.5, 0, 0.5);
                    playerSpawn.setYaw(-90);
                    block.setType(Material.AIR);

                    player.teleport(playerSpawn);
                }
            }
        }
        generator.island.blocks = blocks;

        spawnData.put(player, new SpawnData(playerSpawn, blockSpawn));
        playerGenerators.put(player, generator);
        player.updateScoreboard(generator);
    }

    public void removePlayer(ParkourPlayer player) {
        SingleDuelsGenerator generator = playerGenerators.get(player);

        if (generator == null) {
            IP.logging().error("Failed to remove player from duels generator");
            return; // wtf?
        }

        generator.reset(false);

        playerGenerators.remove(player);

        if (allowJoining && player == owner) {
            for (ParkourPlayer other : new HashSet<>(playerGenerators.keySet())) {
                ParkourUser.unregister(other, true, true, false);

                if (!PlusConfigOption.SEND_BACK_AFTER_MULTIPLAYER) {
                    Modes.DEFAULT.create(player.player);
                }
            }
            return;
        }

        // if there are no other players, player automatically wins
        if (allowJoining || playerGenerators.size() != 1) {
            return;
        }

        ParkourPlayer winner = new ArrayList<>(playerGenerators.keySet()).get(0);

        win(winner);
    }

    public void initCountdown() {
        var countdown = new AtomicInteger(10);

        Task.create(IPP.getPlugin()).repeat(20).execute(new BukkitRunnable() {
            @Override
            public void run() {
                if (stopped) {
                    this.cancel();
                    return;
                }

                switch (countdown.get()) {
                    case 0 -> {
                        playerGenerators.forEach((player, generator) -> {
                            String[] args = PlusLocales.getString(player.player, "play.multi.duels.go", false).formatted(goal).split("\\|\\|");

                            sendTitle(player, args[0], args[1], 0, 21, 5);
                            for (Block block : generator.island.blocks) {
                                if (block.getType() == Material.BARRIER) {
                                    block.setType(Material.AIR);
                                }
                            }

                            SpawnData data = DuelsGenerator.this.spawnData.get(player);
                            generator.generateFirst(data.playerSpawn, data.blockSpawn);
                        });

                        stopped = false;
                        startTick();
                        cancel();
                    }
                    case 1 ->
                            playerGenerators.keySet().forEach(player -> sendTitle(player, "<#DA2626><bold>1", "", 0, 21, 0));
                    case 2 ->
                            playerGenerators.keySet().forEach(player -> sendTitle(player, "<#DCD31D><bold>2", "", 0, 21, 0));
                    case 3 ->
                            playerGenerators.keySet().forEach(player -> sendTitle(player, "<#42D929><bold>3", "", 0, 21, 0));
                    default -> {
                        playerGenerators.keySet().forEach(player -> sendTitle(player, "<#23E120><bold>" + countdown.intValue(), "", 0, 21, 0));

                        allowJoining = false;
                    }
                }
                countdown.getAndDecrement();
            }
        }).run();
    }

    @Override
    public void menu(ParkourPlayer player) {
        playerGenerators.get(player).menu(player);
    }

    private void sendTitle(ParkourPlayer pp, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
        pp.player.sendTitle(Strings.colour(title), Strings.colour(subtitle), fadeIn, duration, fadeOut);
    }

    @Override
    public void reset(boolean regenerate) {
        if (regenerate) {
            return;
        }

        playerGenerators.keySet().forEach(this::removePlayer);
    }

    @Override
    public void tick() {
        if (stopped) {
            task.cancel();
            return;
        }

        playerGenerators.forEach((player, generator) -> generator.tick());
    }

    public void win(ParkourPlayer winner) {
        // prevent winning code from being activated more than once
        // could be possible in #removePlayer
        if (stopped) {
            return;
        }

        SingleDuelsGenerator winningGenerator = playerGenerators.get(winner);
        String winningName = winner.getName();
        String winningTime = winningGenerator.getFormattedTime();

        List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> leaderboard = getLeaderboard();

        playerGenerators.forEach((player, generator) -> {
            generator.stopped = true;

            String[] args = PlusLocales.getString(player.player, "play.multi.duels.overview", false).formatted(winningName, winningTime).split("\\|\\|");

            player.send("");
            player.send(args[0]);
            player.send(args[1]);
            player.send("");

            for (int i = 0; i < leaderboard.size(); i++) {
                Map.Entry<ParkourPlayer, SingleDuelsGenerator> entry = leaderboard.get(i);

                player.send("""
                        <#0072B3>#%d <gray>%s <dark_gray>- <gray>%d
                        """.formatted(i + 1, entry.getKey().getName(), entry.getValue().score));
            }

            player.send("");

            if (player == winner) {
                args = PlusLocales.getString(player.player, "play.multi.duels.victory", false).formatted(winningTime).split("\\|\\|");
            } else {
                args = PlusLocales.getString(player.player, "play.multi.duels.loss", false).formatted(winningName).split("\\|\\|");
            }
            sendTitle(player, args[0], args[1], 1, 100, 10);
        });

        Task.create(IPP.getPlugin()).delay(10 * 20).execute(() -> {
            for (ParkourPlayer other : new HashSet<>(playerGenerators.keySet())) {
                if (PlusConfigOption.SEND_BACK_AFTER_MULTIPLAYER) {
                    ParkourUser.unregister(other, true, true, false);
                } else {
                    Modes.DEFAULT.create(other.player);
                }
            }
        }).run();

        this.stopped = true;
    }

    @Override
    protected void registerScore(String time, String difficulty, int score) {

    }

    @Override
    public Mode getMode() {
        return PlusMode.DUELS;
    }

    // returns the sorted leaderboard
    public List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> getLeaderboard() {
        List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> sorted = new ArrayList<>(playerGenerators.entrySet());

        // sort in reverse natural order
        sorted.sort((one, two) -> two.getValue().score - one.getValue().score);

        return sorted;
    }

    private record SpawnData(Location playerSpawn, Location blockSpawn) {

    }
}