package dev.efnilite.ipplus.menu;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.vilib.inventory.PagedMenu;
import dev.efnilite.ip.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.ip.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ip.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.chat.Message;
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

    public static void open(Player player) {
        List<Gamemode> gamemodes = IP.getRegistry().getGamemodes().stream().filter(t -> t.getName().contains("m-")).toList();
        PagedMenu gameMenu = new PagedMenu(4, "<white>Create a multiplayer game");

        List<MenuItem> items = new ArrayList<>();
        for (Gamemode gamemode : gamemodes) {
            items.add(gamemode.getItem("en")
                    .click(event -> gamemode.handleItemClick(player, null, event.getMenu())));
        }

        gameMenu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click(event -> gameMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click(event -> gameMenu.page(-1)))

                .item(31, new Item(Material.ARROW, "<red><bold>Close").click(event ->
                        event.getEvent().getWhoClicked().closeInventory()))

                .fillBackground(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .animation(new WaveWestAnimation())
                .open(player);
    }

    public static void openSelection(Player player) {
        PagedMenu playerMenu = new PagedMenu(4, "<white>Click to invite");
        List<MenuItem> items = new ArrayList<>();
        ParkourUser user = ParkourUser.getUser(player);
        String sessionId = user == null ? "" : user.getSessionId();

        if (sessionId == null || sessionId.isEmpty()) { // no session found
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            Item item = new Item(Material.PLAYER_HEAD, "<#abcdef><bold>" + p.getName());

            ItemStack stack = item.build();
            stack.setType(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            if (meta == null) {
                continue;
            }
            SkullSetter.setPlayerHead(p, meta);
            item.meta(meta);

            items.add(item.click(event -> {
                Message.send(p, "");
                Message.send(p, IP.PREFIX + "You have been invited by " + player.getName() + " to join their " + " Parkour game.");
                Message.send(p, "<dark_gray>Use <#3BC2C2><underlined>/parkour join " + sessionId + "</underlined><dark_gray> to join.");
                Message.send(p, "");
            }));
        }

        playerMenu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click(event -> playerMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click(event -> playerMenu.page(-1)))

                .item(31, new Item(Material.PAPER, "<#0b55e0><bold>Session " + sessionId)
                        .lore("<gray>Players can also use", "<#346edb><underlined>/parkour join " + sessionId, "<gray>to join this lobby."))

                .item(33, new Item(Material.ARROW, "<red><bold>Close").click(event ->
                        event.getEvent().getWhoClicked().closeInventory()))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new WaveEastAnimation())
                .open(player);
    }
}