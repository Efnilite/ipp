package dev.efnilite.ipp.mode.lobby;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.generator.ParkourGenerator;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.generator.single.LobbyGenerator;
import dev.efnilite.vilib.util.Numbers;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Class for lobby modes
 */
public class Lobby {

    /**
     * The range from the edge of the selection, in order to ensure a safe spawn.
     */
    public static final int LOBBY_SAFE_RANGE = 10;

    /**
     * The minimum size of the axes of a selection.
     */
    public static final int MINIMUM_SIZE = 3 * LOBBY_SAFE_RANGE; // 30x30x30
    private static final Map<World, LobbySelection> selections = new HashMap<>();
    private static final Path FOLDER = Paths.get(IPP.getPlugin().getDataFolder().toString(), "worlds");

    /**
     * Read all lobby mode files in the IP/lobbies
     */
    public static void read() {
        Task.create(IPP.getPlugin()).async().execute(() -> {
            if (!FOLDER.toFile().exists()) {
                FOLDER.toFile().mkdirs();
                return;
            }

            try (Stream<Path> files = Files.list(FOLDER)) {
                // get all worlds in which lobbies are active
                files.forEach(path -> {
                    String fileName = path.getFileName().toString().split("\\.")[0];
                    World world = Bukkit.getWorld(fileName);

                    if (world == null) {
                        return;
                    }

                    try (FileReader reader = new FileReader(path.toFile())) {
                        LobbySelection readInstance = IP.getGson().fromJson(reader, LobbySelection.class);
                        LobbySelection selection = LobbySelection.from(world, readInstance.pos1, readInstance.pos2); // prevent ghost instances

                        selections.put(world, selection);
                    } catch (Exception ex) {
                        IPP.logging().stack("Could not read files in lobbies folder", "delete the lobbies folder and restart", ex);
                    }
                });
            } catch (Exception ex) {
                IPP.logging().stack("Could not list files in lobbies folder", "delete the lobbies folder and restart", ex);
            }
        }).run();
    }

    /**
     * Saves lobby mode settings for a specific world in memory and in a file.
     * Stored in the IPP/lobbies folder.
     *
     * @param world     The world
     * @param selection The selection
     */
    public static void save(@NotNull World world, @NotNull BoundingBox selection) {
        LobbySelection sel = new LobbySelection(selection);
        selections.put(world, sel);

        Task.create(IPP.getPlugin())
                .async()
                .execute(() -> {
                    try {
                        if (!FOLDER.toFile().exists()) {
                            FOLDER.toFile().mkdirs();
                        }

                        Path path = Paths.get(FOLDER.toString(), world.getName() + ".json");
                        File file = path.toFile();
                        file.createNewFile();

                        try (FileWriter writer = new FileWriter(file)) {
                            IP.getGson().toJson(sel, writer);

                            writer.flush();
                        }
                    } catch (Exception ex) {
                        IPP.logging().stack("Error while trying to save lobby mode settings for world " + world.getName(), ex);
                    }
                })
                .run();
    }

    /**
     * Joins a player to the lobby mode in the world they are in, if there is one.
     *
     * @param session The session
     */
    public static void join(@NotNull Session session) {
        LobbyGenerator generator = new LobbyGenerator(session);
        ParkourPlayer player = session.getPlayers().get(0);
        World world = player.player.getWorld();

        // set spawn block
        Location location = generateSpawn(world, generator);

        if (location == null) {
            return;
        }

        generator.island.blocks = List.of(location.getBlock());

        // the player spawn
        Location spawn = location.clone().add(0.5, 1, 0.5);
        Location block = location.clone().add(1, 0, 0);

        spawn.setYaw(-90);

        generator.generateFirst(spawn, block);
        player.setup(spawn);
        session.generator.startTick();
    }

    /**
     * Generates a random spawn in the lobby selection in a specific world.
     * Sets the spawn block as well.
     *
     * @param world     The world.
     * @param generator The player's generator
     * @return the Location of the spawn block.
     */
    public static @Nullable Location generateSpawn(@NotNull World world, @NotNull ParkourGenerator generator) {
        LobbySelection selection = selections.get(world);

        if (selection == null) {
            return null;
        }

        BoundingBox bb = selection.getBb();
        Location min = bb.getMin().toLocation(world);
        Location max = bb.getMax().toLocation(world);

        generator.zone = new Location[]{min, max};

        // get random block in selection
        int x = Numbers.random(min.getBlockX() + LOBBY_SAFE_RANGE, max.getBlockX() - LOBBY_SAFE_RANGE);
        int y = Numbers.random(min.getBlockY() + LOBBY_SAFE_RANGE, max.getBlockY() - LOBBY_SAFE_RANGE);
        int z = Numbers.random(min.getBlockZ() + LOBBY_SAFE_RANGE, max.getBlockZ() - LOBBY_SAFE_RANGE);

        Location location = new Location(world, x, y, z);
        location.getBlock().setType(Material.SMOOTH_QUARTZ, false);

        return location;
    }
}