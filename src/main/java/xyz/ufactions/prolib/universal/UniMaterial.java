package xyz.ufactions.prolib.universal;

import org.bukkit.Material;

public enum UniMaterial {

    WOODEN_AXE("WOOD_AXE", "WOODEN_AXE"),
    CLOCK("WATCH", "CLOCK"),
    REDSTONE_TORCH("REDSTONE_TORCH", "REDSTONE_TORCH_ON");

    private final Material material;

    UniMaterial(String... viableNames) {
        Material m = Material.AIR;
        for (String name : viableNames) {
            try {
                m = Material.valueOf(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
        this.material = m;
    }

    public final Material get() {
        return material;
    }
}