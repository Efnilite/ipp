package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.TeamSurvivalGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
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
        WITP.getDivider().generate(pp, generator, true);
    }
}
