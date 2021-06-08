package xyz.ufactions.prolib.libs;

import org.bukkit.entity.Entity;
import xyz.ufactions.prolib.reflection.ReflectionUtils;

public class UtilEntity {

    private static final ReflectionUtils.RefClass CraftEntity = ReflectionUtils.getRefClass("{cb}.entity.CraftEntity");
    private static final ReflectionUtils.RefMethod GetHandle = CraftEntity.getMethod("getHandle");
    private static final ReflectionUtils.RefClass NBTTagCompound = ReflectionUtils.getRefClass("{nms}.NBTTagCompound");
    private static final ReflectionUtils.RefMethod SetInt = NBTTagCompound.getMethod("setInt", String.class, Integer.TYPE);
    private static final ReflectionUtils.RefClass NMSEntity = ReflectionUtils.getRefClass("{nms}.Entity");
    private static final ReflectionUtils.RefMethod MethodC = NMSEntity.getMethod("c", NBTTagCompound.getRealClass());
    private static final ReflectionUtils.RefMethod MethodF = NMSEntity.getMethod("f", NBTTagCompound.getRealClass());
    private static final ReflectionUtils.RefMethod GetTag = NMSEntity.getMethod("getNBTTag");

    public static void setAI(Entity entity, boolean hasAI) {
        Object nmsEntity = GetHandle.of(entity).call();
        Object tag = GetTag.of(nmsEntity).call();
        if (tag == null) tag = NBTTagCompound.getConstructor().create();
        MethodC.of(nmsEntity).call(tag);
        int value = hasAI ? 0 : 1;
        SetInt.of(tag).call("NoAI", value);
        MethodF.of(nmsEntity).call(tag);
    }
}