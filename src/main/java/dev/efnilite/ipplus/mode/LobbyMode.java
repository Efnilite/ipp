package dev.efnilite.ipplus.mode;

import com.google.gson.Gson;
import dev.efnilite.ip.IP;
import dev.efnilite.vilib.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.FileReader;
import java.io.IOException;
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
                LobbySelection selection = new LobbySelection(readInstance.getSelection()); // prevent ghost instances

                selections.put(world, selection);
            }
        } catch (Throwable throwable) {
            Logging.stack("Could not read lobbies folder",
                    "Please delete the lobbies folder and restart. If the problem persists, contact the developer!", throwable);
        }
    }

}