package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.vilib.inventory.Menu;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipplus.generator.HourglassGenerator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HourglassGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "hourglass";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.SAND, "<#CFB410><bold>Hourglass").lore("<gray>You can only stand on blocks for 1 second.");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        HourglassGenerator generator = new HourglassGenerator(pp);
        IP.getDivider().generate(pp, generator, true);
    }
}
