package dev.efnilite.ipp.config;

import dev.efnilite.ipp.IPP;
import dev.efnilite.vilib.lib.configupdater.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * An utilities class for the Configuration
 */
public class PlusConfig {

    private final Plugin plugin;
    private final HashMap<String, FileConfiguration> files;

    /**
     * Create a new instance
     */
    public PlusConfig(Plugin plugin) {
        this.plugin = plugin;
        files = new HashMap<>();

        String[] defaultFiles = new String[]{"config.yml"};

        File folder = plugin.getDataFolder();
        for (String name : defaultFiles) {
            File file = new File(folder, name);

            if (!file.exists()) {
                folder.mkdirs();

                plugin.saveResource(name, false);
                IPP.logging().info("Created config file " + name);
            }
        }

        try {
            ConfigUpdater.update(plugin, "config.yml", new File(plugin.getDataFolder(), "config.yml"), List.of("styles"));
        } catch (IOException ex) {
            IPP.logging().stack("Error while updating config.yml", ex);
        }
    }

    public void reload() {
        files.put("config", YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/config.yml")));

        PlusConfigOption.init();
        PlusLocales.init();
    }

    /**
     * Get a file
     */
    public FileConfiguration getFile(String file) {
        FileConfiguration config;
        if (files.get(file) == null) {
            config = YamlConfiguration.loadConfiguration(new File(file));
            files.put(file, config);
        } else {
            config = files.get(file);
        }
        return config;
    }
}