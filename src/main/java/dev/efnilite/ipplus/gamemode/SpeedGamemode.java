package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.vilib.inventory.Menu;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipplus.generator.SpeedGenerator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SpeedGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "speed";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.LINGERING_POTION, "<#1882DE><bold>Speed").lore("<gray>Speed is key!");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        SpeedGenerator generator = new SpeedGenerator(pp);
        IP.getDivider().generate(pp, generator, true);
    }
}
