package dev.efnilite.ipex.generator;

import dev.efnilite.witp.ParkourMenu;
import dev.efnilite.witp.ParkourOption;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.fycore.inventory.Menu;
import dev.efnilite.witp.fycore.inventory.item.Item;
import dev.efnilite.witp.fycore.inventory.item.SliderItem;
import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.generator.base.GeneratorOption;
import dev.efnilite.witp.player.ParkourPlayer;
import org.bukkit.Material;

/**
 * Class for multiplayer
 */
public final class PracticeGenerator extends DefaultGenerator {

    public PracticeGenerator(ParkourPlayer player) {
        super(player, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.DISABLE_ADAPTIVE);

        defaultChances.clear();
        distanceChances.clear();
        specialChances.clear();
    }

    public void altMenu() {
        Menu menu = new Menu(3, "Practice");

        menu
                // each jump type uses their own specific key to prevent collision
                .item(9, new SliderItem()
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>One-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.put(0, 1); // keys 0-1
                            distanceChances.put(1, 2);

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>One-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.remove(0);
                            distanceChances.remove(1);

                            return true;
                        }))

                .item(10, new SliderItem()
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>Two-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.put(2, 2); // keys 2-3
                            distanceChances.put(3, 3);

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Two-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.remove(2);
                            distanceChances.remove(3);

                            return true;
                        }))

                .item(11, new SliderItem()
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>Three-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.put(4, 3); // keys 4-5
                            distanceChances.put(5, 4);

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Three-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.remove(4);
                            distanceChances.remove(5);

                            return true;
                        }))

                .item(12, new SliderItem()
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>Four-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.put(6, 4); // key 6

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Four-block")
                                .lore("&7Click to change this setting."), (event) -> {
                            distanceChances.remove(6);

                            return true;
                        }))


                .item(13, new SliderItem()
                        .add(0, new Item(Material.ICE, "<green><bold>Ice")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.put(0, 2); // key 0 for type
                            specialChances.put(0, 0); // key 0 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Ice")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.remove(0);
                            specialChances.remove(0);

                            return true;
                        }))

                .item(14, new SliderItem()
                        .add(0, new Item(Material.SMOOTH_QUARTZ_SLAB, "<green><bold>Slabs")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.put(1, 2); // key 1 for type
                            specialChances.put(1, 1); // key 1 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Slabs")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.remove(1);
                            specialChances.remove(1);

                            return true;
                        }))

                .item(15, new SliderItem()
                        .add(0, new Item(Material.GLASS_PANE, "<green><bold>Glass Panes")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.put(2, 2); // key 2 for default
                            specialChances.put(2, 2); // key 2 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Glass Panes")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.remove(2);
                            specialChances.remove(2);

                            return true;
                        }))

                .item(16, new SliderItem()
                        .add(0, new Item(Material.OAK_FENCE, "<green><bold>Fences")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.put(3, 2); // key 3 for default
                            specialChances.put(3, 3); // key 3 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Fences")
                                .lore("&7Click to change this setting."), (event) -> {
                            defaultChances.remove(3);
                            specialChances.remove(3);

                            return true;
                        }))

                .item(26, WITP.getConfiguration().getFromItemData(player.getLocale(), "general.close")
                        .click((event) -> player.getPlayer().closeInventory()))

                .distributeRowEvenly(1)
                .open(player.getPlayer());
    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }

    @Override
    public void menu() {
        ParkourMenu.openMainMenu(player, ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.SPECIAL_BLOCKS);
    }
}