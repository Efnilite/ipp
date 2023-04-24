package dev.efnilite.ipp.util;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    public static void check(Plugin plugin) {
        String latest;
        try {
            latest = getLatestVersion(plugin);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (!plugin.getDescription().getVersion().equals(latest)) {
            plugin.getLogger().info("A new version of IP+ is available to download!");
            plugin.getLogger().info("Newest version: %s".formatted(latest));
        } else {
            plugin.getLogger().info("IP+ is currently up-to-date!");
        }
    }

    public static String getLatestVersion(Plugin plugin) throws IOException {
        try (InputStream stream = new URL("https://raw.githubusercontent.com/Efnilite/IP-Plus/master/version.yml").openStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                return reader.lines().filter(s -> s.contains("version: "))
                        .toList()
                        .get(0)
                        .replace("version: ", "");
            }
        } catch (IOException e) {
            plugin.getLogger().info("Unable to check for updates!");
            return "";
        }
    }
}