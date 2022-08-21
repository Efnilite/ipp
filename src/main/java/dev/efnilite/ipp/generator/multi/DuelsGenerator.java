package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.data.AreaData;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ip.schematic.RotationAngle;
import dev.efnilite.ip.schematic.Schematic;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.config.PlusOption;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
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

// TODO:
public final class DuelsGenerator extends MultiplayerGenerator {

    public boolean allowJoining;
    public final Map<ParkourPlayer, SingleDuelsGenerator> playerGenerators = new HashMap<>();
    private static final Schematic schematic = new Schematic()
            .file("spawn-island-duels");
    private final Map<ParkourPlayer, SpawnData> spawnData = new HashMap<>();
    private final int goal = IPP.getConfiguration().getFile("config").getInt("gamemodes." + getGamemode().getName().toLowerCase() + ".goal");

    public DuelsGenerator(@NotNull MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    public void init(Vector2D point) {
        if (point == null) {
            return;
        }

        allowJoining = true;
        playerSpawn = IP.getDivider().getEstimatedCenter(point, Option.BORDER_SIZE).toLocation(IP.getWorldHandler().getWorld()).clone();
        playerSpawn.setYaw(-90);
        zone = IP.getDivider().getZone(playerSpawn);

        schematic.read();

        addPlayer(player);

        Task.create(IPP.getPlugin())
                .delay(5)
                .execute(() -> player.getPlayer().getInventory().addItem(new Item(Material.LIME_BANNER, 1, "<#5EC743><bold>Click to start").build()))
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

        generator.setPlayerIndex(playerGenerators.keySet().size());
        generator.setOwningGenerator(this);
        generator.setZone(zone);

        player.setGenerator(generator);

        // setup where:
        // - schematic gets pasted
        // - player gets teleported
        // - block starts generating
        Location spawn = playerSpawn.clone().add(0, 0, 5 * schematic.getDimensions().getWidth() * (getPlayers().size() - 1));
        List<Block> blocks = schematic.paste(spawn, RotationAngle.ANGLE_0);

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
                default -> {}
            }
        }
        generator.setData(new AreaData(blocks));

        spawnData.put(player, new SpawnData(playerSpawn, blockSpawn));
        playerGenerators.put(player, generator);

        for (ParkourPlayer pp : playerGenerators.keySet()) {
            pp.sendTranslated("player-join", player.getName());
        }
    }

    public void removePlayer(ParkourPlayer player) {
        SingleDuelsGenerator generator = this.playerGenerators.get(player);
        generator.reset(false);

        for (ParkourPlayer pp : playerGenerators.keySet()) {
            pp.sendTranslated("player-leave", player.getName());
        }

        AreaData data = generator.getData();

        for (Block block : data.blocks()) {
            block.setType(Material.AIR, false);
        }

        this.playerGenerators.remove(player);

        // if there are no other players, player automatically wins
        if (playerGenerators.size() == 1) {
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
                        switch (countdown.get()) {
                            case 0:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#1BE3DD><bold>Go!", "<gray>First to " + goal + " wins!", 0, 21, 5);
                                    for (Block block : ((DefaultGenerator) player.getGenerator()).getData().blocks()) {
                                        if (block.getType() == Material.BARRIER) {
                                            block.setType(Material.AIR);
                                        }
                                    }

                                    SpawnData data = DuelsGenerator.this.spawnData.get(player);
                                    ((DefaultGenerator) player.getGenerator()).generateFirst(data.playerSpawn, data.blockSpawn);
                                }

                                stopped = false;

                                startTick();
                                cancel();
                                break;
                            case 1:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#DA2626><bold>1", "", 0, 21, 0);
                                }
                                break;
                            case 2:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#DCD31D><bold>2", "", 0, 21, 0);
                                }
                                break;
                            case 3:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#42D929><bold>3", "", 0, 21, 0);
                                }
                                break;
                            default:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    sendTitle(player, "<#23E120><bold>" + countdown.intValue(), "", 0, 21, 0);
                                }
                                close();
                                break;
                        }
                        countdown.getAndDecrement();
                    }
                })
                .run();
    }

    private void sendTitle(ParkourPlayer pp, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
        pp.getPlayer().sendTitle(Util.color(title), Util.color(subtitle), fadeIn, duration, fadeOut);
    }

    private void close() {
        allowJoining = false;
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
        ParkourPlayer winner = null;

        for (ParkourPlayer player : playerGenerators.keySet()) {
            SingleDuelsGenerator generator = (SingleDuelsGenerator) player.getGenerator();

            generator.tick();

            if (generator.getScore() >= goal) {
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
        String winningTime = winner.getGenerator().getStopwatch().toString();

        List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> leaderboard = getLeaderboard();

        for (ParkourPlayer player : playerGenerators.keySet()) {
            SingleDuelsGenerator generator = (SingleDuelsGenerator) player.getGenerator();

            generator.stopGenerator();

            player.send("");
            player.send("<#34B2F9>" + winningName + "<gray> has won the game in " + winningTime + "!");
            player.send("<#34B2F9><bold>Leaderboard:");
            player.send("");

            for (int i = 0; i < leaderboard.size(); i++) {
                Map.Entry<ParkourPlayer, SingleDuelsGenerator> entry = leaderboard.get(i);

                player.send(
                    """
                    <#0072B3>#%d <gray>%s <dark_gray>- <gray>%d
                    """
                .formatted(i + 1, entry.getKey().getName(), entry.getValue().getScore()));
            }

            player.send("");

            if (player == winner) {
                sendTitle(player, "<#EEB40D><bold>Victory", "<gray>You won in " + winningTime + "!", 1, 100, 10);

                getGamemode().getLeaderboard().put(winner.getUUID(),
                        new Score(winningName, winningTime, winner.calculateDifficultyScore(), generator.getScore()));
            } else {
                sendTitle(player, "<#6E1111><bold>Defeat", "<gray>You lost to " + winningName + "!", 1, 100, 10);
            }
        }

        Task.create(IPP.getPlugin())
                .delay(10 * 20)
                .execute(() -> {
                    for (ParkourPlayer parkourPlayer : playerGenerators.keySet()) {
                        ParkourUser.unregister(parkourPlayer, true, true, true);

                        if (!PlusOption.SEND_BACK_AFTER_MULTIPLAYER) {
                            IP.getDivider().generate(ParkourPlayer.register(parkourPlayer.getPlayer()));
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
    public Gamemode getGamemode() {
        return PlusGamemodes.DUELS;
    }

    // returns the sorted leaderboard
    public List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> getLeaderboard() {
        List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> sorted = new ArrayList<>(playerGenerators.entrySet());

        // sort in reverse natural order
        sorted.sort((one, two) -> two.getValue().getScore() - one.getValue().getScore());

        return sorted;
    }

    private record SpawnData(Location playerSpawn, Location blockSpawn) {

    }
}