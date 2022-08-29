package dev.efnilite.ipp.gamemode.single;

import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.single.LobbyGenerator;
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
    public @NotNull Item getItem(String locale) {
        return new Item(Material.STONE, "");
    }

    @Override
    public Leaderboard getLeaderboard() {
        return Gamemodes.DEFAULT.getLeaderboard();
    }

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.getGenerator() instanceof LobbyGenerator) {
            return;
        }
        player.closeInventory();

        pp = ParkourUser.register(player);
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