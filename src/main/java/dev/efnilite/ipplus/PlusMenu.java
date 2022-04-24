package dev.efnilite.ipplus;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SessionVisibility;
import dev.efnilite.ip.vilib.inventory.PagedMenu;
import dev.efnilite.ip.vilib.inventory.animation.RandomAnimation;
import dev.efnilite.ip.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.ip.vilib.inventory.animation.WaveWestAnimation;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ip.vilib.inventory.item.MenuItem;
import dev.efnilite.ipplus.session.MultiSession;
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
import java.util.stream.Collectors;

public class PlusMenu {

    public static void openSessions(Player player, MenuSort sort) {
        PagedMenu sessionsMenu = new PagedMenu(4, "<white>Lobbies");

        List<MultiSession> sessions = new ArrayList<>(); // get all public sessions
        for (Session session : Session.getSessions()) {
            if (!(session instanceof MultiSession) || session.getVisibility() != SessionVisibility.PUBLIC) { // only display public sessions
                continue;
            }
            sessions.add((MultiSession) session);
        }

        sessions = sessions.stream()
                .sorted((session1, session2) -> {
                    int open1 = session1.getMaxPlayers() - session1.getPlayers().size();
                    int open2 = session2.getMaxPlayers() - session2.getPlayers().size();
                    if (sort == MenuSort.LEAST_OPEN_FIRST) {
                        return open1 - open2;
                    } else {
                        return open2 - open1;
                    }
                })
                .collect(Collectors.toList()); // sort sessions

        List<MenuItem> items = new ArrayList<>();
        for (MultiSession session : sessions) { // turn sessions into items
            Item item = new Item(Material.LIME_STAINED_GLASS_PANE, "<#59DB3E><bold>Lobby " + session.getSessionId());
            item.click((event) -> session.join(player));

            int openSpaces = session.getMaxPlayers() - session.getPlayers().size();
            if (openSpaces == 1) {
                item.material(Material.ORANGE_STAINED_GLASS_PANE);
            } else if (openSpaces == 0) {
                item.material(Material.RED_STAINED_GLASS_PANE)
                        .click((event) -> ParkourSpectator.spectateSession(player, session));
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

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click((event) -> sessionsMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click((event) -> sessionsMenu.page(-1)))

                .item(30, new Item(Material.BLAZE_POWDER, "<#2FBC11><bold>Refresh").click((event) ->
                        openSessions(player, sort)))

                .item(31, new Item(Material.BOOKSHELF, "<#2FBC11><bold>Sort")
                        .lore("<gray>The way everything is sorted")
                        .click((event) -> {
                            if (sort ==  MenuSort.LEAST_OPEN_FIRST) {
                                openSessions(player, MenuSort.LEAST_OPEN_LAST);
                            } else {
                                openSessions(player, MenuSort.LEAST_OPEN_FIRST);
                            }
                        }))

                .item(32, new Item(Material.ARROW, "<red><bold>Close").click((event) ->
                        event.getEvent().getWhoClicked().closeInventory()))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new RandomAnimation())
                .open(player);
    }

    enum MenuSort {

        LEAST_OPEN_FIRST,
        LEAST_OPEN_LAST

    }

    public static void openMultiplayerMenu(Player player) {
        List<Gamemode> gamemodes = IP.getRegistry().getGamemodes().stream().filter(t ->
                t.getName().contains("m-")).collect(Collectors.toList());
        PagedMenu gameMenu = new PagedMenu(4, "<white>Create a multiplayer game");

        List<MenuItem> items = new ArrayList<>();
        for (Gamemode gamemode : gamemodes) {
            items.add(gamemode.getItem("en")
                    .click((event) -> gamemode.handleItemClick(player, null, event.getMenu())));
        }

        gameMenu
                .displayRows(0, 1)
                .addToDisplay(items)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click((event) -> gameMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click((event) -> gameMenu.page(-1)))

                .item(31, new Item(Material.ARROW, "<red><bold>Close").click((event) ->
                        event.getEvent().getWhoClicked().closeInventory()))

                .fillBackground(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .animation(new WaveWestAnimation())
                .open(player);
    }

    public static void openPlayerSelectionMenu(Player player) {
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

            items.add(item.click((event) -> {
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
                        .click((event) -> playerMenu.page(1)))
                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click((event) -> playerMenu.page(-1)))

                .item(31, new Item(Material.PAPER, "<#0b55e0><bold>Session " + sessionId)
                        .lore("<gray>Players can also use", "<#346edb><underlined>/parkour join " + sessionId, "<gray>to join this lobby."))

                .item(33, new Item(Material.ARROW, "<red><bold>Close").click((event) ->
                        event.getEvent().getWhoClicked().closeInventory()))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new WaveEastAnimation())
                .open(player);
    }
}