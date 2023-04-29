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
        super(session, GeneratorOption.DISABLE_SCHEMATICS);

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

        defaultChances.clear();
        defaultChances.put(JumpType.DEFAULT, 1.0);
        defaultChances.put(JumpType.SPECIAL, 1.0);
    }

    /**
     * Opens the menu
     */
    public void open() {
        Menu menu = new Menu(4, "<white>Practice");
        String locale = player.locale;

        menu
                .item(9, new SliderItem()
                        .initial(distanceChances.containsKey(1) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.one_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> handleDistanceOn(1))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.one_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleDistanceOff(1)))

                .item(10, new SliderItem()
                        .initial(distanceChances.containsKey(2) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.two_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> handleDistanceOn(2))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.two_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleDistanceOff(2)))

                .item(11, new SliderItem()
                        .initial(distanceChances.containsKey(3) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.three_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> handleDistanceOn(3))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.three_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleDistanceOff(3)))

                .item(12, new SliderItem()
                        .initial(distanceChances.containsKey(4) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.four_block")
                                .material(Material.LIME_STAINED_GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> handleDistanceOn(4))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.four_block")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleDistanceOff(4)))

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
                            if (defaultChances.size() == 1) {
                                return false;
                            }

                            defaultChances.remove(JumpType.DEFAULT);
                            return true;
                        }))

                .item(19, new SliderItem()
                        .initial(specialChances.containsKey(PACKED_ICE) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.ice")
                                .material(Material.ICE)
                                .modifyName(name -> "<green>" + name), event -> handleSpecialOn(PACKED_ICE))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.ice")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleSpecialOff(PACKED_ICE)))

                .item(20, new SliderItem()
                        .initial(specialChances.containsKey(SMOOTH_QUARTZ_SLAB) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.slabs")
                                .material(Material.SMOOTH_QUARTZ_SLAB)
                                .modifyName(name -> "<green>" + name), event -> handleSpecialOn(SMOOTH_QUARTZ_SLAB))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.slabs")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleSpecialOff(SMOOTH_QUARTZ_SLAB)))

                .item(21, new SliderItem()
                        .initial(specialChances.containsKey(GLASS_PANE) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.glass_panes")
                                .material(Material.GLASS_PANE)
                                .modifyName(name -> "<green>" + name), event -> handleSpecialOn(GLASS_PANE))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.glass_panes")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleSpecialOff(GLASS_PANE)))

                .item(22, new SliderItem()
                        .initial(specialChances.containsKey(OAK_FENCE) ? 0 : 1)
                        .add(0, PlusLocales.getItem(locale, "play.single.practice.items.fences")
                                .material(Material.OAK_FENCE)
                                .modifyName(name -> "<green>" + name), event -> handleSpecialOn(OAK_FENCE))
                        .add(1, PlusLocales.getItem(locale, "play.single.practice.items.fences")
                                .material(Material.RED_STAINED_GLASS_PANE)
                                .modifyName(name -> "<red>" + name), event -> handleSpecialOff(OAK_FENCE)))

                .item(27, Locales.getItem(player.locale, "other.close")
                        .click(event -> menu()))

                .animation(new WaveEastAnimation())
                .fillBackground(Material.CYAN_STAINED_GLASS_PANE)
                .distributeRowEvenly(0, 1, 2, 3)
                .open(player.player);
    }

    private boolean handleDistanceOn(int distance) {
        distanceChances.put(distance, 1.0);
        return true;
    }

    private boolean handleDistanceOff(int distance) {
        if (distanceChances.size() == 1) {
            return false;
        }

        distanceChances.remove(distance);
        return true;
    }

    private boolean handleSpecialOn(BlockData type) {
        defaultChances.put(JumpType.SPECIAL, 1.0);
        specialChances.put(type, 1.0);
        return true;
    }

    private boolean handleSpecialOff(BlockData type) {
        if (defaultChances.size() == 1) {
            return false;
        }

        if (specialChances.size() == 1) {
            defaultChances.remove(JumpType.SPECIAL);
        }
        specialChances.remove(type);
        return true;
    }

    @Override
    public Mode getMode() {
        return PlusMode.PRACTICE;
    }
}