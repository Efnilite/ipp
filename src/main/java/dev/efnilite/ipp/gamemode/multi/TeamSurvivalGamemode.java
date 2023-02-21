package dev.efnilite.ipp.gamemode.multi;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.leaderboard.Leaderboard;
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

public final class TeamSurvivalGamemode implements MultiGamemode {

    private final Leaderboard leaderboard = new Leaderboard(getName());

    @Override
    public @NotNull String getName() {
        return "team_survival";
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
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.getGenerator() instanceof TeamSurvivalGenerator) {
            return;
        }
        player.closeInventory();

        pp = ParkourUser.register(player);

        MultiSession session = MultiSession.create(pp, this);

        int max = IPP.getConfiguration().getFile("config").getInt("gamemodes." + getName() + ".max");
        if (max < 1 || max > 16) {
            max = 4;

            IPP.logging().stack("Invalid max player range", max + " is not a supported max player count", new IllegalArgumentException());
        }
        session.setMaxPlayers(max);

        TeamSurvivalGenerator generator = new TeamSurvivalGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public void join(Player player, Session session) {
        if (session.isAcceptingPlayers()) {
            player.closeInventory();

            TeamSurvivalGenerator generator = (TeamSurvivalGenerator) session.getPlayers().get(0).getGenerator();

            ParkourPlayer pp = ParkourUser.register(player);
            pp.setGenerator(generator);
            IP.getDivider().setup(pp, generator.playerSpawn, false);

            session.addPlayers(pp);
        }
    }

    @Override
    public void leave(Player player, Session session) {

    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
