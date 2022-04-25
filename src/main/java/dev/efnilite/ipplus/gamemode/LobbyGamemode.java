package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.ipplus.mode.LobbyMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LobbyGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "lobby";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.BOOK, "<#C91212><bold>Lobby")
                .lore("<gray>Play in a lobby.");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        LobbyMode.join(pp);
    }

    @Override
    public boolean isMultiplayer() {
        return false;
    }
}