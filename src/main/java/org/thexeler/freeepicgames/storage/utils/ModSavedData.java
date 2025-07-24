package org.thexeler.freeepicgames.storage.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.storage.agent.AbstractWorldDataAgent;
import org.thexeler.freeepicgames.storage.agent.JobDataAgent;
import org.thexeler.freeepicgames.storage.agent.RaidDataAgent;

import java.util.*;

public class ModSavedData extends SavedData {

    private static ModSavedData instance = null;

    private final Map<String, JsonObject> globalDataMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Map<String, JsonObject>> worldDataMap = Collections.synchronizedMap(new HashMap<>());

    private final List<AbstractWorldDataAgent> worldAgentList = Collections.synchronizedList(new ArrayList<>());

    private ModSavedData() {
        super();
        instance = this;
    }

    public static JsonObject getGlobalData(String id) {
        return instance.globalDataMap.computeIfAbsent(id, key -> new JsonObject());
    }

    public static JsonObject getWorldData(ServerLevel world, String id) {
        return instance.worldDataMap.computeIfAbsent(world.dimension().toString(), key -> Collections.synchronizedMap(
                new HashMap<>())).computeIfAbsent(id, key -> new JsonObject());
    }

    public static void register(@NotNull AbstractWorldDataAgent agent) {
        if (!instance.worldAgentList.contains(agent)) {
            instance.worldAgentList.add(agent);
        }
    }

    public static ModSavedData create() {
        return new ModSavedData();
    }

    public static ModSavedData load(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        Gson gson = new Gson();
        ModSavedData savedData = create();

        CompoundTag globalTag = compoundTag.getCompound("Global");
        CompoundTag worldsTag = compoundTag.getCompound("Worlds");

        if (!globalTag.isEmpty()) {
            globalTag.getAllKeys().forEach(key -> {
                Tag tag = globalTag.get(key);
                if (tag instanceof StringTag) {
                    savedData.globalDataMap.put(key, gson.fromJson(tag.getAsString(), JsonObject.class));
                }
            });
        }

        if (!worldsTag.isEmpty()) {
            worldsTag.getAllKeys().forEach(key -> {
                Map<String, JsonObject> worldData = savedData.worldDataMap.computeIfAbsent(key, k -> new HashMap<>());
                CompoundTag worldTag = worldsTag.getCompound(key);

                if (!worldTag.isEmpty()) {
                    worldTag.getAllKeys().forEach(key2 -> {
                        Tag tag = worldTag.get(key2);
                        if (tag instanceof StringTag) {
                            worldData.put(key2, gson.fromJson(tag.getAsString(), JsonObject.class));
                        }
                    });
                }
                savedData.worldDataMap.put(key, worldData);
            });
        }

        return savedData;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider registries) {
        JobDataAgent.getInstance().save();
        RaidDataAgent.getInstance().save();

        worldAgentList.forEach(AbstractWorldDataAgent::save);

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
}
