package dev.efnilite.ipp.menu;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.menu.LobbyMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SessionVisibility;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.config.Locales;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.RandomAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens the Session menu
 */
public class ActiveMenu {

    public static void open(Player player, MenuSort sort) {
        PagedMenu menu = new PagedMenu(4, Locales.getString(player, "active.name"));
        ParkourUser user = ParkourUser.getUser(player);
        String locale = user == null ? Option.DEFAULT_LOCALE : user.getLocale();

        List<Session> sessions = new ArrayList<>(); // get all public sessions
        for (Session session : Session.getSessions()) {
            if (session.getVisibility() != SessionVisibility.PUBLIC) { // only display public sessions
                continue;
            }
            sessions.add(session);
        }

        // sort all sessions by available player count
        List<Session> list = new ArrayList<>(sessions);
        list.sort((session1, session2) -> {
            int max1 = 1;
            if (session1 instanceof MultiSession) {
                max1 = ((MultiSession) session1).getMaxPlayers();
            }

            int max2 = 1;
            if (session2 instanceof MultiSession) {
                max2 = ((MultiSession) session2).getMaxPlayers();
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
//        if (Tournament.isActive()) {
//            items.add(new Item(Material.BLUE_STAINED_GLASS_PANE, "<#198EF0><bold>Tournament")); // todo finish
//        }

        for (Session session : sessions) { // turn sessions into items

//            Item item = Locales.getItem(locale, "active.item",
//                            session.getSessionId(), // session id
//                            ChatColor.stripColor(session.getGamemode().getItem(locale).getName())) // gamemode
//                    .material(Material.LIME_STAINED_GLASS_PANE);

            Item item = new Item(Material.LIME_STAINED_GLASS_PANE, "");

            item.click(event -> session.join(player));

            int max = 1;
            if (session instanceof MultiSession) {
                max = ((MultiSession) session).getMaxPlayers();
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
                            ParkourUser u = ParkourUser.getUser(event.getPlayer());
                            if ((u != null && session.getSessionId().equals(u.getSession().getSessionId())) || !session.isAcceptingSpectators()) {
                                return;
                            }

                            Gamemodes.SPECTATOR.create(player, session);
                        });
            }
            item.name(main + "<bold>Lobby " + session.getSessionId());

            List<String> lore = new ArrayList<>();

            lore.add("<gray>Players: " + accent + max + "<dark_gray>/" + max);
            lore.add("<gray>Gamemode: " + accent + session.getGamemode().getName());
            lore.add("");

            if (session.getPlayers().size() > 0) {
                lore.add("<gray>Players:"); // #69B759

                for (ParkourPlayer pp : session.getPlayers()) {
                    lore.add("<dark_gray>" + Unicodes.BULLET + " " + pp.getPlayer().getName());
                }
            }

            if (session.getSpectators().size() > 0) {
                lore.add("<gray>Spectators:"); // #69B759

                for (ParkourSpectator pp : session.getSpectators()) {
                    lore.add("<dark_gray>" + Unicodes.BULLET + " " + pp.getPlayer().getName());
                }
            }

            if (openSpaces == 0 && session.isAcceptingSpectators()) {
                lore.add("");
                lore.add(accent + "You can only join as spectator.");
            }

            items.add(item.lore(lore));
        }

        menu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT).click( // next page
                        event -> menu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT).click( // previous page
                        event -> menu.page(-1)))

                .distributeRowEvenly(3)

                .item(30, Locales.getItem(player, "active.refresh").click(
                        event -> open(player, sort)))

                .item(31, Locales.getItem(player, "active.sort").click(
                        event -> {
                    if (sort == MenuSort.LEAST_OPEN_FIRST) {
                        open(player, MenuSort.LEAST_OPEN_LAST);
                    } else {
                        open(player, MenuSort.LEAST_OPEN_FIRST);
                    }
                }))

                .item(32, IP.getConfiguration().getFromItemData(ParkourUser.getUser(player), "general.close").click(
                        event -> LobbyMenu.INSTANCE.open(event.getPlayer())))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new RandomAnimation())
                .open(player);
    }

    public enum MenuSort {

        LEAST_OPEN_FIRST,
        LEAST_OPEN_LAST

    }
}