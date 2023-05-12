package dev.efnilite.ipp;

import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.generator.multi.DuelsGenerator;
import dev.efnilite.ipp.generator.multi.SingleDuelsGenerator;
import dev.efnilite.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlusHandler implements Listener {

    @EventHandler
    public void click(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);

        if (event.getHand() != EquipmentSlot.HAND || pp == null || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        Item start = PlusLocales.getItem(pp.locale, "play.multi.duels.start");

        if (item == null || item.getType() != start.getMaterial() || !(pp.session.generator instanceof DuelsGenerator generator)) {
            return;
        }

        if (generator.getPlayers().size() > 1) {
            generator.initCountdown();
            pp.player.getInventory().remove(Material.LIME_BANNER);
        } else {
            pp.send(PlusLocales.getString(player, "play.multi.duels.duel_self", false));
        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        if (message.contains("parkour create")) {
            event.setMessage("ipp create");
        } else if (message.contains("parkour lobbies")) {
            event.setMessage("ipp lobbies");
        } else if (message.contains("parkour invite")) {
            event.setMessage("ipp invite");
        }
    }
}