package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.community.SingleLeaderboardMenu;
import dev.efnilite.ipp.generator.single.HourglassGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HourglassMode extends SingleMode {

    private final Leaderboard leaderboard = new Leaderboard(getName(), SingleLeaderboardMenu.Sort.SCORE);

    @Override
    public void create(Player player) {
        create(player, HourglassGenerator::new);
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public @NotNull String getName() {
        return "hourglass";
    }
}
