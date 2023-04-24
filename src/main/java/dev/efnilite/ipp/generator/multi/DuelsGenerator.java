package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.leaderboard.Score;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.schematic.Schematic;
import dev.efnilite.ip.world.WorldDivider;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.mode.PlusMode;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.vector.Vector2D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class DuelsGenerator extends MultiplayerGenerator {

    private static final Schematic schematic = Schematic.create().load("spawn-island-duels");

    public boolean allowJoining;
    public final Map<ParkourPlayer, SingleDuelsGenerator> playerGenerators = new HashMap<>();
    private final Map<ParkourPlayer, SpawnData> spawnData = new HashMap<>();
    private final int goal = IPP.getConfiguration().getFile("config").getInt("gamemodes.%s.goal".formatted(getMode().getName().toLowerCase()));

    public DuelsGenerator(@NotNull MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    public void init(Vector2D point) {
        if (point == null) {
            return;
        }

        allowJoining = true;
        playerSpawn = WorldDivider.toLocation(session);
        playerSpawn.setYaw(-90);

        addPlayer(player);

        Task.create(IPP.getPlugin())
                .delay(5)
                .execute(() -> player.player.getInventory().addItem(new Item(Material.LIME_BANNER, 1, "<#5EC743><bold>CLICK TO START!").build()))
                .run();
    }

    public void addPlayer(ParkourPlayer player) {
        // only allow joining if the game hasn't started yet and max players
        if (playerGenerators.keySet().size() >= 4 || !allowJoining) {
            return;
        }

        // setup generator
        SingleDuelsGenerator generator = new SingleDuelsGenerator(session);
        generator.player = player;
        generator.owningGenerator = this;

        generator.setPlayerIndex(playerGenerators.keySet().size());

        player.generator = generator;

        // setup where:
        // - schematic gets pasted
        // - player gets teleported
        // - block starts generating
        Location spawn = playerSpawn.clone().add(0, 0, (schematic.getDimensions().getX() + PlusConfigOption.DUELS_ISLAND_DISTANCE) * (getPlayers().size() - 1));
        List<Block> blocks = schematic.paste(spawn);

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
                default -> {
                }
            }
        }
        generator.island.blocks = blocks;

        spawnData.put(player, new SpawnData(playerSpawn, blockSpawn));
        playerGenerators.put(player, generator);
        player.updateScoreboard(generator);
    }

    public void removePlayer(ParkourPlayer player) {
        SingleDuelsGenerator generator = this.playerGenerators.get(player);
        generator.reset(false);

        generator.island.destroy();

        this.playerGenerators.remove(player);

        // if there are no other players, player automatically wins
        if (playerGenerators.size() == 1 && !allowJoining) {
            ParkourPlayer winner = new ArrayList<>(playerGenerators.keySet()).get(0);

            win(winner);
        }
    }

    public void initCountdown() {
        AtomicInteger countdown = new AtomicInteger(10);
        Task.create(IPP.getPlugin())
                .repeat(20)
                .execute(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (stopped) {
                            this.cancel();
                            return;
                        }

                        switch (countdown.get()) {
                            case 0 -> {
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    String[] args = PlusLocales.getString(player.player, "play.multi.duels.go", false)
                                            .formatted(goal)
                                            .split("\\|\\|");

                                    sendTitle(player, args[0], args[1], 0, 21, 5);
                                    for (Block block : player.generator.island.blocks) {
                                        if (block.getType() == Material.BARRIER) {
                                            block.setType(Material.AIR);
                                        }
                                    }

                                    SpawnData data = DuelsGenerator.this.spawnData.get(player);
                                    player.generator.generateFirst(data.playerSpawn, data.blockSpawn);
                                }
                                stopped = false;
                                startTick();
                                cancel();
                            }
                            case 1 -> {
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#DA2626><bold>1", "", 0, 21, 0);
                                }
                            }
                            case 2 -> {
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#DCD31D><bold>2", "", 0, 21, 0);
                                }
                            }
                            case 3 -> {
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#42D929><bold>3", "", 0, 21, 0);
                                }
                            }
                            default -> {
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#23E120><bold>" + countdown.intValue(), "", 0, 21, 0);
                                }
                                close();
                            }
                        }
                        countdown.getAndDecrement();
                    }
                })
                .run();
    }

    private void sendTitle(ParkourPlayer pp, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
        pp.player.sendTitle(Strings.colour(title), Strings.colour(subtitle), fadeIn, duration, fadeOut);
    }

    private void close() {
        allowJoining = false;
        session.isAcceptingPlayers = s -> false;
    }

    @Override
    public void reset(boolean regenerate) {
        if (!regenerate) {
            for (ParkourPlayer parkourPlayer : playerGenerators.keySet()) {
                removePlayer(parkourPlayer);
            }
        }
    }

    @Override
    public void tick() {
        if (stopped) {
            return;
        }

        ParkourPlayer winner = null;

        for (ParkourPlayer player : playerGenerators.keySet()) {
            SingleDuelsGenerator generator = (SingleDuelsGenerator) player.generator;

            generator.tick();

            if (generator.score >= goal) {
                winner = player;
            }
        }

        if (winner == null) {
            return;
        }

        win(winner);
    }

    private void win(ParkourPlayer winner) {
        // prevent winning code from being activated more than once
        // could be possible in #removePlayer
        if (stopped) {
            return;
        }

        String winningName = winner.getName();
        String winningTime = winner.generator.getTime();

        List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> leaderboard = getLeaderboard();

        for (ParkourPlayer player : playerGenerators.keySet()) {
            SingleDuelsGenerator generator = (SingleDuelsGenerator) player.generator;

            generator.stopped = true;

            String[] args = PlusLocales.getString(player.player, "play.multi.duels.overview", false)
                    .formatted(winningName, winningTime)
                    .split("\\|\\|");

            player.send("");
            player.send(args[0]);
            player.send(args[1]);
            player.send("");

            for (int i = 0; i < leaderboard.size(); i++) {
                Map.Entry<ParkourPlayer, SingleDuelsGenerator> entry = leaderboard.get(i);

                player.send(
                        """
                                <#0072B3>#%d <gray>%s <dark_gray>- <gray>%d
                                """
                                .formatted(i + 1, entry.getKey().getName(), entry.getValue().score));
            }

            player.send("");

            if (player == winner) {
                args = PlusLocales.getString(player.player, "play.multi.duels.victory", false)
                        .formatted(winningTime)
                        .split("\\|\\|");

                sendTitle(player, args[0], args[1], 1, 100, 10);

                getMode().getLeaderboard().put(winner.getUUID(), new Score(winningName, winningTime, Double.toString(winner.generator.calculateDifficultyScore()), generator.score));
            } else {
                args = PlusLocales.getString(player.player, "play.multi.duels.loss", false)
                        .formatted(winningName)
                        .split("\\|\\|");

                sendTitle(player, args[0], args[1], 1, 100, 10);
            }
        }

        Task.create(IPP.getPlugin())
                .delay(10 * 20)
                .execute(() -> {
                    for (ParkourPlayer parkourPlayer : new ArrayList<>(playerGenerators.keySet())) {
                        ParkourUser.unregister(parkourPlayer, true, true);

                        if (!PlusConfigOption.SEND_BACK_AFTER_MULTIPLAYER) {
                            IP.getDivider().generate(ParkourPlayer.register(parkourPlayer.player));
                        }
                    }
                })
                .run();

        this.stopped = true;
    }

    @Override
    protected void registerScore() {

    }

    public Map<ParkourPlayer, SingleDuelsGenerator> getPlayerGenerators() {
        return playerGenerators;
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