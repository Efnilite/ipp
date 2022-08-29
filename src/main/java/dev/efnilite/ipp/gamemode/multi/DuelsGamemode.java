package dev.efnilite.ipp.gamemode.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.multi.DuelsGenerator;
import dev.efnilite.ipp.generator.multi.SingleDuelsGenerator;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.vector.Vector2D;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DuelsGamemode implements MultiGamemode {

    private final Leaderboard leaderboard = new Leaderboard(getName());

    @Override
    public @NotNull String getName() {
        return "duels";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.multi." + getName() + ".item");
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.getGenerator() instanceof SingleDuelsGenerator) {
            return;
        }
        player.closeInventory();

        pp = ParkourUser.register(player);

        MultiSession session = MultiSession.create(pp, this);
        session.setMaxPlayers(4);

        DuelsGenerator generator = new DuelsGenerator(session);

        Vector2D point = IP.getDivider().generate(pp, null, false);
        IP.getDivider().setup(pp, null, false);

        generator.init(point);
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public void join(Player player, Session session) {
        if (session.isAcceptingPlayers()) {
            final DuelsGenerator generator = ((SingleDuelsGenerator) session.getPlayers().get(0).getGenerator()).owningGenerator;

            player.closeInventory();

            final ParkourPlayer pp = ParkourUser.register(player);
            IP.getDivider().setup(pp, null, false);

            session.addPlayers(pp);
            generator.addPlayer(pp);
        }
    }

    @Override
    public void leave(Player player, Session session) {
        final ParkourPlayer pp = ParkourPlayer.getPlayer(player);

        if (pp == null) {
            return;
        }

        final DuelsGenerator generator = ((SingleDuelsGenerator) session.getPlayers().get(0).getGenerator()).owningGenerator;
        generator.removePlayer(pp);
        session.removePlayers(pp);
        ParkourUser.unregister(pp, true, true, true);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}