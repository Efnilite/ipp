package dev.efnilite.ipex.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.ipex.IPEx;
import dev.efnilite.witp.schematic.Vector3D;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CuboidArea {

    @Expose
    private String world;
    @Expose
    private Vector3D pos1;
    @Expose
    private Vector3D pos2;

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().setLenient().create();

    public CuboidArea() {
        try {
            read();
        } catch (IOException ex) {
            ex.printStackTrace();
            Logging.stack("Error while reading cuboid.json", "Try deleting the file and setting up your cuboid area again.", ex);
        }
    }

    public CuboidArea(String world, Vector3D pos1, Vector3D pos2) {
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public void save() throws IOException {
        File file = new File(IPEx.getInstance().getDataFolder() + "/data/", "cuboid.json");
        if (!file.exists()) {
            File folder = new File(IPEx.getInstance().getDataFolder(), "data");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        gson.toJson(this, writer);
        writer.flush();
        writer.close();
    }

    public void read() throws IOException {
        File file = new File(IPEx.getInstance().getDataFolder() + "/data/", "cuboid.json");
        FileReader reader = new FileReader(file);
        gson.fromJson(reader, CuboidArea.class);
        reader.close();
    }
}