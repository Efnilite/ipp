package dev.efnilite.ipp.mode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.generator.base.ParkourGenerator;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.generator.LobbyGenerator;
import dev.efnilite.vilib.util.Numbers;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
    public static final int LOBBY_SAFE_RANGE = 10;

    /**
     * The minimum size of the axes of a selection.
     */
    public static final int MINIMUM_SIZE = 5 * LOBBY_SAFE_RANGE; // 50x50x50
    private static final Map<World, LobbySelection> selections = new HashMap<>();
    private static final Path FOLDER = Paths.get(IP.getPlugin().getDataFolder().toString(), "lobbies");

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
                            LobbySelection selection = LobbySelection.from(world, readInstance.pos1, readInstance.pos2); // prevent ghost instances

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
                        Path path = Paths.get(FOLDER.toString(), world.getName() + ".json");
                        File file = path.toFile();
                        file.createNewFile();

                        FileWriter writer = new FileWriter(file);

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
     * @param   session
     *          The session
     */
    public static void join(@NotNull SingleSession session) {
        LobbyGenerator generator = new LobbyGenerator(session);
        ParkourPlayer player = session.getPlayers().get(0);
        World world = player.getPlayer().getWorld();

        // set spawn block
        Location location = generateSpawn(world, generator);

        if (location == null) {
            return;
        }

        // the player spawn
        Location spawn = location.clone().add(0.5, 1, 0.5);
        Location block = location.clone().add(1, 0, 0);

        spawn.setYaw(-90);

        generator.generateFirst(spawn, block);
        IP.getDivider().setup(spawn, player);
    }

    /**
     * Generates a random spawn in the lobby selection in a specific world.
     * Sets the spawn block as well.
     *
     * @param   world
     *          The world.
     *
     * @param   generator
     *          The player's generator
     *
     * @return the Location of the spawn block.
     */
    public static @Nullable Location generateSpawn(@NotNull World world, @NotNull ParkourGenerator generator) {
        LobbySelection sel = selections.get(world);

        if (sel == null) {
            return null;
        }

        Selection selection = sel.getSelection();
        Location min = selection.getMinimumPoint();
        Location max = selection.getMaximumPoint();

        generator.setZone(selection);

        // get random block in selection
        int x = Numbers.random(min.getBlockX() + LOBBY_SAFE_RANGE, max.getBlockX() - LOBBY_SAFE_RANGE);
        int y = Numbers.random(min.getBlockY() + LOBBY_SAFE_RANGE, max.getBlockY() - LOBBY_SAFE_RANGE);
        int z = Numbers.random(min.getBlockZ() + LOBBY_SAFE_RANGE, max.getBlockZ() - LOBBY_SAFE_RANGE);

        Location location = new Location(world, x, y, z);
        location.getBlock().setType(Material.SMOOTH_QUARTZ, false);

        return location;
    }
}