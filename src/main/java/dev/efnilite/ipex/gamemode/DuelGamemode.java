package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.DuelGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.gamemode.Gamemode;
import dev.efnilite.witp.command.MainCommand;
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

public final class DuelGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "m-duel";
    }

    @Override
    public @NotNull ItemStack getItem(String s) {
        return new ItemBuilder(Material.RED_CONCRETE, "&#C91212&lDuel")
                .setLore("&7Race opponents to 100 points!", "&7If someone falls, they'll be reset to the start.").build();
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, InventoryBuilder inventoryBuilder) {
        try {
            player.closeInventory();
            ParkourUser.unregister(user, false, false, true);

            ParkourPlayer pp = ParkourPlayer.register(player, user.getPreviousData());
            DuelGenerator generator = new DuelGenerator(pp);
            WITP.getDivider().generate(pp, generator, false);
            generator.initPoint();

            MainCommand.send(player, "&4&l> &7You have to invite another player!");
            MainCommand.send(player, "&4&l> &7Please use &4&n/pkx invite <player>&7.");
        } catch (IOException | SQLException ex) {
            Logging.stack("Error while trying to register player " + player.getName() + " in gamemode " + getName(), "Please report this error to the developer!", ex);
        }
    }
}