package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusLocales;
import dev.efnilite.ipp.mode.PlusMode;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.vilib.inventory.item.SliderItem;
import org.bukkit.Material;

/**
 * Class for multiplayer
 */
public final class PracticeGenerator extends PlusGenerator {

    public PracticeGenerator(Session session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_ADAPTIVE);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC, ParkourOption.SCORE_DIFFICULTY, ParkourOption.SPECIAL_BLOCKS);

        // generate default chances, copied from the menu
        distanceChances.put(0, 1); // keys 0-1
        distanceChances.put(1, 2);
        distanceChances.put(2, 2); // keys 2-3
        distanceChances.put(3, 3);
        distanceChances.put(4, 3); // keys 4-5
        distanceChances.put(5, 4);
        distanceChances.put(6, 4); // key 6

        defaultChances.put(0, 0); // key 0 for type
        defaultChances.put(1, 2); // key 1 for type
        specialChances.put(0, 0); // key 0 for special
        defaultChances.put(2, 2); // key 2 for type
        specialChances.put(1, 1); // key 1 for special
        defaultChances.put(3, 2); // key 3 for type
        specialChances.put(2, 2); // key 2 for special
        defaultChances.put(4, 2); // key 4 for type
        specialChances.put(3, 3); // key 3 for special
    }

    /**
     * Opens the menu
     */
    public void open() {
        Menu menu = new Menu(4, "<white>Practice");
        String locale = player.getLocale();

        menu
                // each jump type uses their own specific key to prevent collision
                .item(9, new SliderItem()
                        .initial(distanceChances.containsKey(0) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.one_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            distanceChances.put(0, 1); // keys 0-1
                            distanceChances.put(1, 2);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.one_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (distanceChances.size() > 2) {
                                distanceChances.remove(0);
                                distanceChances.remove(1);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(10, new SliderItem()
                        .initial(distanceChances.containsKey(2) ? 0 : 1)
                        .add(0,  PlusLocales.getItem(locale, "play.single.practice.items.two_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            distanceChances.put(2, 2); // keys 2-3
                            distanceChances.put(3, 3);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.two_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {

                            if (distanceChances.size() > 2) {
                                distanceChances.remove(2);
                                distanceChances.remove(3);
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
                            distanceChances.put(4, 3); // keys 4-5
                            distanceChances.put(5, 4);

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.three_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (distanceChances.size() > 2) {
                                distanceChances.remove(4);
                                distanceChances.remove(5);

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
                            distanceChances.put(6, 4); // key 6

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.four_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {

                            if (distanceChances.size() > 1) {
                                distanceChances.remove(6);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(18, new SliderItem()
                        .initial(defaultChances.containsKey(0) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.normal")
                                .material(Material.BARREL)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(0, 0); // key 0 for type

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.normal")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {

                            if (defaultChances.size() > 1) {
                                defaultChances.remove(0);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(19, new SliderItem()
                        .initial(specialChances.containsKey(0) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.ice")
                                .material(Material.ICE)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(1, 2); // key 1 for type
                            specialChances.put(0, 0); // key 0 for special

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.ice")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(1);
                                specialChances.remove(0);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(20, new SliderItem()
                        .initial(specialChances.containsKey(1) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.slabs")
                                .material(Material.SMOOTH_QUARTZ_SLAB)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(2, 2); // key 2 for type
                            specialChances.put(1, 1); // key 1 for special

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.slabs")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(2);
                                specialChances.remove(1);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(21, new SliderItem()
                        .initial(specialChances.containsKey(2) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.glass_panes")
                                .material(Material.GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(3, 2); // key 3 for type
                            specialChances.put(2, 2); // key 2 for special

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.glass_panes")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(3);
                                specialChances.remove(2);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(22, new SliderItem()
                        .initial(specialChances.containsKey(3) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.fences")
                                .material(Material.OAK_FENCE)
                                .modifyName(name -> "<green>" + name), event -> {
                            defaultChances.put(4, 2); // key 4 for type
                            specialChances.put(3, 3); // key 3 for special

                            return true;
                        })
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.fences")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(4);
                                specialChances.remove(3);

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
    public void calculateAdaptiveDistance() {

    }

    @Override
    public void calculateDefault() {

    }

    @Override
    public void calculateDistance() {

    }

    @Override
    public void calculateSpecial() {

    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }

    @Override
    public Mode getMode() {
        return PlusMode.PRACTICE;
    }
}