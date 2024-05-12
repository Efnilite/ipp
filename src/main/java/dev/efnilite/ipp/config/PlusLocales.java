package dev.efnilite.ipp.config;

import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.lib.vilib.inventory.item.Item;
import dev.efnilite.ip.lib.vilib.util.Strings;
import dev.efnilite.ip.lib.vilib.util.Task;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ipp.IPP;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PlusLocales {

    // a map of all locales with their respective json trees
    // the json trees are stored instead of the files to avoid having to read the files every time
    private static final Map<String, FileConfiguration> locales = new HashMap<>();
    // a list of all nodes
    // used to check against missing nodes
    private static List<String> resourceNodes;
    private static FileConfiguration defaultResource;

    public static void init() {
        Task.create(IPP.getPlugin())
                .async()
                .execute(() -> {
                    defaultResource = YamlConfiguration.loadConfiguration(new InputStreamReader(IPP.getPlugin().getResource("locales/en.yml"), StandardCharsets.UTF_8));

                    // get all nodes from the plugin's english resource, aka the most updated version
                    resourceNodes = getChildren(defaultResource);

                    Path folder = Paths.get(IPP.getPlugin().getDataFolder() + "/locales");

                    // download files to locales folder
                    if (!folder.toFile().exists()) {
                        folder.toFile().mkdirs();

                        IPP.getPlugin().saveResource("locales/en.yml", false);
                        IPP.getPlugin().saveResource("locales/nl.yml", false);
                        IPP.getPlugin().saveResource("locales/fr.yml", false);
                    }

                    // get all files in locales folder
                    try (Stream<Path> stream = Files.list(folder)) {
                        stream.forEach(path -> {
                            File file = path.toFile();

                            // get locale from file name
                            String locale = file.getName().split("\\.")[0];

                            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                            validate(defaultResource, config, file);

                            locales.put(locale, config);
                        });
                    } catch (IOException throwable) {
                        IPP.logging().stack("Error while trying to read locale files", "restart/reload your server", throwable);
                    }
                })
                .run();
    }

    // validates whether a lang file contains all required keys.
    // if it doesn't, automatically add them
    private static void validate(FileConfiguration provided, FileConfiguration user, File localPath) {
        List<String> userNodes = getChildren(user);

        for (String node : resourceNodes) {
            if (!userNodes.contains(node)) {
                IPP.logging().info("Fixing missing config node '" + node + "'");

                Object providedValue = provided.get(node);
                user.set(node, providedValue);
            }
        }

        try {
            user.save(localPath);
        } catch (IOException throwable) {
            IPP.logging().stack("Error while trying to save fixed config file " + localPath, "delete this file and restart your server", throwable);
        }
    }

    private static List<String> getChildren(FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("");
        return section != null ? new ArrayList<>(section.getKeys(true)) : Collections.emptyList();
    }

    /**
     * Gets a coloured String from the provided path in the provided locale file.
     * The locale is derived from the player.
     * If the player is a {@link ParkourUser}, their locale value will be used.
     * If not, the default locale will be used.
     *
     * @param player The player
     * @param path   The path
     * @return a coloured String
     */
    public static String getString(Player player, String path, boolean colour) {
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user == null ? Option.OPTIONS_DEFAULTS.get(ParkourOption.LANG) : user.locale;

        return getString(locale, path, colour);
    }

    /**
     * Gets a coloured String from the provided path in the provided locale file
     *
     * @param locale The locale
     * @param path   The path
     * @return a coloured String
     */
    public static String getString(String locale, String path, boolean colour) {
        FileConfiguration base = locales.get(locale);

        if (base == null) {
            String defaultLang = Option.OPTIONS_DEFAULTS.get(ParkourOption.LANG);

            if (defaultLang == null) {
                IPP.logging().stack("No default language found", "check your config for an incorrect lang default value");
                return "";
            }

            base = locales.get(defaultLang);
        }

        String string = base.getString(path);

        if (string == null) {
            IPP.logging().stack("Invalid config path: " + path, "contact the developer");
            return "";
        }

        return colour ? Strings.colour(string) : string;
    }

    /**
     * Returns an item from a json locale file.
     * The locale is derived from the player.
     * If the player is a {@link ParkourUser}, their locale value will be used.
     * If not, the default locale will be used.
     *
     * @param player The player
     * @param path   The full path of the item in the locale file
     * @return a non-null {@link Item} instance built from the description in the locale file
     */
    @NotNull
    public static Item getItem(@NotNull Player player, String path, String... replace) {
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user == null ? Option.OPTIONS_DEFAULTS.get(ParkourOption.LANG) : user.locale;

        return getItem(locale, path, replace);
    }

    /**
     * Returns an item from a provided json locale file with possible replacements.
     *
     * @param locale  The locale
     * @param path    The path in the json file
     * @param replace The Strings that will replace any appearances of a String following the regex "%[a-z]"
     * @return a non-null {@link Item} instance built from the description in the locale file
     */
    @NotNull
    public static Item getItem(String locale, String path, String... replace) {
        final FileConfiguration base;

        if (locales.containsKey(locale)) {
            base = locales.get(locale);
        } else {
            IPP.logging().warn("Unknown locale: " + locale + ", switching to default English embedded resource");
            base = defaultResource;
        }

        String material = base.getString(path + ".material");
        String name = base.getString(path + ".name");
        String lore = base.getString(path + ".lore");

        if (material == null) {
            material = "";
        }
        if (name == null) {
            name = "";
        }
        if (lore == null) {
            lore = "";
        }

        Pattern pattern = Pattern.compile("%[a-z]");
        Matcher matcher = pattern.matcher(name);

        int index = 0;
        while (matcher.find()) {
            if (index == replace.length) {
                break;
            }

            name = name.replaceFirst(matcher.group(), replace[index]);
            index++;
        }

        matcher = pattern.matcher(lore);

        while (matcher.find()) {
            if (index == replace.length) {
                break;
            }

            lore = lore.replaceFirst(matcher.group(), replace[index]);
            index++;
        }

        Item item = new Item(Material.getMaterial(material.toUpperCase()), name);

        if (!lore.isEmpty()) {
            item.lore(lore.split("\\|\\|"));
        }

        return item;
    }
}