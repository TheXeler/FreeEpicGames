package org.thexeler.freeepicgames.storage.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesPaths;
import org.thexeler.freeepicgames.storage.agent.RaidDataAgent;
import org.thexeler.freeepicgames.storage.utils.DataPacket;
import org.thexeler.freeepicgames.storage.utils.DataUtils;
import org.thexeler.freeepicgames.storage.view.RaidInstanceView;
import org.thexeler.slacker.utils.IOUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidType {
    private static final Map<String, RaidType> types = new HashMap<>();

    @Getter
    private final String name;
    @Getter
    private final int sizeX, sizeZ;
    @Getter
    private final BlockPos spawnAreaStart, spawnAreaEnd;
    @Getter
    private final List<CompoundTag> chunkTemplates;
    private final Map<BlockPos, RaidTreasureType> treasuresMap;

    private RaidType(String name, BlockPos spawnAreaStart, BlockPos spawnAreaEnd, int sizeX, int sizeZ, List<JsonElement> lootTables, CompoundTag chunkTemplates) {
        this.name = name;
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;

        this.spawnAreaStart = spawnAreaStart;
        this.spawnAreaEnd = spawnAreaEnd;
        this.treasuresMap = new HashMap<>();

        this.chunkTemplates = new ArrayList<>();

        lootTables.forEach(lootTableInfo -> {
            JsonObject object = lootTableInfo.getAsJsonObject();
            treasuresMap.put(new BlockPos(
                            DataUtils.getValue(object, "x", 0),
                            DataUtils.getValue(object, "y", 0),
                            DataUtils.getValue(object, "z", 0)),
                    RaidTreasureType.getType(DataUtils.getValue(object, "loot_table", "")));
        });
    }

    public RaidTreasureType getTreasureType(BlockPos pos) {
        return treasuresMap.get(pos);
    }

    public void setTreasureType(BlockPos pos, String type) {
        if (RaidTreasureType.getType(type) != null) {
            treasuresMap.put(pos, RaidTreasureType.getType(type));
        } else {
            FreeEpicGames.LOGGER.error("Invalid treasure loot table : {}", type);
        }
    }

    public RaidInstanceView create() {
        RaidDataAgent agent = RaidDataAgent.getInstance();
        return agent.createRaidInstance(this);
    }

    public void updateConstruct(ChunkPos startChunk, ChunkPos endChunk) {
        chunkTemplates.clear();
        int targetX = startChunk.x, targetZ = endChunk.z;

        while (targetX <= endChunk.x) {
            while (targetZ <= endChunk.z) {
                int offsetX = targetX - startChunk.x, offsetZ = targetZ - startChunk.z;
                chunkTemplates.add(ChunkSerializer.write(FreeEpicGames.RAID_WORLD, FreeEpicGames.RAID_WORLD.getChunk(offsetX, offsetZ)));
                targetZ++;
            }
            targetZ = startChunk.z;
            targetX++;
        }
    }

    public void updateConstruct(RaidInstanceView view) {
        chunkTemplates.clear();
        int targetX = view.getStartChunk().x, targetZ = view.getStartChunk().z;

        while (targetX <= view.getEndChunk().x) {
            while (targetZ <= view.getEndChunk().z) {
                int offsetX = targetX - view.getStartChunk().x, offsetZ = targetZ - view.getStartChunk().z;
                chunkTemplates.add(ChunkSerializer.write(FreeEpicGames.RAID_WORLD, FreeEpicGames.RAID_WORLD.getChunk(offsetX, offsetZ)));
                targetZ++;
            }
            targetZ = view.getStartChunk().z;
            targetX++;
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("respawn_area_start_x", spawnAreaStart.getX());
        jsonObject.addProperty("respawn_area_start_y", spawnAreaStart.getY());
        jsonObject.addProperty("respawn_area_start_z", spawnAreaStart.getZ());
        jsonObject.addProperty("respawn_area_end_x", spawnAreaEnd.getX());
        jsonObject.addProperty("respawn_area_end_y", spawnAreaEnd.getY());
        jsonObject.addProperty("respawn_area_end_z", spawnAreaEnd.getZ());

        jsonObject.addProperty("chunk_size_x", sizeX);
        jsonObject.addProperty("chunk_size_y", sizeZ);

        JsonArray array = new JsonArray();
        treasuresMap.forEach((pos, type) -> {
            JsonObject object = new JsonObject();

            object.addProperty("x", pos.getX());
            object.addProperty("y", pos.getY());
            object.addProperty("z", pos.getZ());

            object.addProperty("type", type.getName());

            array.add(object);
        });
        jsonObject.add("treasure_info", new JsonArray());

        try {
            CompoundTag tag = new CompoundTag();
            ListTag chunks = new ListTag();
            chunks.addAll(chunkTemplates);
            tag.put("chunks", chunks);
            IOUtilities.writeNbtCompressed(tag, FreeEpicGamesPaths.RAID_TEMPLATE_DIR.resolve(name + ".nbt"));
        } catch (IOException e) {
            FreeEpicGames.LOGGER.error("Failed to write raid template: {}", name + ".nbt");
        }

        return jsonObject;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            try {
                RaidType.types.put(name, new RaidType(name,
                        new BlockPos(
                                DataUtils.getValue(object, "respawn_area_start_x", 0),
                                DataUtils.getValue(object, "respawn_area_start_y", 0),
                                DataUtils.getValue(object, "respawn_area_start_z", 0)),
                        new BlockPos(
                                DataUtils.getValue(object, "respawn_area_end_x", 0),
                                DataUtils.getValue(object, "respawn_area_end_y", 0),
                                DataUtils.getValue(object, "respawn_area_end_z", 0)),
                        DataUtils.getValue(object, "chunk_size_x", 0),
                        DataUtils.getValue(object, "chunk_size_z", 0),
                        DataUtils.getValue(object, "treasure_info", new ArrayList<>()),
                        NbtIo.read(FreeEpicGamesPaths.RAID_TEMPLATE_DIR.resolve(name + ".nbt").toFile())));
            } catch (IOException e) {
                FreeEpicGames.LOGGER.error("Failed to read raid template: {}", name + ".nbt");
            }
            return true;
        } else {
            FreeEpicGames.LOGGER.error("Repeated registration key : {}", name);
        }
        return false;
    }

    public static boolean unregister(String name) {
        if (types.containsKey(name)) {
            types.remove(name);
            return true;
        }
        return false;
    }

    public static List<RaidType> getAllType() {
        return new ArrayList<>(types.values());
    }

    public static List<String> getAllTypeName() {
        return new ArrayList<>(types.keySet());
    }

    @Nullable
    public static RaidType getType(String name) {
        return types.get(name);
    }

    public static void init() {
        FreeEpicGames.LOGGER.info("Registering RaidTypes...");
        DataUtils.getPackAllData(DataPacket.RAID_TEMPLATE).forEach(RaidType::register);
        FreeEpicGames.LOGGER.info("Registered {} RaidTypes.", types.size());
    }

    public static void expire() {
        expire(false);
    }

    public static void expire(boolean forced) {
        if (!forced) {
            FreeEpicGames.LOGGER.info("Saving RaidTypes...");
            Map<String, JsonObject> jsonMap = new HashMap<>();
            types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
            DataUtils.savePacketAllData(DataPacket.RAID_TEMPLATE, jsonMap);
        }
        FreeEpicGames.LOGGER.info("Expiring RaidTypes...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired RaidTypes.");
    }
}
