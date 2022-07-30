package dev.efnilite.ipp.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import dev.efnilite.ip.IP;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Locales {

    private static final Map<String, File> map;

    static {
        map = new HashMap<>();

        try (Stream<Path> stream = Files.list(Paths.get(IP.getPlugin().getDataFolder() + "/locales"))) {
            stream.forEach(path -> {
                File file = path.toFile();

                String locale = file.getName().split("\\.")[0];
                map.put(locale, file);
            });
        } catch (IOException throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Returns an item from a json locale file
     *
     * @param   locale
     *          The locale
     *
     * @param   path
     *          The path in the json file
     *
     * @return an {@link Item} instance, constructed from the data in the locale file
     */
    @NotNull
    public static Item getItem(String locale, String path) {
        try (JsonReader reader = new JsonReader(new FileReader(map.get(locale)))) {
            JsonElement element = JsonParser.parseReader(reader); // todo caching

            JsonObject foot = element.getAsJsonObject()
                    .get("items." + path).getAsJsonObject();

            String material = foot.get("material").getAsString();
            String name = foot.get("name").getAsString();
            String lore = foot.get("lore").getAsString();

            return new Item(Material.getMaterial(material), name).lore(lore.split("\\|\\|"));
        } catch (IOException e) {
            return new Item(Material.STONE, "");
        }
    }

}
