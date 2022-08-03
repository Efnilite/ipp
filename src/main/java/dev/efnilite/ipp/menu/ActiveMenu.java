package dev.efnilite.ipp.menu;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.menu.LobbyMenu;
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

        List<MultiSession> sessions = new ArrayList<>(); // get all public sessions
        for (Session session : Session.getSessions()) {
            if (!(session instanceof MultiSession) || session.getVisibility() != SessionVisibility.PUBLIC) { // only display public sessions
                continue;
            }
            sessions.add((MultiSession) session);
        }

        // sort all sessions by available player count
        List<MultiSession> list = new ArrayList<>(sessions);
        list.sort((session1, session2) -> {
            int open1 = session1.getMaxPlayers() - session1.getPlayers().size();
            int open2 = session2.getMaxPlayers() - session2.getPlayers().size();
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

        for (MultiSession session : sessions) { // turn sessions into items

            Item item = Locales.getItem(locale, "active.item",
                            session.getSessionId(), // session id
                            ChatColor.stripColor(session.getGamemode().getItem(locale).getName())) // gamemode
                    .material(Material.LIME_STAINED_GLASS_PANE);

            item.click(event -> session.join(player));

            int openSpaces = session.getMaxPlayers() - session.getPlayers().size();
            if (openSpaces == 1) {
                item.material(Material.ORANGE_STAINED_GLASS_PANE);
            } else if (openSpaces == 0) {
                item.material(Material.RED_STAINED_GLASS_PANE).click(event -> Gamemodes.SPECTATOR.create(player, session));
            }

            List<String> updated = item.getLore();
            int players = 0;
            for (int i = 0; i < updated.size(); i++) {
                if (updated.get(i).contains("%p")) {
                    players = i;
                }
            }

            updated.remove(players);
            for (ParkourUser pp : session.getPlayers()) {
                updated.add(players, "<dark_gray>•" + pp.getPlayer().getName());
            }

            int spectators = 0;
            for (int i = 0; i < updated.size(); i++) {
                if (updated.get(i).contains("%s")) {
                    spectators = i;
                }
            }

            updated.remove(spectators);
            for (ParkourUser pp : session.getSpectators()) {
                updated.add(spectators, "<dark_gray>•" + pp.getPlayer().getName());
            }
            item.lore(updated);

            items.add(item);
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