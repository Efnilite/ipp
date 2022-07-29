package dev.efnilite.ipp.gamemode.single;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.single.TimeTrialGenerator;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TimeTrialGamemode implements Gamemode {

    private final Leaderboard leaderboard = new Leaderboard(getName());

    @Override
    public @NotNull String getName() {
        return "time-trial";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return new Item(Material.CLOCK, "<#9A0BC7><bold>Time Trial")
                .lore("<gray>Reach a score of 100 as fast as you can");
    }

    @Override
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    @Override
    public void create(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp, this);
        TimeTrialGenerator generator = new TimeTrialGenerator(session);
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