package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.TimeTrialGenerator;
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

public final class TimeTrialGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "timetrial";
    }

    @Override
    public @NotNull ItemStack getItem(String s) {
        return new ItemBuilder(Material.WATER_BUCKET, "&#9A0BC7&lTime Trial").setLore("&7Reach a score of 100 as fast as you can").build();
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, InventoryBuilder inventoryBuilder) {
        try {
            player.closeInventory();
            ParkourUser.unregister(user, false, false, true);
            ParkourPlayer pp = ParkourPlayer.register(player, user.getPreviousData());
            TimeTrialGenerator generator = new TimeTrialGenerator(pp);
            WITP.getDivider().generate(pp, generator, true);
        } catch (IOException | SQLException ex) {
            Logging.stack("Error while trying to register player " + player.getName() + " in gamemode " + getName(), "Please report this error to the developer!", ex);
        }
    }
}
