package dev.efnilite.ipplus;

import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipplus.generator.DuelGenerator;
import dev.efnilite.ipplus.menu.CreationMenu;
import dev.efnilite.ipplus.menu.SessionMenu;
import dev.efnilite.ipplus.session.MultiSession;
import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.particle.ParticleData;
import dev.efnilite.vilib.particle.Particles;
import dev.efnilite.vilib.util.Logging;
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

        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("sessions")) {
                if (sender instanceof Player && sender.hasPermission("IP.sessions.view")) {
                    SessionMenu.open(player, SessionMenu.MenuSort.LEAST_OPEN_FIRST);
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (sender instanceof Player && sender.hasPermission("IP.sessions.create")) {
                    CreationMenu.open(player);
                }
            } else if (args[0].equalsIgnoreCase("invite")) {
                if (sender instanceof Player && sender.hasPermission("IP.sessions.invite")) {
                    CreationMenu.openSelection(player);
                }
            } else if (args[0].equalsIgnoreCase("createsesh")) {

                ParkourPlayer pp = ParkourPlayer.getPlayer(player);

                Session session = new MultiSession();
                session.addPlayers(pp);
                session.register();
                pp.setSessionId(session.getSessionId());

            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("lobby") && player != null && player.hasPermission("IPex.lobby")) {
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
                            send(player, "&8----------- <blue><bold>Lobby area &8-----------");
                            send(player, "<gray>Your lobby area isn't complete yet.");
                            send(player, "<gray>Be sure to set the first and second position!");
                            return true;
                        }
                        send(player, "&8----------- <blue><bold>Lobby area &8-----------");
                        send(player, "<gray>Your lobby area selection is being saved..");
                        //                        LobbyArea area = new LobbyArea(player.getWorld().getName(), Vector3D.fromBukkit(selection.getPos1().toVector()), Vector3D.fromBukkit(selection.getPos2().toVector()));
                        //                        if (!area.save()) {
                        //                            send(player, "&cThere was an error while saving your lobby area!");
                        //                            send(player, "<gray>Please try again.");
                        //                            return true;
                        //                        }
                        send(player, "<gray>Successfully saved your lobby area. Every active player will be kicked in order to prevent any transition issues.");
                        for (ParkourPlayer pp : ParkourUser.getActivePlayers()) {
                            ParkourUser.leave(pp);
                        }
                    }
                    //                        IPPlus.setCuboidArea(area);
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
                    Logging.stack("Error while registering " + other.getName(), "Please report this error to the developer!", throwable);
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