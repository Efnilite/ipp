package dev.efnilite.ipp.mode.multi;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.community.SingleLeaderboardMenu;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.lib.vilib.inventory.item.Item;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.multi.TeamSurvivalGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TeamSurvivalMode implements MultiMode {

    private final Leaderboard leaderboard = new Leaderboard(getName(), SingleLeaderboardMenu.Sort.SCORE);

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.session.generator instanceof TeamSurvivalGenerator) {
            return;
        }
        player.closeInventory();

        Session.create(TeamSurvivalGenerator::new,
                session -> session.getPlayers().size() < PlusConfigOption.TEAM_SURVIVAL_MAX_COUNT,
                null,
                player);
    }

    @Override
    public void join(Player player, Session session) {
        if (!session.isAcceptingPlayers()) {
            return;
        }
        player.closeInventory();

        ParkourPlayer pp = ParkourUser.register(player, session);
        session.addPlayers(pp);
        pp.setup(session.generator.playerSpawn);
    }

    @Override
    public void leave(Player player, Session session) {

    }

    @Override
    public int getMaxPlayers() {
        return PlusConfigOption.TEAM_SURVIVAL_MAX_COUNT;
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.multi.%s".formatted(getName()));
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public @NotNull String getName() {
        return "team_survival";
    }
}
