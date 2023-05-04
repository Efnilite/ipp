package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.mode.Modes;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.generator.single.LobbyGenerator;
import dev.efnilite.ipp.mode.lobby.Lobby;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LobbyMode implements Mode {

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.session.generator instanceof LobbyGenerator) {
            return;
        }
        player.closeInventory();

        Session session = Session.create(LobbyGenerator::new)
                .addPlayers(ParkourUser.register(player))
                .complete();

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