package dev.efnilite.ipplus.mode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    private static final Path FOLDER = Paths.get(IP.getInstance().getDataFolder().getName(), "lobbies");

    private static final Map<World, LobbySelection> selections = new HashMap<>();

    /**
     * Read all lobby mode files in the IP/lobbies
     */
    public static void read() {
        new Task()
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
                        Logging.stack("Could not read lobbies folder",
                                "Please delete the lobbies folder and restart. If the problem persists, contact the developer!", throwable);
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

        new Task()
                .async()
                .execute(() -> {
                    try {
                        Path file = Paths.get(FOLDER.toString(), world.getName() + ".json");

                        FileWriter writer = new FileWriter(file.toFile());

                        IP.getGson().toJson(lsel, writer);

                        writer.flush();
                        writer.close();
                    } catch (Throwable throwable) {
                        Logging.stack("Error while trying to save lobby mode settings for world " + world.getName(),
                                "Please report this error to the developer!", throwable);
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
    public static void join(@NotNull Player player) {
        World world = player.getWorld();
        LobbySelection lsel = selections.get(world);

        if (lsel == null) {
            return;
        }

        Selection selection = lsel.selection();


    }
}