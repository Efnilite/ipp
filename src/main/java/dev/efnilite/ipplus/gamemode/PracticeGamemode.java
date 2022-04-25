package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.ipplus.generator.PracticeGenerator;
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
        return new Item(Material.WHITE_WOOL, "<white><bold>Practice").lore("<gray>Practice a specific type of jump!");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu inventoryBuilder) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        PracticeGenerator generator = new PracticeGenerator(pp);
        IP.getDivider().generate(pp, generator, true);
    }
}
