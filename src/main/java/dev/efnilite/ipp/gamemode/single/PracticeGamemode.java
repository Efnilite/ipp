package dev.efnilite.ipp.gamemode.single;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.single.PracticeGenerator;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PracticeGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "practice";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.single." + getName() + ".item");
    }

    @Override
    public Leaderboard getLeaderboard() {
        return null;
    }

    @Override
    public void create(Player player) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.getGenerator() instanceof PracticeGenerator) {
            return;
        }
        player.closeInventory();

        pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp, this);
        PracticeGenerator generator = new PracticeGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
