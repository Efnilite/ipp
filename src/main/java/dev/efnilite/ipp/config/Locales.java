package dev.efnilite.ipp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.IPP;
import dev.efnilite.vilib.chat.tag.TextTag;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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

    private static final Gson gson = new GsonBuilder()
                        .setLenient()
                        .disableHtmlEscaping()
                        .create();


    public static void init(Plugin plugin) {
        Task.create(plugin)
                .async()
                .execute(() -> {
                    Path folder = Paths.get(plugin.getDataFolder() + "/locales");

                    // download files to locales folder
                    if (!folder.toFile().exists()) {
                        folder.toFile().mkdirs();

                        IPP.getPlugin().saveResource("locales/en.json", false);
                        //            IPP.getPlugin().saveResource("locales/nl.json", false);
                    }

                    // get all files in locales folder
                    try (Stream<Path> stream = Files.list(folder)) {
                        stream.forEach(path -> {
                            File file = path.toFile();

                            // get locale from file name
                            String locale = file.getName().split("\\.")[0];

                            JsonObject object;
                            try {
                                // read file and transform it into object
                                object = gson.fromJson(new FileReader(file), JsonObject.class);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }

                            localeTree.put(locale, object);
                        });
                    } catch (IOException throwable) {
                        IPP.logging().stack("Error while trying to read locale files", "restart/reload your server", throwable);
                    }

                    Locales.getItem("en", "singleplayer.hourglass");
                })
                .run();
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

        return getString(locale, path);
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
        JsonObject base = localeTree.get(locale);

        String[] split = path.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            base = base.get(split[i]).getAsJsonObject();
        }

        return TextTag.parse(base.get(split[split.length - 1]).getAsString());
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
        JsonObject base = localeTree.get(locale);

        for (String s : path.split("\\.")) {
            base = base.get(s).getAsJsonObject();
        }

        String material = base.get("material").getAsString();
        String name = base.get("name").getAsString();
        String lore = base.get("lore").getAsString();

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

        return new Item(Material.getMaterial(material.toUpperCase()), name).lore(lore.split("\\|\\|"));
    }

}
