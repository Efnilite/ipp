package dev.efnilite.ipp.util;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    public static boolean check(Plugin plugin) {
        String latest;
        try {
            latest = getLatestVersion(plugin);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (!plugin.getDescription().getVersion().equals(latest)) {
            plugin.getLogger().info("A new version of IP+ is available to download!");
            plugin.getLogger().info("Newest version: " + latest);
            return true;
        } else {
            plugin.getLogger().info("IP+ is currently up-to-date!");
            return false;
        }
    }

    public static String getLatestVersion(Plugin plugin) throws IOException {
        InputStream stream;

        try {
            stream = new URL("https://raw.githubusercontent.com/Efnilite/IP-Plus/master/version.yml").openStream();
        } catch (IOException e) {
            plugin.getLogger().info("Unable to check for updates!");
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String version = reader.lines().filter(s -> s.contains("version: "))
                .toList()
                .get(0)
                .replace("version: ", "");
        stream.close();
        reader.close();
        return version;
    }
}