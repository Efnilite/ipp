package dev.efnilite.ipp.menu;

import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.util.Cooldowns;
import dev.efnilite.vilib.inventory.PagedMenu;
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

        PagedMenu playerMenu = new PagedMenu(4, PlusLocales.getString(player, "invite.name", false));
        Session session = user.session;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            Item item = PlusLocales.getItem(user.locale, "invite.head", p.getName())
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
                if (Cooldowns.canPerform(player, "multiplayer invite", 2500)) {
                    for (String s : PlusLocales.getString(p, "invite.message", false).formatted(player.getName(), session.generator.getMode().getName(), player.getName()).split("\\|\\|")) {
                        Util.send(p, s);
                    }

                    Util.send(player, PlusLocales.getString(player, "invite.success", false).formatted(p.getName()));
                }
            }));
        }

        playerMenu
            .displayRows(0, 1)
            .addToDisplay(items)
            .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT).click(event -> playerMenu.page(1)))
            .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT).click(event -> playerMenu.page(-1)))
            .item(30, PlusLocales.getItem(player, "invite.lobby", player.getName(), player.getName()))
            .item(32, Locales.getItem(player, "other.close").click(event -> Menus.LOBBY.open(event.getPlayer())))
            .fillBackground(Util.isBedrockPlayer(player) ? Material.AIR : Material.GRAY_STAINED_GLASS_PANE)
            .open(player);
    }

}
