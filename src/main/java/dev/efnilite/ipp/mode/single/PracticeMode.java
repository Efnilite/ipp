package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.single.PracticeGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PracticeMode extends SingleMode {

    @Override
    public void create(Player player) {
        create(player, PracticeGenerator::new);
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.single.%s.item".formatted(getName()));
    }

    @Override
    public Leaderboard getLeaderboard() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return "practice";
    }
}
