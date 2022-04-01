package dev.efnilite.ipex.gamemode;

import dev.efnilite.ipex.generator.SpeedJumpGenerator;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SpeedJumpGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "speedjump";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.RABBIT_FOOT, "&#08508E&lSpeed Jump").lore("&7Jump from platform to platform while you become faster and faster!", "&cThis is very hard!");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourPlayer.register(player);
        SpeedJumpGenerator generator = new SpeedJumpGenerator(pp);
        WITP.getDivider().generate(pp, generator, true);
    }
}
