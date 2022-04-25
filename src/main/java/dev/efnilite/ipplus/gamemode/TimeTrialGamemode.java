package dev.efnilite.ipplus.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.ipplus.generator.TimeTrialGenerator;
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
        return new Item(Material.WATER_BUCKET, "<#9A0BC7><bold>Time Trial")
                .lore("<gray>Reach a score of 100 as fast as you can");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        TimeTrialGenerator generator = new TimeTrialGenerator(pp);
        IP.getDivider().generate(pp, generator, true);
    }
}
