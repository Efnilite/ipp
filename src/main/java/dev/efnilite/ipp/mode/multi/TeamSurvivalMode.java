package dev.efnilite.ipp.mode.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.community.SingleLeaderboardMenu;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.multi.TeamSurvivalGenerator;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TeamSurvivalMode implements MultiMode {

    private final Leaderboard leaderboard = new Leaderboard(getName(), SingleLeaderboardMenu.Sort.SCORE);

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.generator instanceof TeamSurvivalGenerator) {
            return;
        }
        player.closeInventory();

        pp = ParkourUser.register(player);

        MultiSession session = MultiSession.create(pp, this);

        int max = IPP.getConfiguration().getFile("config").getInt("gamemodes.%s.max".formatted(getName()));
        if (max < 1 || max > 16) {
            max = 4;

            IPP.logging().stack("Invalid max player range", "%d is not a supported max player count".formatted(max), new IllegalArgumentException());
        }
        session.maxPlayers = max;

        TeamSurvivalGenerator generator = new TeamSurvivalGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public void join(Player player, Session session) {
        if (!session.isAcceptingPlayers.apply(session)) {
            return;
        }
        player.closeInventory();

        TeamSurvivalGenerator generator = (TeamSurvivalGenerator) session.getPlayers().get(0).generator;

        ParkourPlayer pp = ParkourUser.register(player);
        pp.generator = generator;
        pp.setup(generator.playerSpawn, false);

        session.addPlayers(pp);
    }

    @Override
    public void leave(Player player, Session session) {

    }

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.multi." + getName());
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
