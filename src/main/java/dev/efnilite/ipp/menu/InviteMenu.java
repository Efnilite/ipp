package dev.efnilite.ipp.menu;

import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.lib.paperlib.PaperLib;
import dev.efnilite.ip.lib.vilib.inventory.PagedMenu;
import dev.efnilite.ip.lib.vilib.inventory.item.Item;
import dev.efnilite.ip.lib.vilib.inventory.item.MenuItem;
import dev.efnilite.ip.lib.vilib.util.Cooldowns;
import dev.efnilite.ip.lib.vilib.util.SkullSetter;
import dev.efnilite.ip.lib.vilib.util.Strings;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusLocales;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            Item item = PlusLocales.getItem(user.locale, "invite.head", other.getName())
                    .material(Material.PLAYER_HEAD);

            ItemStack stack = item.build();
            stack.setType(Material.PLAYER_HEAD);

            // bedrock has no player skull support
            if (!ParkourUser.isBedrockPlayer(player)) {
                SkullMeta meta = (SkullMeta) stack.getItemMeta();

                if (meta != null) {
                    SkullSetter.setPlayerHead(other, meta);
                    item.meta(meta);
                }
            }

            items.add(item.click(event -> {
                if (Cooldowns.canPerform(player, "multiplayer invite", 2500)) {
                    for (String s : PlusLocales.getString(other, "invite.message", false).formatted(player.getName(),
                            ChatColor.stripColor(session.generator.getMode().getItem(Option.OPTIONS_DEFAULTS.get(ParkourOption.LANG)).getName()),
                            player.getName()).split("\\|\\|")) {
                        if (PaperLib.isPaper()) {
                            new AdventureInviteSender(other, player, s);
                        } else {
                            send(other, s);
                        }
                    }

                    send(player, PlusLocales.getString(player, "invite.success", false).formatted(other.getName()));
                }
            }));
        }

        playerMenu
                .displayRows(0, 1)
                .addToDisplay(items)
                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>»").click(event -> playerMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>«").click(event -> playerMenu.page(-1)))
                .item(30, PlusLocales.getItem(player, "invite.lobby", player.getName(), player.getName()))
                .item(32, Locales.getItem(player, "other.close").click(event -> Menus.LOBBY.open(event.getPlayer())))
                .open(player);
    }

    private static void send(CommandSender sender, String message) {
        sender.sendMessage(Strings.colour(message));
    }
}
