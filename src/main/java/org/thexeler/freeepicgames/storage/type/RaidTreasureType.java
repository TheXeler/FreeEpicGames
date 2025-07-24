package org.thexeler.freeepicgames.storage.type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.storage.utils.DataPacket;
import org.thexeler.freeepicgames.storage.utils.DataUtils;

import java.util.*;

public class RaidTreasureType {
    private static final Map<String, RaidTreasureType> types = Collections.synchronizedMap(new HashMap<>());

    @Getter
    private final String name;
    @Getter
    private final String title;
    @Getter
    private final boolean isPlayerOwn;
    private final LootTable lootTable;

    private RaidTreasureType(String name, String title, boolean isPlayerOwn, LootTable lootTable) {
        this.name = name;

        this.title = title;
        this.isPlayerOwn = isPlayerOwn;
        this.lootTable = lootTable;
    }

    public void generator(Container container) {
        if (lootTable != null) {
            lootTable.fill(container, new LootParams.Builder(FreeEpicGames.RAID_WORLD).create(LootContextParamSets.CHEST), 0);
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        jsonObject.addProperty("title", title);
        jsonObject.addProperty("is_player_own", isPlayerOwn);
        jsonObject.addProperty("loot_table", gson.toJson(lootTable));

        return jsonObject;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            Gson gson = new Gson();

            types.put(name, new RaidTreasureType(name,
                    DataUtils.getValue(object, "title", ""),
                    DataUtils.getValue(object, "is_player_own", false),
                    gson.fromJson(object.getAsJsonObject("loot_table"), LootTable.class)));

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

    public static List<RaidTreasureType> getAllType() {
        return new ArrayList<>(types.values());
    }

    public static List<String> getAllTypeName() {
        return new ArrayList<>(types.keySet());
    }

    @Nullable
    public static RaidTreasureType getType(String name) {
        return types.get(name);
    }

    public static void init() {
        FreeEpicGames.LOGGER.info("Initializing RaidTreasureTypes...");
        DataUtils.getPackAllData(DataPacket.RAID_TREASURE_TYPE).forEach(RaidTreasureType::register);
    }

    public static void expire() {
        expire(false);
    }

    public static void expire(boolean forced) {
        if (!forced) {
            FreeEpicGames.LOGGER.info("Saving RaidTreasureTypes...");
            Map<String, JsonObject> jsonMap = new HashMap<>();
            types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
            DataUtils.savePacketAllData(DataPacket.RAID_TREASURE_TYPE, jsonMap);
        }
        FreeEpicGames.LOGGER.info("Expiring RaidTreasureTypes...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired RaidTreasureTypes.");
    }
}
