package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.HourglassGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.gamemode.Gamemode;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
import dev.efnilite.witp.util.Logging;
import dev.efnilite.witp.util.inventory.InventoryBuilder;
import dev.efnilite.witp.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

public final class HourglassGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "hourglass";
    }

    @Override
    public @NotNull ItemStack getItem(String s) {
        return new ItemBuilder(Material.SAND, "&#CFB410&lHourglass").setLore("&7You can only stand on blocks for 1 second.").build();
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, InventoryBuilder inventoryBuilder) {
        player.closeInventory();
        try {
            player.closeInventory();
            ParkourUser.unregister(user, false, false, true);
            ParkourPlayer pp = ParkourPlayer.register(player, user.getPreviousData());
            HourglassGenerator generator = new HourglassGenerator(pp);
            WITP.getDivider().generate(pp, generator, true);
        } catch (IOException | SQLException ex) {
            Logging.stack("Error while trying to register player " + player.getName() + " in gamemode " + getName(), "Please report this error to the developer!", ex);
        }
    }
}
