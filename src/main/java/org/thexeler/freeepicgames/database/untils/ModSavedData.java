package org.thexeler.freeepicgames.database.untils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.database.agent.AbstractDataAgent;
import org.thexeler.freeepicgames.database.agent.GlobalJobDataAgent;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.slacker.mojang.level.CompatSavedData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModSavedData extends CompatSavedData {

    private static ModSavedData instance = null;

    private final Map<String, JsonObject> globalDataMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Map<String, JsonObject>> worldDataMap = Collections.synchronizedMap(new HashMap<>());

    private final Map<String, AbstractDataAgent> worldAgentMap = Collections.synchronizedMap(new HashMap<>());

    private ModSavedData() {
        super();

        instance = this;
    }

    public static JsonObject getGlobalData(String id) {
        return instance.globalDataMap.computeIfAbsent(id, key -> new JsonObject());
    }

    public static JsonObject getWorldData(Level world, String id) {
        return instance.worldDataMap.computeIfAbsent(world.dimension().toString(), key -> Collections.synchronizedMap(
                new HashMap<>())).computeIfAbsent(id, key -> new JsonObject());
    }

    public static ModSavedData load(CompoundTag compoundTag) {
        Gson gson = new Gson();
        ModSavedData savedData = new ModSavedData();
        CompoundTag globalTag = compoundTag.getCompound("Global");
        CompoundTag worldsTag = compoundTag.getCompound("Worlds");

        if (!globalTag.isEmpty()) {
            globalTag.getAllKeys().forEach(key -> savedData.globalDataMap.put(key, gson.fromJson(globalTag.get(key).getAsString(), JsonObject.class)));
        }

        if (!worldsTag.isEmpty()) {
            worldsTag.getAllKeys().forEach(key -> {
                Map<String, JsonObject> worldData = savedData.worldDataMap.computeIfAbsent(key, k -> new HashMap<>());
                CompoundTag worldTag = worldsTag.getCompound(key);

                if (!worldTag.isEmpty()) {
                    worldTag.getAllKeys().forEach(key2 -> worldData.put(key2, gson.fromJson(worldTag.get(key2).getAsString(), JsonObject.class)));
                }
                savedData.worldDataMap.put(key, worldData);
            });
        }
        return savedData;
    }

    public static ModSavedData load(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        Gson gson = new Gson();
        ModSavedData savedData = new ModSavedData();
        CompoundTag globalTag = compoundTag.getCompound("Global");
        CompoundTag worldsTag = compoundTag.getCompound("Worlds");

        if (!globalTag.isEmpty()) {
            globalTag.getAllKeys().forEach(key -> savedData.globalDataMap.put(key, gson.fromJson(globalTag.get(key).getAsString(), JsonObject.class)));
        }

        if (!worldsTag.isEmpty()) {
            worldsTag.getAllKeys().forEach(key -> {
                Map<String, JsonObject> worldData = savedData.worldDataMap.computeIfAbsent(key, k -> new HashMap<>());
                CompoundTag worldTag = worldsTag.getCompound(key);

                if (!worldTag.isEmpty()) {
                    worldTag.getAllKeys().forEach(key2 -> worldData.put(key2, gson.fromJson(worldTag.get(key2).getAsString(), JsonObject.class)));
                }
                savedData.worldDataMap.put(key, worldData);
            });
        }
        return savedData;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider registries) {
        GlobalJobDataAgent.getInstance().save();
        GlobalRaidDataAgent.getInstance().save();

        worldAgentMap.forEach((key, value) -> value.save());

        Gson gson = new Gson();
        CompoundTag globalTag = new CompoundTag();
        CompoundTag worldsTag = new CompoundTag();

        globalDataMap.forEach((key, value) -> globalTag.put(key, StringTag.valueOf(gson.toJson(value))));

        worldDataMap.forEach((key, value) -> {
            CompoundTag worldTag = new CompoundTag();

            value.forEach((key2, value2) -> worldTag.put(key2, StringTag.valueOf(gson.toJson(value2))));

            worldsTag.put(key, worldTag);
        });

        compoundTag.put("Global", globalTag);
        compoundTag.put("Worlds", worldsTag);
        return compoundTag;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    //  Compat fix
    public static ModSavedData create() {
        return new ModSavedData();
    }
}
