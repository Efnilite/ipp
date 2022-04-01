package dev.efnilite.ipex.style;

import dev.efnilite.witp.WITP;
import dev.efnilite.witp.api.StyleType;
import dev.efnilite.witp.fycore.inventory.item.Item;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CumulativeStyle extends StyleType {

    private int count;

    @Override
    public @NotNull String getName() {
        return "cumulative";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return WITP.getConfiguration().getFromItemData(locale, "styles.default");
    }

    @Override
    public Material get(String style) {
        List<Material> materials = styles.get(style);

        count++;
        if (count == styles.size() - 1) {
            count = 0;
        }
        return materials.get(count);
    }
}
