package dev.efnilite.ipp;

import dev.efnilite.ip.lib.vilib.command.ViCommand;
import dev.efnilite.ip.lib.vilib.particle.ParticleData;
import dev.efnilite.ip.lib.vilib.particle.Particles;
import dev.efnilite.ip.lib.vilib.util.Locations;
import dev.efnilite.ip.lib.vilib.util.Strings;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ipp.menu.ActiveMenu;
import dev.efnilite.ipp.menu.InviteMenu;
import dev.efnilite.ipp.menu.MultiplayerMenu;
import dev.efnilite.ipp.mode.lobby.Lobby;
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

    public static final HashMap<Player, Location[]> selections = new HashMap<>();

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
                    if (sender instanceof Player && PlusOption.ACTIVE.mayPerform(player)) {
                        ActiveMenu.open(player, ActiveMenu.MenuSort.LEAST_OPEN_FIRST);
                    }
                }
                case "create", "multiplayer" -> {
                    if (sender instanceof Player && PlusOption.MULTIPLAYER.mayPerform(player)) {
                        MultiplayerMenu.open(player);
                    }
                }
                case "invite" -> {
                    if (sender instanceof Player && PlusOption.INVITE.mayPerform(player)) {
                        InviteMenu.open(player);
                    }
                }
                case "reload" -> {
                    if (sender.hasPermission(ParkourOption.ADMIN.permission)) {
                        IPP.getConfiguration().reload();
                        send(sender, "%sReloaded all config files.".formatted(IPP.PREFIX));
                    }
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("lobbygm") && player != null && ParkourOption.ADMIN.mayPerform(sender)) {

                Location playerLocation = player.getLocation();
                Location[] existingSelection = selections.get(player);

                switch (args[1]) {
                    case "pos1" -> {
                        send(player, "%sPosition 1 was set to %s".formatted(IPP.PREFIX, Locations.toString(playerLocation, true)));

                        if (existingSelection == null) {
                            selections.put(player, new Location[]{playerLocation, null});
                            return true;
                        }

                        selections.put(player, new Location[]{playerLocation, existingSelection[1]});

                        Particles.box(BoundingBox.of(playerLocation, existingSelection[1]), player.getWorld(),
                                new ParticleData<>(Particle.END_ROD, null, 2), player, 0.2);
                    }
                    case "pos2" -> {
                        send(player, "%sPosition 2 was set to %s".formatted(IPP.PREFIX, Locations.toString(playerLocation, true)));

                        if (existingSelection == null) {
                            selections.put(player, new Location[]{null, playerLocation});
                            return true;
                        }

                        selections.put(player, new Location[]{existingSelection[0], playerLocation});

                        Particles.box(BoundingBox.of(existingSelection[0], playerLocation), player.getWorld(),
                                new ParticleData<>(Particle.END_ROD, null, 2), player, 0.2);
                    }
                    case "save" -> {
                        if (existingSelection == null || existingSelection[0] == null || existingSelection[1] == null) {
                            send(player, "%sYour lobby area isn't complete yet. Be sure to set the first and second position!".formatted(IPP.PREFIX));
                            return true;
                        }

                        BoundingBox bb = BoundingBox.of(existingSelection[0], existingSelection[1]);

                        if (bb.getWidthX() < Lobby.MINIMUM_SIZE ||
                                bb.getHeight() < Lobby.MINIMUM_SIZE ||
                                bb.getWidthZ() < Lobby.MINIMUM_SIZE) {
                            send(player, "%sYou haven't made the area big enough! It needs to be at least <bold>%d</bold> blocks in all directions.".formatted(IPP.PREFIX, Lobby.MINIMUM_SIZE));
                            return true;
                        }

                        send(player, "%sYour lobby area selection is being saved.".formatted(IPP.PREFIX));
                        Lobby.save(player.getWorld(), bb);
                    }
                }
            }
        }
        return true;
    }

    private void help(CommandSender sender) {
        send(sender, IPP.PREFIX + "Commands");
        send(sender, "");
        send(sender, "<#ff5050>/ipp create <dark_gray>- <gray>Create a multiplayer lobby");
        send(sender, "<#ff5050>/ipp lobbies <dark_gray>- <gray>View all multiplayer lobbies");
        send(sender, "<#ff5050>/ipp invite [player]<dark_gray>- <gray>Invite another player");
        if (ParkourOption.ADMIN.mayPerform(sender)) {
            send(sender, "<#ff5050>/ipp reload - <gray>Reload the plugin");
            send(sender, "<#ff5050>/ipp lobbygm <pos1/pos2/save><dark_gray>- <gray>Setup lobby mode selection");
        }
        send(sender, "");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 0 -> {
                completions.addAll(List.of("create", "lobbies", "invite"));
                if (ParkourOption.ADMIN.mayPerform(sender)) {
                    completions.add("reload");
                    completions.add("lobbygm");
                }
            }
            case 1 -> {
                if (args[0].equalsIgnoreCase("lobbygm") && ParkourOption.ADMIN.mayPerform(sender)) {
                    completions.addAll(List.of("pos1", "pos2", "save"));
                }
            }
            default -> {
                return Collections.emptyList();
            }
        }

        return completions;
    }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(Strings.colour(message));
    }
}