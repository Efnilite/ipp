package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.AreaData;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.schematic.RotationAngle;
import dev.efnilite.ip.schematic.Schematic;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.ipp.util.config.PlusOption;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.vector.Vector2D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class DuelGenerator extends MultiplayerGenerator {

    private boolean allowJoining;
    private static final Schematic schematic = new Schematic()
            .file("duel-island");
    private final Map<ParkourPlayer, SingleDuelGenerator> playerGenerators = new HashMap<>();
    private final Map<ParkourPlayer, SpawnData> spawnData = new HashMap<>();
    private final int goal = IPP.getConfiguration().getFile("config").getInt("gamemodes." + getGamemode().getName().toLowerCase() + ".goal");

    public DuelGenerator(@NotNull MultiSession session) {
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

        System.out.println("Init call for " + player.getName());
        addPlayer(player);

        Task.create(IPP.getPlugin())
                .delay(5)
                .execute(() -> player.getPlayer().getInventory().addItem(new Item(Material.LIME_BANNER, 1, "<#5EC743><bold>Click to start").build()))
                .run();
    }

    public void addPlayer(ParkourPlayer player) {
        System.out.println("Join call for player " + player.getName());

        // only allow joining if the game hasn't started yet and max players
        if (playerGenerators.keySet().size() >= 4 || !allowJoining) {
            return;
        }

        // setup generator
        SingleDuelGenerator generator = new SingleDuelGenerator(player);
        generator.player = player;

        generator.setPlayerIndex(playerGenerators.keySet().size());
        generator.setOwningGenerator(this);
        generator.setZone(zone);

        player.setGenerator(generator);

        // setup where:
        // - schematic gets pasted
        // - player gets teleported
        // - block starts generating
        Location spawn = playerSpawn.clone().add(0, 0, 3 * schematic.getDimensions().getWidth() * (session.getPlayers().size() - 1));
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
        SingleDuelGenerator generator = this.playerGenerators.get(player);
        generator.reset(false);

        for (ParkourPlayer pp : playerGenerators.keySet()) {
            pp.sendTranslated("player-leave", player.getName());
        }

        AreaData data = generator.getData();

        for (Block block : data.blocks()) {
            block.setType(Material.AIR, false);
        }

        this.playerGenerators.remove(player);
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

                                    SpawnData data = DuelGenerator.this.spawnData.get(player);
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
        System.out.println("tick call");
        ParkourPlayer winner = null;
        String winningTime = null;

        for (ParkourPlayer player : playerGenerators.keySet()) {
            SingleDuelGenerator generator = (SingleDuelGenerator) player.getGenerator();

            generator.tick();

            if (generator.getScore() >= goal) {
                winner = player;
                winningTime = generator.getTime();
            }
        }

        if (winner == null) {
            return;
        }

        for (ParkourPlayer player : playerGenerators.keySet()) {
            SingleDuelGenerator generator = (SingleDuelGenerator) player.getGenerator();

            generator.stopGenerator();

            player.send("");
            player.send("<dark_red><bold>> <gray>Player <dark_red><underline>" + winner.getPlayer().getName() + "<gray> has won the game!");
            player.send("<dark_red><bold>> <gray>You will be sent back in 10 seconds.");

            if (player == winner) {
                sendTitle(player, "<#EEB40D><bold>Victory", "<gray>You won in " + winningTime + "!", 1, 100, 10);
            } else {
                sendTitle(player, "<#6E1111><bold>Defeat", "<gray>You lost to " + winner.getPlayer().getName() + "!", 1, 100, 10);
            }
        }

        Task.create(IPP.getPlugin())
                .delay(10 * 20)
                .execute(() -> {
                    for (ParkourPlayer parkourPlayer : playerGenerators.keySet()) {
                        ParkourUser.unregister(parkourPlayer, true, true, true);
                        if (!PlusOption.SEND_BACK_AFTER_MULTIPLAYER.get()) {
                            IP.getDivider().generate(ParkourPlayer.register(parkourPlayer.getPlayer()));
                        }
                    }
                })
                .run();

        this.stopped = true;
    }

    public Map<ParkourPlayer, SingleDuelGenerator> getPlayerGenerators() {
        return playerGenerators;
    }

    // https://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
    private ParkourPlayer getPlayerFromGenerator(SingleDuelGenerator generator) {
        for (Map.Entry<ParkourPlayer, SingleDuelGenerator> entry : playerGenerators.entrySet()) {
            if (Objects.equals(generator, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.DUEL;
    }

    private record SpawnData(Location playerSpawn, Location blockSpawn) {

    }
}