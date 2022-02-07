package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.SpeedJumpGenerator;
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

public final class SpeedJumpGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "speedjump";
    }

    @Override
    public @NotNull ItemStack getItem(String s) {
        return new ItemBuilder(Material.RABBIT_FOOT, "&#08508E&lSpeed Jump").setLore("&7Jump from platform to platform!").build();
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, InventoryBuilder inventoryBuilder) {
        try {
            player.closeInventory();
            ParkourUser.unregister(user, false, false, true);
            ParkourPlayer pp = ParkourPlayer.register(player, user.getPreviousData());
            SpeedJumpGenerator generator = new SpeedJumpGenerator(pp);
            WITP.getDivider().generate(pp, generator, true);
        } catch (IOException | SQLException ex) {
            Logging.stack("Error while trying to register player " + player.getName() + " in gamemode " + getName(), "Please report this error to the developer!", ex);
        }
    }
}
