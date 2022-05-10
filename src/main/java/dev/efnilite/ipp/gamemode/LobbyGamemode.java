package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.mode.LobbyMode;
import dev.efnilite.vilib.inventory.item.Item;
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
    public void create(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        SingleSession session = (SingleSession) SingleSession.create(pp, this);
        LobbyMode.join(session);
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}