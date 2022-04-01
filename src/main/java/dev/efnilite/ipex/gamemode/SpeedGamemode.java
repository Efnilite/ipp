package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.SpeedGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
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
        return new Item(Material.LINGERING_POTION, "&#1882DE&lSpeed").lore("&7Speed is key!");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        SpeedGenerator generator = new SpeedGenerator(pp);
        WITP.getDivider().generate(pp, generator, true);
    }
}
