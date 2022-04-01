package dev.efnilite.ipex;

import dev.efnilite.fycore.chat.Message;
import dev.efnilite.fycore.command.FyCommand;
import dev.efnilite.fycore.particle.ParticleData;
import dev.efnilite.fycore.particle.Particles;
import dev.efnilite.fycore.util.Logging;
import dev.efnilite.fycore.vector.Vector3D;
import dev.efnilite.ipex.generator.DuelGenerator;
import dev.efnilite.ipex.mode.LobbyArea;
import dev.efnilite.ipex.session.MultiSession;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
import dev.efnilite.witp.schematic.selection.Selection;
import dev.efnilite.witp.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ExCommand extends FyCommand {

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
                if (sender instanceof Player) {
                    ExMenu.openSessions((Player) sender, ExMenu.MenuSort.LEAST_OPEN_FIRST);
                }
            } else if (args[0].equalsIgnoreCase("createsesh")) {
                ParkourPlayer pp = ParkourPlayer.getPlayer(player); // todo remove
                MultiSession session = new MultiSession();
                session.addPlayers(pp);
                session.register();

                pp.send("Registered!");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("cuboid") && player != null && player.hasPermission("witpex.cuboid")) {
                Selection selection = selections.get(player);
                switch (args[1]) {
                    case "pos1":
                        if (selections.get(player) == null) {
                            selections.put(player, new Selection(player.getLocation(), null, player.getWorld()));
                        } else {
                            Location pos1 = player.getLocation();
                            Location pos2 = selections.get(player).getPos2();
                            selections.put(player, new Selection(pos1, pos2, player.getWorld()));
                            Particles.box(BoundingBox.of(pos1, pos2), player.getWorld(), new ParticleData<>(Particle.END_ROD, null, 2), player, 0.2);
                        }
                        send(player, "&b&l> &7Position 1 was set to " + Util.toString(player.getLocation(), true));
                        return true;
                    case "pos2":
                        if (selections.get(player) == null) {
                            selections.put(player, new Selection(null, player.getLocation(), player.getWorld()));
                        } else {
                            Location pos1 = selections.get(player).getPos1();
                            Location pos2 = player.getLocation();
                            selections.put(player, new Selection(pos1, pos2, player.getWorld()));
                            Particles.box(BoundingBox.of(pos1, pos2), player.getWorld(), new ParticleData<>(Particle.END_ROD, null, 2), player, 0.2);
                        }
                        send(player, "&b&l> &7Position 2 was set to " + Util.toString(player.getLocation(), true));
                        return true;
                    case "save":
                        if (selection == null || !selection.isComplete()) {
                            send(player, "&8----------- &b&lCuboid &8-----------");
                            send(player, "&7Your cuboid isn't complete yet.");
                            send(player, "&7Be sure to set the first and second position!");
                            return true;
                        }

                        send(player, "&8----------- &b&lCuboid &8-----------");
                        send(player, "&7Your cuboid selection is being saved..");

                        LobbyArea area = new LobbyArea(player.getWorld().getName(),
                                Vector3D.fromBukkit(selection.getPos1().toVector()),
                                Vector3D.fromBukkit(selection.getPos2().toVector()));
                        if (!area.save()) {
                            send(player, "&cThere was an error while saving your cuboid!");
                            send(player, "&7Please try again.");
                            return true;
                        }

                        send(player, "&7Successfully saved your cuboid. Every active player will be kicked in order to prevent any transition issues.");

                        for (ParkourPlayer pp : ParkourUser.getActivePlayers()) {
                            ParkourUser.leave(pp);
                        }

                        IPEx.setCuboidArea(area);
                }
                return true;
            }
        } else if (args[0].equalsIgnoreCase("invite")) {
            Player other = Bukkit.getPlayer(args[1]);
            if (other == null) {
                send(sender, "&4&l> &7That player isn't online in this server!");
                return true;
            } else if (!(sender instanceof Player)) {
                return true;
            }
            Player pSender = (Player) sender;

            ParkourPlayer pp = ParkourPlayer.getPlayer(pSender);
            if (pp == null) {
                return true;
            }
            if (pp.getGenerator() instanceof DuelGenerator) {
                DuelGenerator gen = (DuelGenerator) pp.getGenerator();
                try {
                    gen.addPlayer(ParkourPlayer.register(other));
                } catch (Throwable throwable) {
                    Logging.stack("Error while registering " + other.getName(), "Please report this error to the developer!", throwable);
                }
            }

            send(other, "&4&l> &4&n" + pSender.getName() + "&7 has invited you to play ");
            send(other, "&4&l> &4&nClick here&7 to join. ");

            // todo accept and player handling
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