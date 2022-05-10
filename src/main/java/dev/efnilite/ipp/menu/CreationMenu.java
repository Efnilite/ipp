package dev.efnilite.ipp.menu;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.SkullSetter;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class CreationMenu {

    /**
     * Opens the multiplayer creation menu
     *
     * @param   player
     *          The player
     */
    public static void open(Player player) {
        List<Gamemode> gamemodes = new ArrayList<>();
        for (Gamemode g : IP.getRegistry().getGamemodes()) {
            if (g instanceof MultiGamemode) {
                gamemodes.add(g);
            }
        }
        PagedMenu gameMenu = new PagedMenu(4, "<white>Create a multiplayer game");

        List<MenuItem> items = new ArrayList<>();
        for (Gamemode gamemode : gamemodes) {
            items.add(gamemode.getItem("en").click(event -> gamemode.click(player)));
        }

        gameMenu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click(event -> gameMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click(event -> gameMenu.page(-1)))

                .item(31, new Item(Material.ARROW, "<red><bold>Close").click(event ->
                        LobbyMenu.open(event.getPlayer())))

                .fillBackground(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .animation(new WaveWestAnimation())
                .open(player);
    }
}