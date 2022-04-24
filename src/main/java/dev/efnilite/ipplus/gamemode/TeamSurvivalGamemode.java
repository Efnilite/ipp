package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.vilib.inventory.Menu;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipplus.generator.TeamSurvivalGenerator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TeamSurvivalGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "m-teamsurvival";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.POPPY, "<#C91212><bold>Team Survival")
                .lore("<gray>Live as long as you can with your entire team", "<gray>If someone falls, everyone's back to start");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        TeamSurvivalGenerator generator = new TeamSurvivalGenerator(pp);
        IP.getDivider().generate(pp, generator, true);
    }
}
