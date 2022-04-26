package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.SpeedJumpGenerator;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
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
        return new Item(Material.RABBIT_FOOT, "<#08508E><bold>Speed Jump")
                .lore("<gray>Jump from platform to platform while you become faster and faster!", "&cThis is very hard!");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu menu) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp);
        SpeedJumpGenerator generator = new SpeedJumpGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public boolean isMultiplayer() {
        return false;
    }
}