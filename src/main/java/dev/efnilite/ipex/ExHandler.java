package dev.efnilite.ipex;

import dev.efnilite.fycore.chat.Message;
import dev.efnilite.fycore.util.Time;
import dev.efnilite.ipex.generator.DuelGenerator;
import dev.efnilite.ipex.generator.SingleDuelGenerator;
import dev.efnilite.ipex.util.config.ExOption;
import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.util.config.Option;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ExHandler implements Listener {

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
                    if (pp.getGenerator() instanceof DefaultGenerator) {
                        DefaultGenerator generator = (DefaultGenerator) pp.getGenerator();
//                        generator.altMenu();
                    }
                } else if (dp.contains("Click to start")) {
                    if (pp.getGenerator() instanceof SingleDuelGenerator) {
                        SingleDuelGenerator singleGen = (SingleDuelGenerator) pp.getGenerator();
                        DuelGenerator superGen = singleGen.getOwningGenerator();
                        if (superGen.getPlayerGenerators().keySet().size() > 1) {
                            superGen.initCountdown();
                        } else {
                            pp.send("&4<bold>> <gray>You can't duel yourself! :(");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String message = event.getMessage().replaceAll("(witp|witp:witp)", "parkour");
        if (message.contains("parkour reload") && Option.PERMISSIONS.get() && player.hasPermission("witp.reload")) {
            event.setCancelled(true);
            Time.timerStart("exreload");
            ExOption.init();
            player.sendMessage(Message.parseFormatting("&a<bold>(!) <gray>Reloaded all WITPEx config files in " + Time.timerEnd("exreload") + "ms!"));
        } else if (message.contains("parkour create") && Option.PERMISSIONS.get() && player.hasPermission("witpex.create")) {
            event.setCancelled(true);
            Bukkit.dispatchCommand(player, "/pkx create");
        }
    }
}