package dev.efnilite.ipplus.mode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ipplus.IPP;
import dev.efnilite.ipplus.generator.LobbyGenerator;
import dev.efnilite.vilib.util.Numbers;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for lobby modes
 */
public class LobbyMode {

    /**
     * The range from the edge of the selection, in order to ensure a safe spawn.
     */
    private static final int LOBBY_SAFE_RANGE = 10;

    /**
     * The minimum size of any of the axes of a selection.
     */
    private static final int MINIMUM_SIZE = 5 * LOBBY_SAFE_RANGE; // 30
    private static final Map<World, LobbySelection> selections = new HashMap<>();
    private static final Path FOLDER = Paths.get(IP.getPlugin().getDataFolder().getName(), "lobbies");

    /**
     * Read all lobby mode files in the IP/lobbies
     */
    public static void read() {
        Task.create(IPP.getPlugin())
                .async()
                .execute(() -> {
                    try {
                        if (!FOLDER.toFile().exists()) {
                            FOLDER.toFile().mkdirs();
                        }

                        // get all worlds in which lobbies are active
                        List<Path> files = Files.list(FOLDER)
                                .filter(path -> path.endsWith(".json")) // only json files
                                .toList();

                        for (Path file : files) {
                            String fileName = file.getFileName().toString();
                            World world = Bukkit.getWorld(fileName);

                            if (world == null) {
                                continue;
                            }

                            FileReader reader = new FileReader(file.toFile());

                            LobbySelection readInstance = IP.getGson().fromJson(reader, LobbySelection.class);
                            LobbySelection selection = new LobbySelection(readInstance.selection()); // prevent ghost instances

                            selections.put(world, selection);

                            reader.close();
                        }
                    } catch (Throwable throwable) {
                        IPP.logging().stack("Could not read files in lobbies folder",
                                "delete the lobbies folder and restart", throwable);
                    }
                })
                .run();
    }

    /**
     * Saves lobby mode settings for a specific world in memory and in a file.
     * Stored in the WITP/lobbies folder.
     *
     * @param   world
     *          The world
     *
     * @param   selection
     *          The selection
     */
    public static void save(@NotNull World world, @NotNull Selection selection) {
        LobbySelection lsel = new LobbySelection(selection);
        selections.put(world, lsel);

        Task.create(IPP.getPlugin())
                .async()
                .execute(() -> {
                    try {
                        Path file = Paths.get(FOLDER.toString(), world.getName() + ".json");

                        FileWriter writer = new FileWriter(file.toFile());

                        IP.getGson().toJson(lsel, writer);

                        writer.flush();
                        writer.close();
                    } catch (Throwable throwable) {
                        IPP.logging().stack("Error while trying to save lobby mode settings for world " + world.getName(), throwable);
                    }
                })
                .run();
    }

    /**
     * Joins a player to the lobby mode in the world they are in, if there is one.
     *
     * @param   player
     *          The player
     */
    public static void join(@NotNull ParkourPlayer player) {
        LobbyGenerator generator = new LobbyGenerator(player);
        World world = player.getPlayer().getWorld();

        // set spawn block
        Location location = generateSpawn(world);

        if (location == null) {
            return;
        }

        // the player spawn
        Location spawn = location.clone().add(0, 1, 0);
        Location block = location.clone().add(1, 0, 0);

        generator.generateFirst(spawn, block);
    }

    /**
     * Generates a random spawn in the lobby selection in a specific world.
     * Sets the spawn block as well.
     *
     * @param   world
     *          The world.
     *
     * @return the Location of the spawn block.
     */
    public static @Nullable Location generateSpawn(@NotNull World world) {
        LobbySelection sel = selections.get(world);

        if (sel == null) {
            return null;
        }

        Selection selection = sel.selection();
        Location min = selection.getMinimumPoint();
        Location max = selection.getMaximumPoint();

        // get random block in selection
        int x = Numbers.random(min.getBlockX() + LOBBY_SAFE_RANGE, max.getBlockX() - LOBBY_SAFE_RANGE);
        int y = Numbers.random(min.getBlockY() + LOBBY_SAFE_RANGE, max.getBlockY() - LOBBY_SAFE_RANGE);
        int z = Numbers.random(min.getBlockZ() + LOBBY_SAFE_RANGE, max.getBlockZ() - LOBBY_SAFE_RANGE);

        Location location = new Location(world, x, y, z);
        location.getBlock().setType(Material.SMOOTH_QUARTZ, false);

        return location;
    }
}