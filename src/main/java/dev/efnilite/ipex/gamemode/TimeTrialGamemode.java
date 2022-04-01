package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.TimeTrialGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TimeTrialGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "timetrial";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return new Item(Material.WATER_BUCKET, "&#9A0BC7&lTime Trial").lore("&7Reach a score of 100 as fast as you can");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        TimeTrialGenerator generator = new TimeTrialGenerator(pp);
        WITP.getDivider().generate(pp, generator, true);
    }
}
