package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.generator.TeamSurvivalGenerator;
import dev.efnilite.ipp.menu.CreationMenu;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TeamSurvivalGamemode implements MultiGamemode {

    @Override
    public @NotNull String getName() {
        return "teamsurvival";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.POPPY, "<#C91212><bold>Team Survival")
                .lore("<gray>Live as long as you can with your entire team", "<gray>If someone falls, everyone's back to start");
    }

    @Override
    public void create(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        MultiSession session = MultiSession.create(pp, this);
        TeamSurvivalGenerator generator = new TeamSurvivalGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public void join(Player player, Session session) {
        if (session.getPlayers().get(0).getGenerator() instanceof TeamSurvivalGenerator generator) {

        }
    }

    @Override
    public void leave(Player player, Session session) {
        session.join(player);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
