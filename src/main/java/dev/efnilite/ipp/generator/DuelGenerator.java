package dev.efnilite.ipp.generator;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.generator.AreaData;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.schematic.RotationAngle;
import dev.efnilite.ip.schematic.Schematic;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ip.world.WorldDivider;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.ipp.util.config.PlusOption;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.vector.Vector2D;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DuelGenerator extends MultiplayerGenerator {

    private static final Schematic schematic = new Schematic()
            .file("duel-island");
    private final Map<ParkourPlayer, SingleDuelGenerator> playerGenerators = new HashMap<>();

    public DuelGenerator(@NotNull MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);
    }

    public void initPoint() {
        WorldDivider divider = IP.getDivider();
        Vector2D point = divider.getPoint(player);

        if (point == null) {
            return;
        }

        playerSpawn = divider.getEstimatedCenter(point, Option.BORDER_SIZE.getAsDouble()).toLocation(player.getPlayer().getWorld()).clone();
        schematic.read();

        addPlayer(player);

        player.getPlayer().getInventory().addItem(new Item(Material.GREEN_BANNER, 1, "&a<bold>Click to start").build());
        //                .setPersistentData("IPex", "true").buildPersistent(InfiniteEx.getInstance())); todo
    }

    public void addPlayer(ParkourPlayer player) {
        if (playerGenerators.keySet().size() == 4) {
            return;
        }

        SingleDuelGenerator generator = new SingleDuelGenerator(player);
        generator.setPlayerIndex(playerGenerators.keySet().size());
        generator.setOwningGenerator(this);

        Location spawn = playerSpawn.clone().add(playerGenerators.keySet().size() * 10, 0, 0);
        List<Block> blocks = schematic.paste(spawn, RotationAngle.ANGLE_0);
        for (Block block : blocks) {
            switch (block.getType()) {
                case EMERALD_BLOCK -> {
                    generator.setBlockSpawn(block.getLocation().add(0.5, 0, 0.5));
                    block.setType(Material.AIR);
                }
                case DIAMOND_BLOCK -> {
                    generator.setPlayerSpawn(block.getLocation().add(0.5, 0, 0.5));
                    block.setType(Material.AIR);
                }
                default -> {}
            }
        }
        generator.setData(new AreaData(blocks, Collections.singletonList(player.getPlayer().getLocation().getChunk())));

        this.playerGenerators.put(player, generator);
    }

    public void removePlayer(ParkourPlayer player) {
        SingleDuelGenerator generator = this.playerGenerators.get(player);
        generator.reset(false);

        AreaData data = generator.getData();
        for (Chunk spawnChunk : data.spawnChunks()) {
            spawnChunk.setForceLoaded(false);
        }

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
                                    player.getPlayer().sendTitle("<#1BE3DD><bold>Go!", "<gray>First to 100 wins!", 0, 21, 5);
                                    for (Block block : ((DefaultGenerator) player.getGenerator()).getData().blocks()) {
                                        if (block.getType() == Material.GLASS) {
                                            block.setType(Material.AIR);
                                        }
                                    }
                                }
                                startTick();
                                cancel();
                                break;
                            case 1:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    player.getPlayer().sendTitle("<#DA2626><bold>1", "", 0, 21, 0);
                                }
                                break;
                            case 2:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    player.getPlayer().sendTitle("<#DCD31D><bold>2", "", 0, 21, 0);
                                }
                                break;
                            case 3:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    player.getPlayer().sendTitle("<#42D929><bold>3", "", 0, 21, 0);
                                }
                                break;
                            default:
                                for (ParkourPlayer player : playerGenerators.keySet()) {
                                    player.getPlayer().sendTitle("<#23E120><bold>" + countdown.intValue(), "", 0, 21, 0);
                                }
                                break;
                        }
                    }
                })
                .run();
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
        super.tick();

        ParkourPlayer winner = null;
        String winningTime = null;
        for (SingleDuelGenerator generator : playerGenerators.values()) {
            generator.tick();

            if (generator.getScore() > 100) {
                winner = getPlayerFromGenerator(generator);
                winningTime = generator.getTime();
            }
            generator.stopGenerator();
        }
        if (winner == null) {
            return;
        }

        for (ParkourPlayer player : playerGenerators.keySet()) {
            player.send("");
            player.send("<dark_red><bold>> <gray>Player <dark_red>&u" + winner.getPlayer().getName() + "<gray> has won the game!");
            player.send("<dark_red><bold>> <gray>You will be sent back in 10 seconds.");

            if (player == winner) {
                player.getPlayer().sendTitle("&6<bold>Victory", "<gray>You won in " + winningTime + "!", 1, 2 * 4, 10);
            } else {
                player.getPlayer().sendTitle("&c<bold>Defeat", "<gray>You lost to " + winner.getPlayer().getName() + "!", 1, 2 * 4, 10);
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
}