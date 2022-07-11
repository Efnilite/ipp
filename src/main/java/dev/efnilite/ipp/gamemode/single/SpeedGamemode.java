package dev.efnilite.ipp.gamemode.single;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.single.SpeedGenerator;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SpeedGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "speed";
    }

    @Override
    public @NotNull Item getItem(String s) {
        return new Item(Material.LINGERING_POTION, "<#1882DE><bold>Speed").lore("<gray>Speed is key!");
    }

    @Override
    public void create(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp, this);
        SpeedGenerator generator = new SpeedGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public void click(Player player) {
        create(player);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
