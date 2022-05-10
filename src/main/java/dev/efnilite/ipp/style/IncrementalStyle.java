package dev.efnilite.ipp.style;

import dev.efnilite.ip.api.StyleType;
import dev.efnilite.vilib.inventory.item.Item;
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
        return new Item(Material.SCAFFOLDING, "<#348EDB><bold>Incremental")
                .lore("<dark_gray>Inkrementell • 增加的", "<dark_gray>• Incrémentale • 増分 • Incrementeel");
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
