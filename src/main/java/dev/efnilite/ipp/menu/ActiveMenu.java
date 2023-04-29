package dev.efnilite.ipp.menu;

import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.menu.Menus;
import dev.efnilite.ip.mode.Modes;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ip.world.WorldDivider;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens the Session menu
 */
public class ActiveMenu {

    public static void open(Player player, MenuSort sort) {
        PagedMenu menu = new PagedMenu(3, PlusLocales.getString(player, "active.name", false));
        ParkourUser user = ParkourUser.getUser(player);

        List<Session> sessions = new ArrayList<>(); // get all public sessions
        for (Session session : WorldDivider.sessions.values()) {
            if (user != null && user.session == session) {
                continue;
            }

            if (session.visibility != Session.Visibility.PUBLIC) { // only display public sessions
                continue;
            }

            sessions.add(session);
        }

        // sort all sessions by available player count
        List<Session> list = new ArrayList<>(sessions);
        list.sort((session1, session2) -> {
            int max1 = 1;
            if (session1.generator.getMode() instanceof MultiMode multiMode) {
                max1 = multiMode.getMaxPlayers();
            }

            int max2 = 1;
            if (session2.generator.getMode() instanceof MultiMode multiMode) {
                max2 = multiMode.getMaxPlayers();
            }

            int open1 = max1 - session1.getPlayers().size();
            int open2 = max2 - session2.getPlayers().size();
            if (sort == MenuSort.LEAST_OPEN_FIRST) {
                return open1 - open2;
            } else {
                return open2 - open1;
            }
        });
        sessions = list; // sort sessions

        // put tournaments first
        List<MenuItem> items = new ArrayList<>();

        for (Session session : sessions) { // turn sessions into items

//            Item item = Locales.getItem(locale, "active.item",
//                            session.getSessionId(), // session id
//                            ChatColor.stripColor(session.getGamemode().getItem(locale).getName())) // gamemode
//                    .material(Material.LIME_STAINED_GLASS_PANE);

            Item item = new Item(Material.LIME_STAINED_GLASS_PANE, ""); // todo finish

            item.click(event -> {
                if (!WorldDivider.sessions.containsValue(session) || !(session.generator.getMode() instanceof MultiMode)) {
                    return;
                }

                ((MultiMode) session.generator.getMode()).join(player, session);
            });

            int max = 1;
            if (session.generator.getMode() instanceof MultiMode multiMode) {
                max = multiMode.getMaxPlayers();
            }

            String main = "<#59DB3E>";
            String accent = "<#C8F2C0>";

            int openSpaces = max - session.getPlayers().size();
            if (openSpaces == 1) {
                main = "<#DB973E>";
                accent = "<#F2D9C0>";

                item.material(Material.ORANGE_STAINED_GLASS_PANE);
            } else if (openSpaces == 0) {
                main = "<#DB3E3E>";
                accent = "<#F2C0C0>";

                item.material(Material.RED_STAINED_GLASS_PANE)
                        .click(event -> {
                            ParkourUser other = ParkourUser.getUser(event.getPlayer());
                            if ((other != null && session == other.session) || !session.isAcceptingSpectators()) {
                                return;
                            }

                            Modes.SPECTATOR.create(player, session);
                        });
            }
            item.name("%s<bold>Lobby %s".formatted(main, session.getPlayers().get(0).getName()));

            List<String> lore = new ArrayList<>();

            lore.add("<gray>Players: %s%d<dark_gray>/%d".formatted(accent, session.getPlayers().size(), max));
            lore.add("<gray>Mode: %s%s".formatted(accent, session.generator.getMode().getName()));
            lore.add("");

            if (session.getPlayers().size() > 0) {
                lore.add("<gray>Players:"); // #69B759

                for (ParkourPlayer pp : session.getPlayers()) {
                    lore.add("<dark_gray>%s %s".formatted(Unicodes.BULLET, pp.getName()));
                }
            }

            if (session.getSpectators().size() > 0) {
                lore.add("<gray>Spectators:"); // #69B759

                for (ParkourSpectator pp : session.getSpectators()) {
                    lore.add("<dark_gray>%s %s".formatted(Unicodes.BULLET, pp.getName()));
                }
            }

            if (openSpaces == 0 && session.isAcceptingSpectators()) {
                lore.add("");
                lore.add(accent + "You can only join as spectator.");
            }

            items.add(item.lore(lore));
        }

        menu.displayRows(0, 1)
                .addToDisplay(items)
                .nextPage(26, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT).click(event -> menu.page(1)))
                .prevPage(18, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT).click(event -> menu.page(-1)))
                .distributeRowEvenly(3)
                .item(21, PlusLocales.getItem(player, "active.refresh").click(event -> open(player, sort)))
                .item(22, PlusLocales.getItem(player, "active.sort").click(event -> {
                    if (sort == MenuSort.LEAST_OPEN_FIRST) {
                        open(player, MenuSort.LEAST_OPEN_LAST);
                    } else {
                        open(player, MenuSort.LEAST_OPEN_FIRST);
                    }
                }))
                .item(23, Locales.getItem(player, "other.close").click(event -> Menus.COMMUNITY.open(event.getPlayer())))
                .fillBackground(Util.isBedrockPlayer(player) ? Material.AIR : Material.GRAY_STAINED_GLASS_PANE)
                .open(player);
    }

    public enum MenuSort {

        LEAST_OPEN_FIRST,
        LEAST_OPEN_LAST

    }
}