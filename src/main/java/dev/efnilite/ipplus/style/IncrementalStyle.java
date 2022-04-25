package dev.efnilite.ipplus.style;

import dev.efnilite.ip.api.StyleType;
import dev.efnilite.ip.vilib.inventory.item.Item;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IncrementalStyle extends StyleType {

    private int count;

    @Override
    public @NotNull String getName() {
        return "incremental";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return new Item(Material.SCAFFOLDING, "<#348EDB><bold>Incremental");
    }

    @Override
    public Material get(String style) {
        List<Material> materials = styles.get(style);

        count++;
        if (count >= materials.size() - 1) {
            count = 0;
        }
        return materials.get(count);
    }
}
