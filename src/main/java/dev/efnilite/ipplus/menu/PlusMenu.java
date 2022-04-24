package dev.efnilite.ipplus.menu;

import dev.efnilite.ip.menu.MainMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipplus.PlusOption;
import org.bukkit.Material;

public class PlusMenu {

    static {
        // Multiplayer if player is not found
        MainMenu.registerMainItem(1, 1, new Item(Material.BUCKET, "<#6E92B1><bold>Multiplayer").click(
                        event -> SessionMenu.open(event.getPlayer(), SessionMenu.MenuSort.LEAST_OPEN_FIRST)),
                player -> !ParkourPlayer.isActive(player) && PlusOption.MULTIPLAYER.check(player));
    }

}
