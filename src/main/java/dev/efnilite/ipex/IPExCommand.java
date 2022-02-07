package dev.efnilite.ipex;


import dev.efnilite.fycore.command.FyCommand;
import dev.efnilite.fycore.util.Task;
import dev.efnilite.ipex.generator.DuelGenerator;
import dev.efnilite.ipex.util.CuboidArea;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.gamemode.Gamemode;
import dev.efnilite.witp.command.MainCommand;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.schematic.Vector3D;
import dev.efnilite.witp.schematic.selection.Selection;
import dev.efnilite.witp.util.Logging;
import dev.efnilite.witp.util.Util;
import dev.efnilite.witp.util.inventory.InventoryBuilder;
import dev.efnilite.witp.util.particle.ParticleData;
import dev.efnilite.witp.util.particle.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class IPExCommand extends FyCommand {

    public static final HashMap<Player, Selection> selections = new HashMap<>();

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (args.length == 0) {

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
                        MainCommand.send(player, "&b&l> &7Position 1 was set to " + Util.toString(player.getLocation(), true));
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
                        MainCommand.send(player, "&b&l> &7Position 2 was set to " + Util.toString(player.getLocation(), true));
                        return true;
                    case "save":
                        if (selection == null || !selection.isComplete()) {
                            MainCommand.send(player, "&8----------- &b&lCuboid &8-----------");
                            MainCommand.send(player, "&7Your cuboid isn't complete yet.");
                            MainCommand.send(player, "&7Be sure to set the first and second position!");
                            return true;
                        }

                        MainCommand.send(player, "&8----------- &b&lCuboid &8-----------");
                        MainCommand.send(player, "&7Your cuboid selection is being saved..");

                        CuboidArea cuboidArea = new CuboidArea(player.getWorld().getName(),
                                Vector3D.fromBukkit(selection.getPos1().toVector()),
                                Vector3D.fromBukkit(selection.getPos2().toVector()));
                        try {
                            cuboidArea.save();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            MainCommand.send(player, "&cThere was an error while saving your cuboid!");
                            MainCommand.send(player, "&7Please try again.");
                            return true;
                        }
                        MainCommand.send(player, "&7Succesfully saved your cuboid. WITP needs to have 0 active players before it will apply.");

                        new Task()
                                .repeat(5 * 20)
                                .execute(new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (ParkourPlayer.getActivePlayers().size() == 0) {
                                            IPEx.setCuboidArea(cuboidArea);
                                            this.cancel();
                                        }
                                    }
                                })
                                .run();

                        return true;
                }
            } else if (args[0].equalsIgnoreCase("invite")) {
                Player other = Bukkit.getPlayer(args[1]);
                if (other == null) {
                    MainCommand.send(sender, "&4&l> &7That player isn't online in this server!");
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
                        gen.addPlayer(ParkourPlayer.register(other, null));
                    } catch (Throwable throwable) {
                        Logging.stack("Error while registering " + other.getName(), "Please report this error to the developer!", throwable);
                    }
                }

                MainCommand.send(other, "&4&l> &4&n" + pSender.getName() + "&7 has invited you to play ");
                MainCommand.send(other, "&4&l> &4&nClick here&7 to join. ");

                // todo accept and player handling
            } else if (args[0].equalsIgnoreCase("create")) {
                List<Gamemode> gamemodes = WITP.getRegistry().getGamemodes().stream().filter(t ->
                        t.getName().contains("m-")).collect(Collectors.toList());
                InventoryBuilder builder = new InventoryBuilder(2, "Select a multiplayer game");
                int index = 0;
                Player p = player;
                for (Gamemode gamemode : gamemodes) {
                    builder.setItem(index, gamemode.getItem(""), (t, e) -> {
                        gamemode.handleItemClick(p, null, null);
                    });  // todo add lang

                    index++;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}