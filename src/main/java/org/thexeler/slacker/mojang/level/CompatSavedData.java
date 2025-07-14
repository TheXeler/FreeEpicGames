package org.thexeler.slacker.mojang.level;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.slacker.SlackerForge;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class CompatSavedData extends SavedData {

    public static class Factory<T extends SavedData>{
        public Supplier<T> constructor;
        public BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer;
        public @Nullable DataFixTypes type;
        public Factory(Supplier<T> constructor, BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer) {
            this.constructor = constructor;
            this.deserializer = deserializer;
            this.type = null;
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag pCompoundTag) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            SlackerForge.LOGGER.error("Try to save SavedData on client side.");
            throw new NullPointerException();
        }
        return save(pCompoundTag, server.registryAccess());
    }

    public abstract CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider registries);
}
