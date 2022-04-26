package dev.efnilite.ipp;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.generator.DuelGenerator;
import dev.efnilite.ipp.generator.SingleDuelGenerator;
import dev.efnilite.ipp.util.config.ExOption;
import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.util.Time;
import org.bukkit.Bukkit;
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
        ParkourPlayer pp = ParkourPlayer.getPlayer(event.getPlayer());
        if (event.getHand() == EquipmentSlot.HAND && pp != null) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getItem();
                if (item == null) {
                    return;
                }
                String dp = item.getItemMeta().getDisplayName();
                if (dp.contains("Gamemode Menu")) {
                    if (pp.getGenerator() instanceof DefaultGenerator generator) {
                        //                        generator.altMenu();
                    }
                } else if (dp.contains("Click to start")) {
                    if (pp.getGenerator() instanceof SingleDuelGenerator singleGen) {
                        DuelGenerator superGen = singleGen.getOwningGenerator();
                        if (superGen.getPlayerGenerators().keySet().size() > 1) {
                            superGen.initCountdown();
                        } else {
                            pp.send("<dark_red><bold>> <gray>You can't duel yourself! :(");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String message = event.getMessage().replaceAll("(IP|IP:IP)", "parkour");
        if (message.contains("parkour reload") && Option.PERMISSIONS.get() && player.hasPermission("IP.reload")) {
            event.setCancelled(true);
            Time.timerStart("exreload");
            ExOption.init();
            player.sendMessage(Message.parseFormatting("&a<bold>(!) <gray>Reloaded all IPEx config files in " + Time.timerEnd("exreload") + "ms!"));
        } else if (message.contains("parkour create") && Option.PERMISSIONS.get() && player.hasPermission("IPex.create")) {
            event.setCancelled(true);
            Bukkit.dispatchCommand(player, "/pkx create");
        }
    }
}