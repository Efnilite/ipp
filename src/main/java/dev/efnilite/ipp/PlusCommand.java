package dev.efnilite.ipp;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.schematic.selection.Dimensions;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.menu.ActiveMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.mode.LobbyMode;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.particle.ParticleData;
import dev.efnilite.vilib.particle.Particles;
import dev.efnilite.vilib.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
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
            help(sender);
            return true;
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "lobbies", "lobby" -> {
                    if (sender instanceof Player && PlusOption.ACTIVE.check(player)) {
                        ActiveMenu.open(player, ActiveMenu.MenuSort.LEAST_OPEN_FIRST);
                    }
                    return true;
                }
                case "create", "multiplayer" -> {
                    if (sender instanceof Player && PlusOption.MULTIPLAYER.check(player)) {
                        MultiplayerMenu.open(player);
                    }
                    return true;
                }
                case "invite" -> {
                    if (sender instanceof Player && PlusOption.INVITE.check(player)) {
                        InviteMenu.open(player);
                    }
                    return true;
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("lobbygm") && player != null && ParkourOption.ADMIN.check(sender)) {
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
                        Util.send(player, "<blue><bold>> <gray>Position 1 was set to " + Locations.toString(player.getLocation(), true));
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
                        Util.send(player, "<blue><bold>> <gray>Position 2 was set to " + Locations.toString(player.getLocation(), true));
                        return true;
                    }
                    case "save" -> {
                        if (selection == null || !selection.isComplete()) {
                            Util.send(player, "<gray>Your lobby area isn't complete yet.");
                            Util.send(player, "<gray>Be sure to set the first and second position!");
                            return true;
                        } else {
                            Dimensions dimensions = selection.getDimensions();
                            if (dimensions.getWidth() < LobbyMode.MINIMUM_SIZE ||
                                    dimensions.getHeight() < LobbyMode.MINIMUM_SIZE ||
                                    dimensions.getLength() < LobbyMode.MINIMUM_SIZE) {
                                Util.send(player, "<gray>You haven't made the area big enough!");
                                Util.send(player, "<gray>It needs to be at least " + LobbyMode.MINIMUM_SIZE + " blocks in all directions.");
                                return true;
                            }
                        }
                        Util.send(player, "<gray>Your lobby area selection is being saved.");
                        LobbyMode.save(player.getWorld(), selection);
                    }
                }
            }
            return true;
        }
        return true;
    }

    private void help(CommandSender sender) {
        Util.send(sender, "<#841414><red>Infinite Parkour+ Commands");
        Util.send(sender, "");
        Util.send(sender, "<#C01E1E>/ipp create <dark_gray>- <gray>Create a multiplayer lobby");
        Util.send(sender, "<#C01E1E>/ipp lobbies <dark_gray>- <gray>View all multiplayer lobbies");
        Util.send(sender, "<#C01E1E>/ipp invite [player]<dark_gray>- <gray>Invite another player");
        if (ParkourOption.ADMIN.check(sender)) {
            Util.send(sender, "<#C01E1E>/ipp lobbygm <pos1/pos2/save><dark_gray>- <gray>Setup lobby mode selection");
        }
        Util.send(sender, "");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 0 -> {
                completions.addAll(List.of("create", "lobbies", "invite"));
                if (ParkourOption.ADMIN.check(sender)) {
                    completions.add("lobbygm");
                }
            }
            case 1 -> {
                if (args[0].equalsIgnoreCase("lobbygm") && ParkourOption.ADMIN.check(sender)) {
                    completions.addAll(List.of("pos1", "pos2", "save"));
                }
            }
            default -> {
                return Collections.emptyList();
            }
        }

        return completions;
    }
}