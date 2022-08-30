package dev.efnilite.ipp.util;

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

        if (event.getHand() == EquipmentSlot.HAND && pp != null) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getItem();

                if (item == null) {
                    return;
                }

                Item start = PlusLocales.getItem(pp.getLocale(), "play.multi.duels.start");

                if (item.getType() == start.getMaterial()) {
                    if (pp.getGenerator() instanceof SingleDuelsGenerator generator) {
                        DuelsGenerator superGen = generator.owningGenerator;
                        if (superGen.getPlayerGenerators().keySet().size() > 1) {
                            superGen.initCountdown();
                            pp.player.getInventory().remove(Material.LIME_BANNER);
                        } else {
                            pp.send(PlusLocales.getString(player, "play.multi.duels.duel_self", false));
                        }
                    }
                }
            }
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