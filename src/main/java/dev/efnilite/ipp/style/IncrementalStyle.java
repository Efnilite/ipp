package dev.efnilite.ipp.style;

import dev.efnilite.ip.style.Style;
import org.bukkit.Material;

import java.util.List;

public final class IncrementalStyle implements Style {

    private final String name;
    private final List<Material> materials;
    private int idx = 0;

    public IncrementalStyle(String name, List<Material> materials) {
        this.name = name;
        this.materials = materials;
    }

    @Override
    public Material getNext() {
        var material = materials.get(idx);

        idx++;
        if (idx >= materials.size()) {
            idx = 0;
        }

        return material;
    }

    @Override
    public String getName() {
        return name;
    }
}
