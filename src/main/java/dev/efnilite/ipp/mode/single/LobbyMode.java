package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.mode.Modes;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.lib.vilib.inventory.item.Item;
import dev.efnilite.ipp.generator.single.LobbyGenerator;
import dev.efnilite.ipp.mode.lobby.Lobby;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LobbyMode implements Mode {

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.session.generator instanceof LobbyGenerator) {
            return;
        }

        if (Lobby.getSelections().isEmpty() || !Lobby.getSelections().containsKey(player.getWorld())) {
            return;
        }

        player.closeInventory();

        Session session = Session.create(LobbyGenerator::new, null, null, player);

        Lobby.join(session);
    }

    @Override
    public Item getItem(String locale) {
        return null;
    }

    @Override
    public Leaderboard getLeaderboard() {
        return Modes.DEFAULT.getLeaderboard();
    }

    @Override
    public @NotNull String getName() {
        return "lobby";
    }
}