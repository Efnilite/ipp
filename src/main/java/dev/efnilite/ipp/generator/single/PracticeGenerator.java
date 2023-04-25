package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.mode.PlusMode;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.vilib.inventory.item.SliderItem;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Class for multiplayer
 */
public final class PracticeGenerator extends PlusGenerator {

    // ensure same instances to ensure working ==
    private static final BlockData PACKED_ICE = Material.PACKED_ICE.createBlockData();
    private static final BlockData SMOOTH_QUARTZ_SLAB = Material.SMOOTH_QUARTZ_SLAB.createBlockData("[type=bottom]");
    private static final BlockData GLASS_PANE = Material.GLASS_PANE.createBlockData();
    private static final BlockData OAK_FENCE = Material.OAK_FENCE.createBlockData();

    public PracticeGenerator(Session session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC, ParkourOption.SPECIAL_BLOCKS);

        distanceChances.clear();
        distanceChances.put(1, 1.0);
        distanceChances.put(2, 1.0);
        distanceChances.put(3, 1.0);
        distanceChances.put(4, 1.0);

        specialChances.clear();
        specialChances.put(PACKED_ICE, 1.0);
        specialChances.put(SMOOTH_QUARTZ_SLAB, 1.0);
        specialChances.put(GLASS_PANE, 1.0);
        specialChances.put(OAK_FENCE, 1.0);
    }

    /**
     * Opens the menu
     */
    public void open() {
        Menu menu = new Menu(4, "<white>Practice");
        String locale = player.locale;

        menu
                // each jump type uses their own specific key to prevent collision
                .item(9, new SliderItem()
                        .initial(distanceChances.containsKey(1) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.one_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            distanceChances.put(1, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.one_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (distanceChances.size() > 1) {
                                distanceChances.remove(1);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(10, new SliderItem()
                        .initial(distanceChances.containsKey(2) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.two_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            distanceChances.put(2, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.two_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {

                            if (distanceChances.size() > 2) {
                                distanceChances.remove(2);
                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(11, new SliderItem()
                        .initial(distanceChances.containsKey(4) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.three_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            distanceChances.put(3, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.three_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (distanceChances.size() > 2) {
                                distanceChances.remove(3);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(12, new SliderItem()
                        .initial(distanceChances.containsKey(6) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.four_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            distanceChances.put(4, 1.0); // key 6

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.four_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {

                            if (distanceChances.size() > 1) {
                                distanceChances.remove(4);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(18, new SliderItem()
                        .initial(defaultChances.containsKey(JumpType.DEFAULT) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.normal")
                                .material(Material.BARREL)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(JumpType.DEFAULT, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.normal")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {

                            if (defaultChances.size() > 1) {
                                defaultChances.remove(JumpType.DEFAULT);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(19, new SliderItem()
                        .initial(specialChances.containsKey(PACKED_ICE) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.ice")
                                .material(Material.ICE)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(JumpType.SPECIAL, 1.0);
                            specialChances.put(PACKED_ICE, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.ice")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(JumpType.SPECIAL);
                                specialChances.remove(PACKED_ICE);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(20, new SliderItem()
                        .initial(specialChances.containsKey(SMOOTH_QUARTZ_SLAB) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.slabs")
                                .material(Material.SMOOTH_QUARTZ_SLAB)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(JumpType.SPECIAL, 1.0); // key 2 for type
                            specialChances.put(SMOOTH_QUARTZ_SLAB, 1.0); // key 1 for special

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.slabs")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(JumpType.SPECIAL);
                                specialChances.remove(SMOOTH_QUARTZ_SLAB);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(21, new SliderItem()
                        .initial(specialChances.containsKey(GLASS_PANE) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.glass_panes")
                                .material(Material.GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(JumpType.SPECIAL, 1.0);
                            specialChances.put(GLASS_PANE, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.glass_panes")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(JumpType.SPECIAL);
                                specialChances.remove(GLASS_PANE);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(22, new SliderItem()
                        .initial(specialChances.containsKey(OAK_FENCE) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.fences")
                                .material(Material.OAK_FENCE)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(JumpType.SPECIAL, 1.0);
                            specialChances.put(OAK_FENCE, 1.0);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.fences")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(JumpType.SPECIAL);
                                specialChances.remove(OAK_FENCE);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(27, Locales.getItem(player.locale, "other.close")
                        .click(event -> menu()))

                .animation(new WaveEastAnimation())
                .fillBackground(Material.CYAN_STAINED_GLASS_PANE)
                .distributeRowEvenly(0, 1, 2, 3)
                .open(player.player);
    }

    @Override
    public Mode getMode() {
        return PlusMode.PRACTICE;
    }
}