package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.community.SingleLeaderboardMenu;
import dev.efnilite.ipp.generator.single.TimeTrialGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TimeTrialMode extends SingleMode {

    private final Leaderboard leaderboard = new Leaderboard(getName(), SingleLeaderboardMenu.Sort.TIME);

    @Override
    public void create(Player player) {
        create(player, TimeTrialGenerator::new);
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public @NotNull String getName() {
        return "time_trial";
    }
}