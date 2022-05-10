package dev.efnilite.ipp;

import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.schematic.selection.Dimensions;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.generator.DuelGenerator;
import dev.efnilite.ipp.menu.CreationMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.LobbyMenu;
import dev.efnilite.ipp.mode.LobbyMode;
import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.particle.ParticleData;
import dev.efnilite.vilib.particle.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PlusCommand extends ViCommand {

    public static final HashMap<Player, Selection> selections = new HashMap<>();

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (args.length == 0) {
            sender.sendMessage("Sup"); // todo
            return true;
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "lobbies" -> {
                    if (sender instanceof Player && sender.hasPermission("ip.lobbies.view")) {
                        LobbyMenu.open(player);
                    }
                    return true;
                }
                case "create" -> {
                    if (sender instanceof Player && sender.hasPermission("ip.sessions.create")) {
                        CreationMenu.open(player);
                    }
                    return true;
                }
                case "invite" -> {
                    if (sender instanceof Player && sender.hasPermission("ip.sessions.invite")) {
                        InviteMenu.open(player);
                    }
                    return true;
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("lobby") && player != null && player.hasPermission("witp.reload")) {
                Selection selection = selections.get(player);
                switch (args[1]) {
                    case "pos1" -> {
                        if (selections.get(player) == null) {
                            selections.put(player, new Selection(player.getLocation(), null, player.getWorld()));
                        } else {
                            Location pos1 = player.getLocation();
                            Location pos2 = selections.get(player).getPos2();
                            selections.put(player, new Selection(pos1, pos2, player.getWorld()));
                            Particles.box(BoundingBox.of(pos1, pos2), player.getWorld(), new ParticleData<>(Particle.END_ROD, null, 2), player, 0.2);
                        }
                        send(player, "<blue><bold>> <gray>Position 1 was set to " + Util.toString(player.getLocation(), true));
                        return true;
                    }
                    case "pos2" -> {
                        if (selections.get(player) == null) {
                            selections.put(player, new Selection(null, player.getLocation(), player.getWorld()));
                        } else {
                            Location pos1 = selections.get(player).getPos1();
                            Location pos2 = player.getLocation();
                            selections.put(player, new Selection(pos1, pos2, player.getWorld()));
                            Particles.box(BoundingBox.of(pos1, pos2), player.getWorld(), new ParticleData<>(Particle.END_ROD, null, 2), player, 0.2);
                        }
                        send(player, "<blue><bold>> <gray>Position 2 was set to " + Util.toString(player.getLocation(), true));
                        return true;
                    }
                    case "save" -> {
                        if (selection == null || !selection.isComplete()) {
                            send(player, "<gray>Your lobby area isn't complete yet.");
                            send(player, "<gray>Be sure to set the first and second position!");
                            return true;
                        } else {
                            Dimensions dimensions = selection.getDimensions();
                            if (dimensions.getWidth() < LobbyMode.MINIMUM_SIZE ||
                                    dimensions.getHeight() < LobbyMode.MINIMUM_SIZE ||
                                    dimensions.getLength() < LobbyMode.MINIMUM_SIZE) {
                                send(player, "<gray>You haven't made the area big enough!");
                                send(player, "<gray>It needs to be at least " + LobbyMode.MINIMUM_SIZE + " blocks in all directions.");
                                return true;
                            }
                        }
                        send(player, "<gray>Your lobby area selection is being saved.");
                        LobbyMode.save(player.getWorld(), selection);
                    }
                }
            }
            return true;
        } else if (args[0].equalsIgnoreCase("invite")) {
            Player other = Bukkit.getPlayer(args[1]);
            if (other == null) {
                send(sender, "<dark_red><bold>> <gray>That player isn't online in this server!");
                return true;
            } else if (!(sender instanceof Player)) {
                return true;
            }
            Player pSender = (Player) sender;

            ParkourPlayer pp = ParkourPlayer.getPlayer(pSender);
            if (pp == null) {
                return true;
            }
            if (pp.getGenerator() instanceof DuelGenerator gen) {
                try {
                    gen.addPlayer(ParkourUser.register(other));
                } catch (Throwable throwable) {
                    IPP.logging().stack("Error while registering " + other.getName(), throwable);
                }
            }

            send(other, "<dark_red><bold>> <dark_red>&n" + pSender.getName() + "<gray> has invited you to play ");
            send(other, "<dark_red><bold>> <dark_red>&nClick here<gray> to join. ");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(Message.parseFormatting(message));
    }
}