package dev.efnilite.ipex.gamemode;

import dev.efnilite.fycore.chat.Message;
import dev.efnilite.ipex.generator.DuelGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
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
        WITP.getDivider().generate(pp, generator, false);
        generator.initPoint();

        Message.send(player, "&4<bold>> <gray>You have to invite another player!");
        Message.send(player, "&4<bold>> <gray>Please use &4&n/pkx invite <player><gray>.");
    }
}