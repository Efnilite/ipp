package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.TeamSurvivalGenerator;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TeamSurvivalGamemode implements Gamemode {

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
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp);
        TeamSurvivalGenerator generator = new TeamSurvivalGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public boolean isMultiplayer() {
        return true;
    }
}
