package dev.efnilite.ipex.mode;

import com.google.gson.annotations.Expose;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.vector.Vector3D;
import dev.efnilite.ipex.IPEx;
import dev.efnilite.witp.WITP;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Class for the cube area for Lobby Mode
 */
public class LobbyArea {

    @Expose
    private String world;
    @Expose
    private Vector3D pos1;
    @Expose
    private Vector3D pos2;

    /**
     * Initializer reads the file
     */
    public LobbyArea() {
        LobbyArea area = read();

        if (area == null) {
            return;
        }

        // copy vars
        this.world = area.world;
        this.pos1 = area.pos1;
        this.pos2 = area.pos2;
    }

    public LobbyArea(String world, Vector3D pos1, Vector3D pos2) {
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Saves the lobby-area.json file
     *
     * @return true if saving was successful, false if not
     */
    public boolean save() {
        try {
            File file = new File(WITP.getInstance().getDataFolder() + "/data", "lobby-area.json");

            if (!file.exists()) {
                File folder = new File(IPEx.getInstance().getDataFolder(), "data");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            IPEx.getGson().toJson(this, writer);
            writer.flush();
            writer.close();

            return true;
        } catch (Throwable throwable) {
            Logging.stack("Error while trying to save lobby-area.json", "Please delete this file in the data folder or report this error to the developer", throwable);
            return false;
        }
    }

    /**
     * Read the lobby-area.json file
     */
    public @Nullable LobbyArea read() {
        try {
            File file = new File(WITP.getInstance().getDataFolder() + "/data", "lobby-area.json");

            if (!file.exists()) {
                return null;
            }

            FileReader reader = new FileReader(file);
            LobbyArea area = IPEx.getGson().fromJson(reader, LobbyArea.class);
            reader.close();
            return area;
        } catch (Throwable throwable) {
            Logging.stack("Error while trying to read lobby-area.json", "Please delete this file in the data folder or report this error to the developer", throwable);
            return null;
        }
    }
}