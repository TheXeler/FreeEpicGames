package org.thexeler.freeepicgames.database.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.freeepicgames.database.untils.DataPacket;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidType {
    private static final Map<String, RaidType> types = new HashMap<>();

    @Getter
    private final String name;
    @Getter
    private final Vec3i size;
    @Getter
    private final AABB respawnArea;
    private final Map<BlockPos, RaidTreasureType> treasuresMap;

    private RaidType(String name, AABB respawnArea, Vec3i size, List<JsonElement> lootTables) {
        this.size = size;
        this.name = name;

        this.respawnArea = respawnArea;
        this.treasuresMap = new HashMap<>();

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
        GlobalRaidDataAgent agent = GlobalRaidDataAgent.getInstance();
        return agent.createRaidInstance(this);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("chunk_size_x", size.getX());
        jsonObject.addProperty("chunk_size_y", size.getZ());

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

        return jsonObject;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            RaidType.types.put(name, new RaidType(name,
                    new AABB(
                            new BlockPos(
                                    DataUtils.getValue(object, "respawn_area_start_x", 0),
                                    DataUtils.getValue(object, "respawn_area_start_y", 0),
                                    DataUtils.getValue(object, "respawn_area_start_z", 0)),
                            new BlockPos(
                                    DataUtils.getValue(object, "respawn_area_end_x", 0),
                                    DataUtils.getValue(object, "respawn_area_end_y", 0),
                                    DataUtils.getValue(object, "respawn_area_end_z", 0))),
                    new Vec3i(
                            DataUtils.getValue(object, "chunk_size_x", 0),
                            0,
                            DataUtils.getValue(object, "chunk_size_y", 0)),
                    DataUtils.getValue(object, "treasure_info", new ArrayList<>())));
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
        FreeEpicGames.LOGGER.info("Saving RaidTypes...");
        Map<String, JsonObject> jsonMap = new HashMap<>();
        types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
        DataUtils.savePacketAllData(DataPacket.RAID_TEMPLATE, jsonMap);
        FreeEpicGames.LOGGER.info("Expiring RaidTypes...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired RaidTypes.");
    }
}
