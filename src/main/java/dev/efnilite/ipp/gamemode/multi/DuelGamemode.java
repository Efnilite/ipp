package dev.efnilite.ipp.gamemode.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.generator.multi.DuelGenerator;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.vector.Vector2D;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DuelGamemode implements MultiGamemode {

    private final Leaderboard leaderboard = new Leaderboard(getName());

    @Override
    public @NotNull String getName() {
        return "duel";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.RED_CONCRETE, "<#C91212><bold>Duel")
                .lore("<gray>Race opponents to 100 points!", "<gray>If someone falls, they'll be reset to the start.");
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public void create(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);

        MultiSession session = MultiSession.create(pp, this);
        session.setMaxPlayers(4);

        DuelGenerator generator = new DuelGenerator(session);

        IP.getDivider().generate(pp, generator, false);
        IP.getDivider().setup(pp, null, true, false);

        generator.initPoint();
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public void join(Player player, Session session) {
        if (session.isAcceptingPlayers() && session.getPlayers().get(0).getGenerator() instanceof DuelGenerator generator) {
            player.closeInventory();

            ParkourPlayer pp = ParkourUser.register(player);
            IP.getDivider().setup(pp, session.getPlayers().get(0).getLocation(), true, false);

            session.addPlayers(pp);
            generator.addPlayer(pp);
        }
    }

    @Override
    public void leave(Player player, Session session) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        session.removePlayers(pp);
        ParkourUser.unregister(pp, true, true, true);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}