package dev.efnilite.ipp.mode.multi;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.lib.vilib.inventory.item.Item;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.multi.DuelsGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DuelsMode implements MultiMode {

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.session.generator instanceof DuelsGenerator) {
            return;
        }
        player.closeInventory();

        Session.create(DuelsGenerator::new,
                        session -> session.getPlayers().size() < PlusConfigOption.DUELS_MAX_COUNT && ((DuelsGenerator) session.generator).allowJoining,
                        null,
                        player);
    }

    @Override
    public void join(Player player, Session session) {
        if (!session.isAcceptingPlayers()) {
            return;
        }

        player.closeInventory();

        DuelsGenerator generator = (DuelsGenerator) session.generator;

        ParkourPlayer pp = ParkourUser.register(player, session);
        session.addPlayers(pp);
        generator.addPlayer(pp);
        pp.setup(null);
    }

    @Override
    public void leave(Player player, Session session) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);

        if (pp == null || session.getPlayers().isEmpty()) {
            return;
        }

        ((DuelsGenerator) session.generator).removePlayer(pp);
    }

    @Override
    public int getMaxPlayers() {
        return PlusConfigOption.DUELS_MAX_COUNT;
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