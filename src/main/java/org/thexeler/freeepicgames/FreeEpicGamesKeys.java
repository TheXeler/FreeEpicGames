package org.thexeler.freeepicgames;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FreeEpicGamesKeys {
    public static ResourceKey<Level> RAID_WORLD_KEY = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("freeepicgames", "raid_dim"));
}
