package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.PracticeGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PracticeGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "practice";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.WHITE_WOOL, "&f&lPractice").lore("&7Practice a specific type of jump!");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu inventoryBuilder) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        PracticeGenerator generator = new PracticeGenerator(pp);
        WITP.getDivider().generate(pp, generator, true);
    }
}
