package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.HourglassGenerator;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HourglassGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "hourglass";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.SAND, "<#CFB410><bold>Hourglass").lore("<gray>You can only stand on blocks for 1 second.");
    }

    @Override
    public void join(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp);
        HourglassGenerator generator = new HourglassGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public boolean isMultiplayer() {
        return false;
    }
}
