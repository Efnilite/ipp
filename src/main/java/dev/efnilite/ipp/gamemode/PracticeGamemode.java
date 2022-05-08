package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import dev.efnilite.ipp.generator.PracticeGenerator;
import dev.efnilite.vilib.inventory.item.Item;
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
    public void join(Player player) {
        player.closeInventory();
        ParkourPlayer pp = ParkourUser.register(player);
        Session session = SingleSession.create(pp);
        PracticeGenerator generator = new PracticeGenerator(session);
        IP.getDivider().generate(pp, generator, true);
    }

    @Override
    public boolean isMultiplayer() {
        return false;
    }
}
