package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.community.SingleLeaderboardMenu;
import dev.efnilite.ipp.generator.single.WaveTrialGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class WaveTrialMode extends SingleMode {

    private final Leaderboard leaderboard = new Leaderboard(getName(), Leaderboard.Sort.TIME);

    @Override
    public void create(Player player) {
        create(player, WaveTrialGenerator::new);
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public @NotNull String getName() {
        return "wave_trial";
    }
}