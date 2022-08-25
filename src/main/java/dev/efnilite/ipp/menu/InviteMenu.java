package dev.efnilite.ipp.menu;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.config.Locales;
import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.WaveEastAnimation;
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

public class InviteMenu {

    public static void open(Player player) {
        List<MenuItem> items = new ArrayList<>();
        ParkourUser user = ParkourUser.getUser(player);

        if (user == null) {
            return;
        }

        String sessionId = user.getSessionId();

        if (sessionId == null || sessionId.isEmpty()) { // no session found
            return;
        }

        PagedMenu playerMenu = new PagedMenu(4, Locales.getString(player, "invite.name"));
        Session session = user.getSession();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            Item item = Locales.getItem(user.getLocale(), "invite.head", p.getName())
                    .material(Material.PLAYER_HEAD);

            ItemStack stack = item.build();
            stack.setType(Material.PLAYER_HEAD);

            // bedrock has no player skull support
            if (!Util.isBedrockPlayer(player)) {
                SkullMeta meta = (SkullMeta) stack.getItemMeta();

                if (meta != null) {
                    SkullSetter.setPlayerHead(p, meta);
                    item.meta(meta);
                }
            }

            items.add(item.click(event -> {
                Message.send(p, "");
                Message.send(p, IP.PREFIX + "You have been invited by " + player.getName() + " to join their " + session.getGamemode().getName() + " game.");
                Message.send(p, "<dark_gray>Use <#792727>/parkour join " + sessionId + "<dark_gray> to join.");
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

                .item(30, Locales.getItem(player, "invite.lobby", sessionId, sessionId))

                .item(32, IP.getConfiguration().getFromItemData(ParkourUser.getUser(player), "general.close")
                        .click(event -> Menus.LOBBY.open(event.getPlayer())))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new WaveEastAnimation())
                .open(player);
    }

}
