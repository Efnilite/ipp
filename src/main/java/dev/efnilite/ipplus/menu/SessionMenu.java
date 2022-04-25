package dev.efnilite.ipplus.menu;

import dev.efnilite.ip.menu.MainMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SessionVisibility;
import dev.efnilite.ip.vilib.inventory.Menu;
import dev.efnilite.ip.vilib.inventory.PagedMenu;
import dev.efnilite.ip.vilib.inventory.animation.RandomAnimation;
import dev.efnilite.ip.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ip.vilib.inventory.item.MenuItem;
import dev.efnilite.ipplus.session.MultiSession;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Opens the Session menu
 */
public class SessionMenu {

    public static void open(Player player) {
        Menu menu = new Menu(4, "<white>Lobbies")
                .animation(new WaveWestAnimation())
                .fillBackground(Material.GRAY_STAINED_GLASS_PANE);

        menu
                .distributeRowEvenly(1, 3)

                .item(9, new Item(Material.OAK_SAPLING, "<#76EC3E><bold>Create a lobby").click(
                        event -> CreationMenu.open(player)))

                .item(10, new Item(Material.CHEST, "<#EA9926><bold>View current lobbies")
                        .click(event -> openSessions(player, MenuSort.LEAST_OPEN_FIRST)))

                .item(27, new Item(Material.ARROW, "<red><bold>Go back").click(
                        event -> MainMenu.open(player)))

                .open(player);
    }

    public static void openSessions(Player player, MenuSort sort) {
        PagedMenu sessionsMenu = new PagedMenu(4, "<white>Lobbies");

        List<MultiSession> sessions = new ArrayList<>(); // get all public sessions
        for (Session session : Session.getSessions()) {
            if (!(session instanceof MultiSession) || session.getVisibility() != SessionVisibility.PUBLIC) { // only display public sessions
                continue;
            }
            sessions.add((MultiSession) session);
        }

        sessions = sessions.stream().sorted((session1, session2) -> {
            int open1 = session1.getMaxPlayers() - session1.getPlayers().size();
            int open2 = session2.getMaxPlayers() - session2.getPlayers().size();
            if (sort == MenuSort.LEAST_OPEN_FIRST) {
                return open1 - open2;
            } else {
                return open2 - open1;
            }
        }).collect(Collectors.toList()); // sort sessions

        List<MenuItem> items = new ArrayList<>();
        for (MultiSession session : sessions) { // turn sessions into items
            Item item = new Item(Material.LIME_STAINED_GLASS_PANE, "<#59DB3E><bold>Lobby " + session.getSessionId());
            item.click(event -> session.join(player));

            int openSpaces = session.getMaxPlayers() - session.getPlayers().size();
            if (openSpaces == 1) {
                item.material(Material.ORANGE_STAINED_GLASS_PANE);
            } else if (openSpaces == 0) {
                item.material(Material.RED_STAINED_GLASS_PANE).click(event -> ParkourSpectator.spectateSession(player, session));
            }

            List<String> lore = new ArrayList<>();
            if (session.getPlayers().size() > 0) {
                lore.add("<gray>Players"); // #69B759

                for (ParkourPlayer pp : session.getPlayers()) {
                    lore.add("<dark_gray>" + Unicodes.BULLET + " " + pp.getPlayer().getName());
                }
            }

            if (session.getSpectators().size() > 0) {
                lore.add("<gray>Spectators"); // #69B759

                for (ParkourSpectator pp : session.getSpectators()) {
                    lore.add("<dark_gray>" + Unicodes.BULLET + " " + pp.getPlayer().getName());
                }
            }
            item.lore(lore);

            items.add(item);
        }

        sessionsMenu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT).click( // next page
                        event -> sessionsMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT).click( // previous page
                        event -> sessionsMenu.page(-1)))

                .distributeRowEvenly(3)

                .item(30, new Item(Material.BLAZE_POWDER, "<#2FBC11><bold>Refresh").click(
                        event -> openSessions(player, sort)))

                .item(31, new Item(Material.BOOKSHELF, "<#2FBC11><bold>Sort").lore("<gray>The way everything is sorted").click(
                        event -> {
                    if (sort == MenuSort.LEAST_OPEN_FIRST) {
                        openSessions(player, MenuSort.LEAST_OPEN_LAST);
                    } else {
                        openSessions(player, MenuSort.LEAST_OPEN_FIRST);
                    }
                }))

                .item(32, new Item(Material.ARROW, "<red><bold>Go back").click(
                        event -> SessionMenu.open(event.getPlayer())))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new RandomAnimation())
                .open(player);
    }

    public enum MenuSort {

        LEAST_OPEN_FIRST,
        LEAST_OPEN_LAST

    }
}