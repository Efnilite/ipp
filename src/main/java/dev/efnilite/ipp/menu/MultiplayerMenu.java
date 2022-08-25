package dev.efnilite.ipp.menu;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.config.Locales;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerMenu {

    /**
     * Opens the multiplayer creation menu
     *
     * @param   player
     *          The player
     */
    public static void open(Player player) {
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user == null ? Option.DEFAULT_LOCALE : user.getLocale();

        List<Gamemode> gamemodes = new ArrayList<>();
        for (Gamemode gm : IP.getRegistry().getGamemodes()) {
            boolean permissions = Option.PERMISSIONS && player.hasPermission("witp.gamemode." + gm.getName());

            if (!permissions || !(gm instanceof MultiGamemode) || !gm.isVisible()) {
                continue;
            }

            gamemodes.add(gm);
        }
        PagedMenu gameMenu = new PagedMenu(4, Locales.getString(locale, "multiplayer.name"));

        List<MenuItem> items = new ArrayList<>();
        for (Gamemode gamemode : gamemodes) {
            items.add(gamemode.getItem(locale).click(event -> gamemode.click(player)));
        }

        gameMenu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click(event -> gameMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click(event -> gameMenu.page(-1)))

                .item(31, IP.getConfiguration().getFromItemData(user, "general.close")
                        .click(event -> Menus.PLAY.open(event.getPlayer())))

                .fillBackground(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .animation(new WaveWestAnimation())
                .open(player);
    }
}