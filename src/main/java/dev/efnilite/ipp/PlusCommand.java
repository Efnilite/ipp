package dev.efnilite.ipp;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.menu.LobbyMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.schematic.selection.Dimensions;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ipp.menu.ActiveMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.mode.LobbyMode;
import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.particle.ParticleData;
import dev.efnilite.vilib.particle.Particles;
import dev.efnilite.vilib.util.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class PlusCommand extends ViCommand {

    public static final HashMap<Player, Selection> selections = new HashMap<>();

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "lobbies", "lobby" -> {
                    if (sender instanceof Player && sender.hasPermission("ip.lobbies.view")) {
                        LobbyMenu.INSTANCE.open(player);
                    }
                    return true;
                }
                case "create", "multiplayer" -> {
                    if (sender instanceof Player && sender.hasPermission("ip.sessions.create")) {
                        MultiplayerMenu.open(player);
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
            if (args[0].equalsIgnoreCase("lobbygm") && player != null && player.hasPermission("witp.schematic")) {
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
                        send(player, "<blue><bold>> <gray>Position 1 was set to " + Locations.toString(player.getLocation(), true));
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
                        send(player, "<blue><bold>> <gray>Position 2 was set to " + Locations.toString(player.getLocation(), true));
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

            Message.send(other, "");
            Message.send(other, IP.PREFIX + "You have been invited by " + player.getName() + " to join their " + pp.getSession().getGamemode().getName() + " game.");
            Message.send(other, "<dark_gray>Use <#3BC2C2><underline>/parkour join " + pp.getSessionId() + "</underline><dark_gray> to join.");
            Message.send(other, "");
        }
        return true;
    }

    private void help(CommandSender sender) {
        send(sender, "<#841414><red>Infinite Parkour+ Commands");
        send(sender, "");
        send(sender, "<#C01E1E>/ipp create <dark_gray>- <gray>Create a multiplayer lobby");
        send(sender, "<#C01E1E>/ipp lobbies <dark_gray>- <gray>View all multiplayer lobbies");
        send(sender, "<#C01E1E>/ipp invite [player]<dark_gray>- <gray>Invite another player");
        if (sender.hasPermission("witp.schematic")) {
            send(sender, "<#C01E1E>/ipp lobbygm <pos1/pos2/save><dark_gray>- <gray>Setup lobby mode selection");
        }
        send(sender, "");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 0 -> {
                completions.addAll(List.of("create", "lobbies", "invite"));
                if (sender.hasPermission("witp.schematic")) {
                    completions.add("lobbygm");
                }
            }
            case 1 -> {
                if (args[0].equalsIgnoreCase("lobbygm") && sender.hasPermission("witp.schematic")) {
                    completions.addAll(List.of("pos1", "pos2", "save"));
                }
            }
            default -> {
                return Collections.emptyList();
            }
        }

        return completions;
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(Message.parseFormatting(message));
    }
}