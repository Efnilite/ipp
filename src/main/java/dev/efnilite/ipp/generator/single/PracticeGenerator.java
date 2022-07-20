package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.menu.DynamicMenu;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.animation.WaveEastAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.SliderItem;
import org.bukkit.Material;

/**
 * Class for multiplayer
 */
public final class PracticeGenerator extends PlusGenerator {

    static {
        // practice settings only if player's generator is of this instance
        DynamicMenu.Reg.MAIN.registerMainItem(1, 3,
                user -> new Item(Material.COMPARATOR, "<#E74FA1><bold>Practice Settings").click(
                event -> {
                    ParkourPlayer pp = ParkourPlayer.getPlayer(event.getPlayer());
                    if (pp != null && pp.getGenerator() instanceof PracticeGenerator generator) {
                        generator.open();
                    }
                }),
                player -> {
                    ParkourPlayer pp = ParkourPlayer.getPlayer(player);
                    return pp != null && pp.getGenerator() instanceof PracticeGenerator;
                });
        // todo
    }

    public PracticeGenerator(Session session) {
        // setup generator settings
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_ADAPTIVE);

        // setup menu
        menu = new SettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.SPECIAL_BLOCKS);

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

        menu
                // each jump type uses their own specific key to prevent collision
                .item(9, new SliderItem()
                        .initial(distanceChances.containsKey(0) ? 0 : 1)
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>One-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {
                            distanceChances.put(0, 1); // keys 0-1
                            distanceChances.put(1, 2);

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>One-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {
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
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>Two-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {
                            distanceChances.put(2, 2); // keys 2-3
                            distanceChances.put(3, 3);

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Two-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {

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
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>Three-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {
                            distanceChances.put(4, 3); // keys 4-5
                            distanceChances.put(5, 4);

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Three-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {
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
                        .add(0, new Item(Material.LIME_STAINED_GLASS_PANE, "<green><bold>Four-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {
                            distanceChances.put(6, 4); // key 6

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Four-block jumps")
                                .lore("<gray>Click to change this setting."), event -> {

                            if (distanceChances.size() > 1) {
                                distanceChances.remove(6);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(18, new SliderItem()
                        .initial(defaultChances.containsKey(0) ? 0 : 1)
                        .add(0, new Item(Material.BARREL, "<green><bold>Normal")
                                .lore("<gray>Click to change this setting."), event -> {
                            defaultChances.put(0, 0); // key 0 for type

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Normal")
                                .lore("<gray>Click to change this setting."), event -> {

                            if (defaultChances.size() > 1) {
                                defaultChances.remove(0);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(19, new SliderItem()
                        .initial(specialChances.containsKey(0) ? 0 : 1)
                        .add(0, new Item(Material.ICE, "<green><bold>Ice")
                                .lore("<gray>Click to change this setting."), event -> {
                            defaultChances.put(1, 2); // key 1 for type
                            specialChances.put(0, 0); // key 0 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Ice")
                                .lore("<gray>Click to change this setting."), event -> {
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
                        .add(0, new Item(Material.SMOOTH_QUARTZ_SLAB, "<green><bold>Slabs")
                                .lore("<gray>Click to change this setting."), event -> {
                            defaultChances.put(2, 2); // key 2 for type
                            specialChances.put(1, 1); // key 1 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Slabs")
                                .lore("<gray>Click to change this setting."), event -> {
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
                        .add(0, new Item(Material.GLASS_PANE, "<green><bold>Glass Panes")
                                .lore("<gray>Click to change this setting."), event -> {
                            defaultChances.put(3, 2); // key 3 for type
                            specialChances.put(2, 2); // key 2 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Glass Panes")
                                .lore("<gray>Click to change this setting."), event -> {
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
                        .add(0, new Item(Material.OAK_FENCE, "<green><bold>Fences")
                                .lore("<gray>Click to change this setting."), event -> {
                            defaultChances.put(4, 2); // key 4 for type
                            specialChances.put(3, 3); // key 3 for special

                            return true;
                        })
                        .add(1, new Item(Material.RED_STAINED_GLASS_PANE, "<red><bold>Fences")
                                .lore("<gray>Click to change this setting."), event -> {
                            if (defaultChances.size() > 1) {
                                defaultChances.remove(4);
                                specialChances.remove(3);

                                return true;
                            } else {
                                return false;
                            }
                        }))

                .item(27, IP.getConfiguration().getFromItemData(player.getLocale(), "general.close")
                        .click(event -> menu()))

                .animation(new WaveEastAnimation())
                .fillBackground(Material.CYAN_STAINED_GLASS_PANE)
                .distributeRowEvenly(0, 1, 2, 3)
                .open(player.getPlayer());
    }

    @Override
    protected void calculateAdaptiveDistance() {

    }

    @Override
    protected void calculateDefault() {

    }

    @Override
    protected void calculateDistance() {

    }

    @Override
    protected void calculateSpecial() {

    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.PRACTICE;
    }
}