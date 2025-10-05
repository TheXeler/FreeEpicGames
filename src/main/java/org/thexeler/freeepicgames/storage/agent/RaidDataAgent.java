package org.thexeler.freeepicgames.storage.agent;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.storage.type.RaidType;
import org.thexeler.freeepicgames.storage.utils.DataUtils;
import org.thexeler.freeepicgames.storage.utils.ModSavedData;
import org.thexeler.freeepicgames.storage.view.RaidInstanceView;
import org.thexeler.freeepicgames.event.RaidEvent;
import oshi.util.tuples.Pair;

import java.util.*;

public class RaidDataAgent extends AbstractDataAgent {

    private static RaidDataAgent instance;

    private final JsonObject optionData;
    private final JsonObject raidInstanceData;

    private final Map<String, RaidInstanceView> raidInstances = Collections.synchronizedMap(new HashMap<>());

    private RaidDataAgent() {
        optionData = ModSavedData.getGlobalData("RaidSettings");
        if (FreeEpicGamesConfigs.isEnabledRaidCachePersistence) {
            raidInstanceData = ModSavedData.getGlobalData("RaidsInstanceCache");
        } else {
            raidInstanceData = new JsonObject();
        }

        load();
    }

    public static RaidDataAgent getInstance() {
        if (instance == null) {
            instance = new RaidDataAgent();
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
            NeoForge.EVENT_BUS.post(new RaidEvent.BuildEvent(raidInstanceView));

            raidInstances.put(uuid, raidInstanceView);
        }
        return raidInstanceView;
    }

    public void removeRaidInstance(RaidInstanceView raidInstanceView) {
        if (raidInstanceView.isActive()) {
            raidInstanceView.destroy();
        } else {
            raidInstances.remove(raidInstanceView.getId());
        }
    }

    public ChunkPos locateEmptyChunk(RaidType type) {
        int posX, posZ, level = 0;
        int sizeX = type.getSizeX(), sizeZ = type.getSizeZ();

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

    public Pair<String, Vec3> getBackPos(ServerPlayer player) {
        return RaidInstanceView.getBackPos(player);
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

    public static void expire() {
        instance = null;
    }
}
