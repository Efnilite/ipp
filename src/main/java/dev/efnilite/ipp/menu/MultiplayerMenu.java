package dev.efnilite.ipp.menu;

import dev.efnilite.ip.api.Registry;
import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.util.Cooldowns;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerMenu {

    /**
     * Opens the multiplayer creation menu
     *
     * @param player The player
     */
    public static void open(Player player) {
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user != null ? user.locale : Option.OPTIONS_DEFAULTS.get(ParkourOption.LANG);

        List<MenuItem> items = new ArrayList<>();
        PagedMenu menu = new PagedMenu(3, PlusLocales.getString(locale, "play.multi.name", false));

        for (Mode mode : Registry.getModes()) {
            boolean permissions = Option.PERMISSIONS && !player.hasPermission("ip.gamemode." + mode.getName());

            if (permissions || !(mode instanceof MultiMode) || mode.getItem(locale) == null) {
                continue;
            }

            items.add(mode.getItem(locale)
                    .click(event -> {
                        if (Cooldowns.passes(player.getUniqueId(), "switch gamemode", 5000)) {
                            mode.create(player);
                        }
                    }));
        }

        menu.displayRows(0, 1)
                .addToDisplay(items)
                .nextPage(26, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT).click(event -> menu.page(1)))
                .prevPage(18, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT).click(event -> menu.page(-1)))
                .item(22, Locales.getItem(player, "other.close").click(event -> Menus.PLAY.open(event.getPlayer())))
                .fillBackground(Util.isBedrockPlayer(player) ? Material.AIR : Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .open(player);
    }
}