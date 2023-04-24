package dev.efnilite.ipp.mode.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.world.WorldDivider;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.multi.DuelsGenerator;
import dev.efnilite.ipp.generator.multi.SingleDuelsGenerator;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.vector.Vector2D;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DuelsMode implements MultiMode {

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.generator instanceof SingleDuelsGenerator) {
            return;
        }
        player.closeInventory();

        pp = ParkourUser.register(player);

        MultiSession session = MultiSession.create(pp, this);

        int max = IPP.getConfiguration().getFile("config").getInt("gamemodes.%s.max".formatted(getName()));
        if (max < 1 || max > 16) {
            max = 1;

            IPP.logging().stack("Invalid max player range", "%d is not a supported max player count".formatted(max), new IllegalArgumentException());
        }
        session.maxPlayers = max;

        DuelsGenerator generator = new DuelsGenerator(session);

        Vector2D point = IP.getDivider().generate(pp, null, false);
        pp.setup(null, false);

        generator.init(point);
    }

    @Override
    public void join(Player player, Session session) {
        if (!session.isAcceptingPlayers.apply(session)) {
            return;
        }

        final DuelsGenerator generator = ((SingleDuelsGenerator) session.getPlayers().get(0).generator).owningGenerator;

        player.closeInventory();

        final ParkourPlayer pp = ParkourUser.register(player);
        pp.setup(null, false);

        session.addPlayers(pp);
        generator.addPlayer(pp);
    }

    @Override
    public void leave(Player player, Session session) {
        final ParkourPlayer pp = ParkourPlayer.getPlayer(player);

        if (pp == null || session.getPlayers().size() == 0) {
            return;
        }

        final DuelsGenerator generator = ((SingleDuelsGenerator) session.getPlayers().get(0).generator).owningGenerator;
        generator.removePlayer(pp);
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.multi.%s.item".formatted(getName()));
    }

    @Override
    public Leaderboard getLeaderboard() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return "duels";
    }
}