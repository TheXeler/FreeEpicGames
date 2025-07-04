package org.thexeler.freeepicgames.database.agent;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.database.type.RaidType;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.untils.ModSavedData;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;

import java.util.*;

public class GlobalRaidDataAgent implements AbstractDataAgent {

    private static GlobalRaidDataAgent instance;

    // TODO
    private final JsonObject optionData;
    private final JsonObject raidInstanceData;

    private final Map<String, RaidInstanceView> raidInstances = Collections.synchronizedMap(new HashMap<>());

    private GlobalRaidDataAgent() {
        optionData = ModSavedData.getGlobalData("RaidSettings");
        if (FreeEpicGamesConfigs.isEnabledRaidCachePersistence) {
            raidInstanceData = ModSavedData.getGlobalData("RaidsInstanceCache");
        } else {
            raidInstanceData = new JsonObject();
        }

        load();
    }

    public static GlobalRaidDataAgent getInstance() {
        if (instance == null) {
            instance = new GlobalRaidDataAgent();
        }
        return instance;
    }

    @Nullable
    public RaidInstanceView createRaidInstance(RaidType type) {
        RaidInstanceView raidInstanceView = null;
        if (type != null) {
            String uuid = UUID.randomUUID().toString();

            raidInstanceView = new RaidInstanceView(uuid, type, locateEmptyChunk(type));

            raidInstanceView.build();
            // TODO

            raidInstances.put(uuid, raidInstanceView);
        }
        return raidInstanceView;
    }

    public ChunkPos locateEmptyChunk(RaidType type) {
        int posX, posZ, level = 0;
        int sizeX = type.getSize().getX(), sizeZ = type.getSize().getZ();

        while (true) {
            posX = 0;
            posZ = level;
            while (posX < level) {
                int negationX = posX * -1, negationZ = posZ * -1;
                if (DataUtils.isChunkEmpty(posX, posZ) && DataUtils.isChunkEmpty(posX + sizeX, posZ + sizeZ)) {
                    return new ChunkPos(posX, posZ);
                }
                if (DataUtils.isChunkEmpty(posX, negationZ) && DataUtils.isChunkEmpty(posX + sizeX, negationZ + sizeZ)) {
                    return new ChunkPos(posX, negationZ);
                }
                if (DataUtils.isChunkEmpty(negationX, posZ) && DataUtils.isChunkEmpty(negationX + sizeX, posZ + sizeZ)) {
                    return new ChunkPos(negationX, posZ);
                }
                if (DataUtils.isChunkEmpty(negationX, negationZ) && DataUtils.isChunkEmpty(negationX + sizeX, negationZ + sizeZ)) {
                    return new ChunkPos(negationX, negationZ);
                }
                posX++;
            }

            posZ = 0;
            while (posZ < (level - 1)) {
                int negationX = posX * -1, negationZ = posZ * -1;
                if (DataUtils.isChunkEmpty(posX, posZ) && DataUtils.isChunkEmpty(posX + sizeX, posZ + sizeZ)) {
                    return new ChunkPos(posX, posZ);
                }
                if (DataUtils.isChunkEmpty(posX, negationZ) && DataUtils.isChunkEmpty(posX + sizeX, negationZ + sizeZ)) {
                    return new ChunkPos(posX, negationZ);
                }
                if (DataUtils.isChunkEmpty(negationX, posZ) && DataUtils.isChunkEmpty(negationX + sizeX, posZ + sizeZ)) {
                    return new ChunkPos(negationX, posZ);
                }
                if (DataUtils.isChunkEmpty(negationX, negationZ) && DataUtils.isChunkEmpty(negationX + sizeX, negationZ + sizeZ)) {
                    return new ChunkPos(negationX, negationZ);
                }
                posZ++;
            }
            level++;
        }
    }

    public Collection<RaidInstanceView> getAllRaidInstance() {
        return raidInstances.values();
    }

    @Nullable
    public RaidInstanceView getRaidInstance(ServerPlayer player) {
        return RaidInstanceView.getRaidInstanceFromPlayer(player);
    }

    @Nullable
    public RaidInstanceView getRaidInstance(String name) {
        return raidInstances.get(name);
    }

    @Override
    public void load() {
        if (FreeEpicGamesConfigs.isEnabledRaidCachePersistence) {
            raidInstanceData.asMap().forEach((key, value) ->
                    raidInstances.put(key, new RaidInstanceView(value.getAsJsonObject())));
        }
    }

    @Override
    public void save() {
        if (FreeEpicGamesConfigs.isEnabledRaidCachePersistence) {
            DataUtils.computeCacheViewMap(raidInstances, raidInstanceData);
        }
    }
}
