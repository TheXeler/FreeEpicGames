package org.thexeler.freeepicgames.storage.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.events.RaidEvent;
import org.thexeler.freeepicgames.storage.agent.RaidDataAgent;
import org.thexeler.freeepicgames.storage.type.RaidTreasureType;
import org.thexeler.freeepicgames.storage.type.RaidType;
import org.thexeler.freeepicgames.storage.utils.DataUtils;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RaidInstanceView implements AbstractCacheView {
    private final static Map<String, RaidInstanceView> playerInstanceMappings = Collections.synchronizedMap(new HashMap<>());
    private final static Map<String, Pair<String, Vec3>> playerBackPosMappings = Collections.synchronizedMap(new HashMap<>());

    @Getter
    private final String id;
    @Getter
    private final RaidType baseType;

    @Getter
    private final ChunkPos startChunk, endChunk;
    private final Map<String, Vec3> playerCheckpointPosMap;
    private final Map<BlockPos, Container> sharedTreasuresMap;
    private final Map<String, Map<BlockPos, Container>> playerTreasuresMap;

    @Getter
    private boolean isActive;
    @Getter
    private final AABB frame;
    @Getter
    private final BlockPos blockPosOffset;

    public RaidInstanceView(String id, RaidType baseType, ChunkPos startChunk) {
        this.id = id;
        this.baseType = baseType;

        this.startChunk = startChunk;
        this.endChunk = new ChunkPos(
                SectionPos.blockToSectionCoord(startChunk.getMaxBlockX() + baseType.getSizeX()),
                SectionPos.blockToSectionCoord(startChunk.getMaxBlockZ() + baseType.getSizeZ()));

        this.playerCheckpointPosMap = new HashMap<>();

        this.sharedTreasuresMap = new HashMap<>();
        this.playerTreasuresMap = new HashMap<>();

        Vec3 startPos = new Vec3(startChunk.getMinBlockX(), 0, startChunk.getMinBlockZ());
        Vec3 endPos = new Vec3(endChunk.getMaxBlockX(), FreeEpicGames.RAID_WORLD.getMaxBuildHeight(), endChunk.getMaxBlockZ());
        this.frame = new AABB(startPos, endPos);
        this.isActive = false;

        this.blockPosOffset = new BlockPos(startChunk.getMinBlockX(), 0, startChunk.getMinBlockZ());
    }

    public RaidInstanceView(JsonObject object) {
        this.id = DataUtils.getValue(object, "uuid", UUID.randomUUID().toString());
        this.baseType = RaidType.getType(DataUtils.getValue(object, "base_type", ""));

        this.startChunk = new ChunkPos(object.get("start_chunk").getAsInt(), object.get("start_chunk").getAsInt());
        this.endChunk = new ChunkPos(object.get("end_chunk").getAsInt(), object.get("end_chunk").getAsInt());

        this.playerCheckpointPosMap = new HashMap<>();
        Map<String, JsonElement> checkPoints = DataUtils.getValue(object, "check_points", new HashMap<>());
        checkPoints.forEach((uuid, infoElement) -> {
            JsonObject infoObject = infoElement.getAsJsonObject();

            this.playerCheckpointPosMap.put(uuid, new Vec3(
                    DataUtils.getValue(infoObject, "x", 0.0F),
                    DataUtils.getValue(infoObject, "y", 0.0F),
                    DataUtils.getValue(infoObject, "z", 0.0F)));
        });

        this.sharedTreasuresMap = new HashMap<>();
        List<JsonElement> sharedTreasures = DataUtils.getValue(object, "shared_treasures", new ArrayList<>());
        sharedTreasures.forEach(infoElement -> {
            JsonObject infoObject = infoElement.getAsJsonObject();
            JsonArray containerJson = DataUtils.getValue(infoObject, "container", new JsonArray());
            if (containerJson != null) {
                this.sharedTreasuresMap.put(
                        new BlockPos(
                                DataUtils.getValue(infoObject, "x", 0),
                                DataUtils.getValue(infoObject, "y", 0),
                                DataUtils.getValue(infoObject, "z", 0)),
                        DataUtils.toContainer(containerJson));
            }
        });

        this.playerTreasuresMap = new HashMap<>();
        Map<String, JsonElement> allPlayerTreasures = DataUtils.getValue(object, "player_treasures", new HashMap<>());
        allPlayerTreasures.forEach((uuid, playerTreasuresInfo) -> {
            JsonObject playerTreasuresInfoObject = playerTreasuresInfo.getAsJsonObject();
            Map<BlockPos, Container> playerTreasuresMap = this.playerTreasuresMap.computeIfAbsent(uuid, k -> new HashMap<>());
            List<JsonElement> playerTreasures = DataUtils.getValue(playerTreasuresInfoObject, "treasures", new ArrayList<>());
            playerTreasures.forEach(playerTreasureInfo -> {
                JsonObject playerTreasureInfoObject = playerTreasureInfo.getAsJsonObject();
                JsonArray containerJson = DataUtils.getValue(playerTreasureInfoObject, "container", new JsonArray());
                playerTreasuresMap.put(
                        new BlockPos(
                                DataUtils.getValue(playerTreasureInfoObject, "x", 0),
                                DataUtils.getValue(playerTreasureInfoObject, "y", 0),
                                DataUtils.getValue(playerTreasureInfoObject, "z", 0)),
                        DataUtils.toContainer(containerJson));
            });
        });

        BlockPos startBlock = new BlockPos(startChunk.getMinBlockX(), 0, startChunk.getMinBlockZ());
        BlockPos endBlock = new BlockPos(endChunk.getMaxBlockX(), FreeEpicGames.RAID_WORLD.getMaxBuildHeight(), endChunk.getMaxBlockZ());
        this.frame = new AABB(new Vec3(startBlock.getX(), startBlock.getY(), startBlock.getZ()), new Vec3(endBlock.getX(), endBlock.getY(), endBlock.getZ()));
        this.isActive = DataUtils.getValue(object, "is_active", false);
        this.blockPosOffset = new BlockPos(startChunk.getMinBlockX(), 0, startChunk.getMinBlockZ());
    }

    public Container getTreasureContainer(ServerPlayer player, BlockPos blockPos) {
        RaidTreasureType treasure = baseType.getTreasureType(blockPos.subtract(blockPosOffset));
        Map<BlockPos, Container> actualMap = treasure.isPlayerOwn() ?
                sharedTreasuresMap :
                playerTreasuresMap.computeIfAbsent(player.getStringUUID(), p -> new HashMap<>());

        return actualMap.computeIfAbsent(blockPos, bp -> {
            Container simpleContainer = new SimpleContainer(27);
            treasure.generator(simpleContainer);
            return simpleContainer;
        });
    }

    public boolean isInside(Vec3 pos) {
        return frame.contains(pos);
    }

    public RaidTreasureType getTreasureType(BlockPos blockPos) {
        return baseType.getTreasureType(blockPos.subtract(blockPosOffset));
    }

    public void respawn(ServerPlayer player) {
        Vec3 checkpointPos = new Vec3(0, 0, 0);

        if (player.isDeadOrDying()) {
            player.respawn();
        }

        player.teleportTo(FreeEpicGames.RAID_WORLD, checkpointPos.x, checkpointPos.y, checkpointPos.z, Collections.emptySet(), 0.0F, 0.0F);
    }

    public void joinPlayer(ServerPlayer player, Vec3 checkpointPos) {
        playerInstanceMappings.put(player.getStringUUID(), this);
        playerBackPosMappings.put(player.getStringUUID(),
                new Pair<>(player.level().dimension().registry().toString(), new Vec3(player.getX(), player.getY(), player.getZ())));
        playerCheckpointPosMap.put(player.getStringUUID(), checkpointPos);

        if (!FreeEpicGames.RAID_WORLD.players().contains(player)) {
            player.teleportTo(FreeEpicGames.RAID_WORLD, checkpointPos.x, checkpointPos.y, checkpointPos.z, Collections.emptySet(), 0.0F, 0.0F);
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (player.getRespawnPosition() != null) {
            removePlayer(player, player.getRespawnPosition());
        } else {
            removePlayer(player, FreeEpicGames.OVER_WORLD.getSharedSpawnPos());
        }
    }

    public void removePlayer(ServerPlayer player, BlockPos pos) {
        removePlayer(player, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
    }

    public void removePlayer(ServerPlayer player, Vec3 pos) {
        playerInstanceMappings.remove(player.getStringUUID());
        playerCheckpointPosMap.remove(player.getStringUUID());

        player.teleportTo(FreeEpicGames.OVER_WORLD, pos.x, pos.y, pos.z, Collections.emptySet(), player.getYRot(), player.getXRot());
    }

    @Nullable
    public static RaidInstanceView getRaidInstanceFromPlayer(ServerPlayer player) {
        return playerInstanceMappings.get(player.getStringUUID());
    }

    @Nullable
    public static RaidInstanceView getRaidInstanceFromChunk(ChunkPos chunkPos) {
        RaidDataAgent agent = RaidDataAgent.getInstance();
        AtomicReference<RaidInstanceView> ret = new AtomicReference<>(null);
        agent.getAllRaidInstance().forEach(view -> {
            if (view.isInside(new Vec3(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ()))) {
                ret.set(view);
            }
        });
        return ret.get();
    }

    public static Pair<String, Vec3> getBackPos(ServerPlayer player) {
        return playerBackPosMappings.get(player.getStringUUID());
    }

    public void build() {
        if (!isActive) {
            FreeEpicGames.LOGGER.info("Raid instance building: {}", id);

            int targetX = startChunk.x, targetZ = startChunk.z;
            while (targetX <= endChunk.x) {
                while (targetZ <= endChunk.z) {
                    int offsetX = targetX - startChunk.x, offsetZ = targetZ - startChunk.z;
                    CompoundTag tag = baseType.getChunkTemplates().get(offsetX + (offsetZ * baseType.getSizeX()));
                    FreeEpicGames.RAID_WORLD.getChunkSource().chunkMap.write(new ChunkPos(targetX, targetZ), tag);
                    targetZ++;
                }
                targetZ = startChunk.z;
                targetX++;
            }

            isActive = true;
        } else {
            FreeEpicGames.LOGGER.warn("Raid instance already active: {}", id);
        }
    }

    public void destroy() {
        if (isActive) {
            if (!MinecraftForge.EVENT_BUS.post(new RaidEvent.DestroyEvent(this))) {
                isActive = false;
                RaidDataAgent.getInstance().removeRaidInstance(this);

                playerBackPosMappings.forEach((u, v) -> {
                    Player player = FreeEpicGames.RAID_WORLD.getPlayerByUUID(UUID.fromString(u));
                    if (player instanceof ServerPlayer serverPlayer) {
                        if (frame.contains(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ())) {
                            removePlayer(serverPlayer);
                        }
                    }
                });
                playerInstanceMappings.forEach((u, v) -> {
                    if (v.equals(this)) {
                        playerInstanceMappings.remove(u);
                    }
                });

                // TODO : clear chunk (or not?)

                FreeEpicGames.LOGGER.info("Raid instance destroyed: {}", id);
            }
        } else {
            FreeEpicGames.LOGGER.warn("Raid instance already inactive: {}", id);
        }
    }

    @Override
    public JsonObject toCacheJson() {
        JsonObject cacheInfo = new JsonObject();

        cacheInfo.addProperty("uuid", id);
        cacheInfo.addProperty("base_type", baseType.getName());

        cacheInfo.addProperty("start_chunk", startChunk.toString());
        cacheInfo.addProperty("end_chunk", endChunk.toString());

        JsonObject checkpointsJson = new JsonObject();
        playerCheckpointPosMap.forEach((u, v) -> {
            JsonObject posObject = new JsonObject();
            posObject.addProperty("x", v.x);
            posObject.addProperty("y", v.y);
            posObject.addProperty("z", v.z);
            checkpointsJson.add(u, posObject);
        });
        cacheInfo.add("check_points", checkpointsJson);

        JsonArray sharedTreasure = new JsonArray();
        sharedTreasuresMap.forEach((bp, c) -> {
            JsonObject treasureObject = new JsonObject();
            treasureObject.addProperty("x", bp.getX());
            treasureObject.addProperty("y", bp.getY());
            treasureObject.addProperty("z", bp.getZ());
            treasureObject.add("container", DataUtils.fromContainer(c));
            sharedTreasure.add(treasureObject);
        });
        cacheInfo.add("shared_treasures", sharedTreasure);

        JsonObject allPlayerTreasures = new JsonObject();
        playerTreasuresMap.forEach((p, pc) -> {
            JsonArray playerTreasures = new JsonArray();
            pc.forEach((bp, c) -> {
                JsonObject treasureObject = new JsonObject();
                treasureObject.addProperty("x", bp.getX());
                treasureObject.addProperty("y", bp.getY());
                treasureObject.addProperty("z", bp.getZ());
                treasureObject.add("container", DataUtils.fromContainer(c));

                playerTreasures.add(treasureObject);
            });
            allPlayerTreasures.add(p, playerTreasures);
        });
        cacheInfo.add("player_treasures", allPlayerTreasures);

        return cacheInfo;
    }

}
