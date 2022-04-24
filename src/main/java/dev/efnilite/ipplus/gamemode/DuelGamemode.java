package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.vilib.inventory.Menu;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipplus.generator.DuelGenerator;
import dev.efnilite.vilib.chat.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DuelGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "m-duel";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.RED_CONCRETE, "<#C91212><bold>Duel")
                .lore("<gray>Race opponents to 100 points!", "<gray>If someone falls, they'll be reset to the start.");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        DuelGenerator generator = new DuelGenerator(pp);
        IP.getDivider().generate(pp, generator, false);
        generator.initPoint();

        Message.send(player, "<dark_red><bold>> <gray>You have to invite another player!");
        Message.send(player, "<dark_red><bold>> <gray>Please use <dark_red>&n/pkx invite <player><gray>.");
    }
}