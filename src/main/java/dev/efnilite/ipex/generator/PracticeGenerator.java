package dev.efnilite.ipex.generator;

import dev.efnilite.fycore.item.Item;
import dev.efnilite.witp.WITP;
import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.generator.base.GeneratorOption;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.util.Util;
import dev.efnilite.witp.util.inventory.InventoryBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class for multiplayer
 */
public final class PracticeGenerator extends DefaultGenerator {

    public PracticeType type;
    private final Set<String> enabledOptions;

    public PracticeGenerator(ParkourPlayer player) {
        super(player, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.DISABLE_ADAPTIVE);

        enabledOptions = new LinkedHashSet<>();
    }

    private void update() {
        defaultChances.clear();
        distanceChances.clear();
        specialChances.clear();

//        calculateDefault();
//        calculateDistance();
//        calculateSpecial();

        for (String option : enabledOptions) {
            switch (option) {
                case "ICE":
                    calculateDistance();

                    defaultChances.put(0, 2);
                    specialChances.put(0, 0);
                    break;
                case "SLAB":
                    calculateDistance();

                    defaultChances.put(0, 2);
                    specialChances.put(1, 1);
                    break;
                case "GLASS_PANE":
                    calculateDistance();

                    defaultChances.put(0, 2);
                    specialChances.put(2, 2);
                    break;
                case "FENCE":
                    calculateDistance();

                    defaultChances.put(0, 2);
                    specialChances.put(3, 3);
                    break;
                case "ONE":
                    calculateDefault();
                    calculateSpecial();

                    distanceChances.put(0, 1);
                    distanceChances.put(1, 2);
                    break;
                case "TWO":
                    calculateDefault();
                    calculateSpecial();

                    distanceChances.put(2, 2);
                    distanceChances.put(3, 3);
                    break;
                case "THREE":
                    calculateDefault();
                    calculateSpecial();

                    distanceChances.put(4, 3);
                    distanceChances.put(5, 4);
                    break;
                case "FOUR":
                    calculateDefault();
                    calculateSpecial();

                    distanceChances.put(6, 4);
                    break;
            }
        }
    }

    @Override
    public boolean hasAltMenu() {
        return true;
    }

    @Override
    public void altMenu() {
        InventoryBuilder builder = new InventoryBuilder(player, 3, "Practice").open();
        PracticeType[] types = PracticeType.values();
        InventoryBuilder.DynamicInventory dynamic = new InventoryBuilder.DynamicInventory(types.length, 1);
        for (PracticeType type : types) {
            String typeName = type.name();
            boolean isEnabled = enabledOptions.contains(typeName);
            Material material = isEnabled ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;

            Item item = new Item(material,  getColour(isEnabled) + Util.capitalizeFirst(typeName.toLowerCase().replace("_", " ")));
            if (isEnabled) {
                item.glowing().lore("&#757575Currently enabled");
            }

            builder.setItem(dynamic.next(), item.build(), (event, it) -> {
                String name = ChatColor.stripColor(it.getItemMeta().getDisplayName().toUpperCase().replace(" ", "_"));

                if (enabledOptions.contains(name)) {
                    enabledOptions.remove(name);
                } else {
                    enabledOptions.add(name);
                }

                update();
                altMenu();
            });
        }
        ItemStack close = WITP.getConfiguration().getFromItemData(player.locale, "general.close");
        builder.setItem(26, close, (t2, e2) -> player.getPlayer().closeInventory());
        builder.build();
    }

    private String getColour(boolean enabled)  {
        return enabled ? "&a&l" : "&c&l";
    }


    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }

    @Override
    public void menu() {
        super.handler.menu("structure", "difficulty", "special");
    }

    private enum PracticeType {
        ONE,
        TWO,
        THREE,
        FOUR,
        ICE,
        FENCE,
        SLAB,
        GLASS_PANE,
    }
}