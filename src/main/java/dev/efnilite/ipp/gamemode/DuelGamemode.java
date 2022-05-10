package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.generator.DuelGenerator;
import dev.efnilite.ipp.generator.TeamSurvivalGenerator;
import dev.efnilite.ipp.menu.CreationMenu;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DuelGamemode implements MultiGamemode {

    @Override
    public @NotNull String getName() {
        return "duel";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.RED_CONCRETE, "<#C91212><bold>Duel")
                .lore("<gray>Race opponents to 100 points!", "<gray>If someone falls, they'll be reset to the start.");
    }

    @Override
    public void create(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        MultiSession session = MultiSession.create(pp, this);
        DuelGenerator generator = new DuelGenerator(session);
        IP.getDivider().generate(pp, generator, false);
        generator.initPoint();
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public void join(Player player, Session session) {
        session.join(player);
        if (session.isAcceptingPlayers() && session.getPlayers().get(0).getGenerator() instanceof DuelGenerator generator) {
            generator.addPlayer(ParkourPlayer.getPlayer(player));
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