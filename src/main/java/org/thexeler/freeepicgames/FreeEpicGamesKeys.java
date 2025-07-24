package org.thexeler.freeepicgames;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FreeEpicGamesKeys {
    public static ResourceKey<Level> RAID_WORLD_KEY = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryBuild(FreeEpicGames.MOD_ID, "raid_dim"));

    public static ResourceKey<Level> parseWorldKey(String res) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(res));
    }
}
