package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.HourglassGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
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
        WITP.getDivider().generate(pp, generator, true);
    }
}
