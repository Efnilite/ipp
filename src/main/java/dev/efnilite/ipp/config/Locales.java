package dev.efnilite.ipp.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import dev.efnilite.ip.IP;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.vilib.chat.tag.TextTag;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Locales {

    // a map of all locales with their respective json trees
    // the json trees are stored instead of the files to avoid having to read the files every time
    private static final Map<String, JsonObject> localeTree = new HashMap<>();

    static {
        // get all files in locales folder
        try (Stream<Path> stream = Files.list(Paths.get(IP.getPlugin().getDataFolder() + "/locales"))) {
            stream.forEach(path -> {
                File file = path.toFile();

                // get locale from file name
                String locale = file.getName().split("\\.")[0];

                JsonReader reader;

                try {
                    reader = new JsonReader(new FileReader(file));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                // read file and put it in the map
                JsonElement element = JsonParser.parseReader(reader);

                localeTree.put(locale, element.getAsJsonObject());
            });
        } catch (IOException throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Gets a coloured String from the provided path in the provided locale file.
     * The locale is derived from the player.
     * If the player is a {@link ParkourUser}, their locale value will be used.
     * If not, the default locale will be used.
     *
     * @param   player
     *          The player
     *
     * @param   path
     *          The path
     *
     * @return a coloured String
     */
    public static String getString(Player player, String path) {
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user == null ? Option.DEFAULT_LOCALE : user.getLocale();

        return TextTag.parse(localeTree.get(locale)
                .get(path).getAsString());
    }

    /**
     * Gets a coloured String from the provided path in the provided locale file
     *
     * @param   locale
     *          The locale
     *
     * @param   path
     *          The path
     *
     * @return a coloured String
     */
    public static String getString(String locale, String path) {
        return TextTag.parse(localeTree.get(locale)
                .get(path).getAsString());
    }

    /**
     * Returns an item from a json locale file.
     * The locale is derived from the player.
     * If the player is a {@link ParkourUser}, their locale value will be used.
     * If not, the default locale will be used.
     *
     * @param   player
     *          The player
     *
     * @param   path
     *          The full path of the item in the locale file
     *
     * @return a non-null {@link Item} instance built from the description in the locale file
     */
    @NotNull
    public static Item getItem(Player player, String path, String... replace) {
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user == null ? Option.DEFAULT_LOCALE : user.getLocale();

        return getItem(locale, path, replace);
    }

    /**
     * Returns an item from a provided json locale file with possible replacements.
     *
     * @param   locale
     *          The locale
     *
     * @param   path
     *          The path in the json file
     *
     * @param   replace
     *          The Strings that will replace any appearances of a String following the regex "%[a-z]"
     *
     * @return a non-null {@link Item} instance built from the description in the locale file
     */
    @NotNull
    public static Item getItem(String locale, String path, String... replace) {
        JsonObject foot = localeTree.get(locale)
                .get("items." + path).getAsJsonObject();

        String material = foot.get("material").getAsString();
        String name = foot.get("name").getAsString();
        String lore = foot.get("lore").getAsString();

        Pattern pattern = Pattern.compile("%[a-z]");
        Matcher matcher = pattern.matcher(name);

        int index = 0;
        while (matcher.find()) {
            name = name.replaceFirst(matcher.group(), replace[index]);
            index++;
        }

        matcher = pattern.matcher(lore);

        while (matcher.find()) {
            lore = lore.replaceFirst(matcher.group(), replace[index]);
            index++;
        }

        return new Item(Material.getMaterial(material), name).lore(lore.split("\\|\\|"));
    }

}
