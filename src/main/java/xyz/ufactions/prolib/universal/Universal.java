package xyz.ufactions.prolib.universal;

import org.bukkit.Material;

public class Universal {

    private static Universal instance;

    public static Universal getInstance() {
        if (instance == null) instance = new Universal();
        return instance;
    }

    private boolean legacyMaterialNames;

    public Universal() {
        try {
            Material.valueOf("WOODEN_AXE");
            legacyMaterialNames = false;
        } catch (EnumConstantNotPresentException e) {
            legacyMaterialNames = true;
        }
    }

    public boolean isLegacyMaterialNames() {
        return legacyMaterialNames;
    }
}