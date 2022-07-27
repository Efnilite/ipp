package dev.efnilite.ipp.menu;

import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.chat.ChatType;
import dev.efnilite.ip.menu.DynamicMenu;
import dev.efnilite.ip.menu.MainMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SessionVisibility;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.RandomAnimation;
import dev.efnilite.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens the Session menu
 */
public class MultiplayerMenu {

    public static void open(Player player) {
        Menu menu = new Menu(4, "<white>Lobbies")
                .animation(new WaveWestAnimation())
                .fillBackground(Material.GRAY_STAINED_GLASS_PANE);

        menu
                .distributeRowEvenly(1, 3)

                .item(9, new Item(Material.OAK_SAPLING, "<#76EC3E><bold>Create a lobby")
                        .lore("<dark_gray>Eine Lobby erstellen • 创建大厅",
                                "<dark_gray>• 創建大廳 • Créer un lobby",
                                "<dark_gray>• ロビーを作成する • Een lobby aanmaken").click(
                        event -> CreationMenu.open(player)))

                .item(10, new Item(Material.CHEST, "<#EA9926><bold>View current lobbies")
                        .lore("<dark_gray>Aktuelle Lobbys ansehen • 查看当前大厅" ,
                                "<dark_gray>• 查看當前大廳 • Voir les lobbys actuels",
                                "<dark_gray>• 現在のロビーを表示 • Actuele lobby's bekijken")
                        .click(event -> openSessions(player, MenuSort.LEAST_OPEN_FIRST)))

                .item(27, new Item(Material.ARROW, "<#F5A3A3><bold>Go back")
                        .lore("<dark_gray>Zurückgehen • 回去", "<dark_gray>• Retourner • 戻る", "<dark_gray>• Teruggaan").click(
                        event -> MainMenu.INSTANCE.open(player)))

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
            Item item = new Item(Material.LIME_STAINED_GLASS_PANE, "<#59DB3E><bold>Lobby " + session.getSessionId());
            item.click(event -> session.join(player));

            int openSpaces = session.getMaxPlayers() - session.getPlayers().size();
            if (openSpaces == 1) {
                item.material(Material.ORANGE_STAINED_GLASS_PANE);
            } else if (openSpaces == 0) {
                item.material(Material.RED_STAINED_GLASS_PANE).click(event -> Gamemodes.SPECTATOR.create(player, session));
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

                .item(30, new Item(Material.BLAZE_POWDER, "<#2FBC11><bold>Refresh")
                        .lore("<dark_gray>Erneuern • 刷新 • Rafraîchir", "<dark_gray>リフレッシュ • Vernieuwen").click(
                        event -> openSessions(player, sort)))

                .item(31, new Item(Material.BOOKSHELF, "<#DEA11F><bold>Sort")
                        .lore("<dark_gray>Sortieren • 种类", "<dark_gray>Trier • 選別 • Sorteren").click(
                        event -> {
                    if (sort == MenuSort.LEAST_OPEN_FIRST) {
                        openSessions(player, MenuSort.LEAST_OPEN_LAST);
                    } else {
                        openSessions(player, MenuSort.LEAST_OPEN_FIRST);
                    }
                }))

                .item(32, new Item(Material.ARROW, "<red><bold>Go back").click(
                        event -> MultiplayerMenu.open(event.getPlayer())))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new RandomAnimation())
                .open(player);
    }

    public enum MenuSort {

        LEAST_OPEN_FIRST,
        LEAST_OPEN_LAST

    }
}