package xyz.ufactions.prolib.universal;

import org.bukkit.Material;

public enum UniMaterial {

    WOODEN_AXE("WOOD_AXE", "WOODEN_AXE"),
    CLOCK("WATCH", "CLOCK"),
    REDSTONE_TORCH("REDSTONE_TORCH", "REDSTONE_TORCH_ON");

    private final String LegacyName;
    private final String LatestName;

    UniMaterial(String legacyName, String latestName) {
        LegacyName = legacyName;
        LatestName = latestName;
    }

    public final Material get() {
        return Material.getMaterial(Universal.getInstance().isLegacyMaterialNames() ? LegacyName : LatestName);
    }
}