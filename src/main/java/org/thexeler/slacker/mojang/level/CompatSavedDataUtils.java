package org.thexeler.slacker.mojang.level;

import net.minecraft.server.level.ServerLevel;

public class CompatSavedDataUtils {
    public static <T extends CompatSavedData> T get(ServerLevel level, CompatSavedData.Factory<T> factory, String name) {
        //TODO
        //return level.getDataStorage().get(factory.deserializer(), factory.type(), name)
        return null;
    }

    public static <T extends CompatSavedData> T computeIfAbsent(ServerLevel level, CompatSavedData.Factory<T> factory, String name) {
        return null;
    }
}
