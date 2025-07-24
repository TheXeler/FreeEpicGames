package org.thexeler.freeepicgames.storage.type;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.storage.utils.DataPacket;
import org.thexeler.freeepicgames.storage.utils.DataUtils;

import java.util.*;

public class NpcType {
    private static final Map<String, NpcType> types = new HashMap<>();

    @Getter
    private final String name;
    @Getter
    private final EntityType<?> entityType;
    @Getter
    private final boolean isInvulnerable;
    @Getter
    private final boolean isInvisible;
    @Getter
    private final boolean isNoGravity;
    @Getter
    private final boolean isNoAI;
    @Getter
    @Setter
    private boolean isWeakAI;
    @Nullable
    @Getter
    private final ResourceKey<LootTable> lootTable;

    private NpcType(String name, EntityType<?> type, boolean isInvulnerable, boolean isInvisible, boolean isNoGravity, boolean isNoAI, boolean isWeakAI, @Nullable ResourceKey<LootTable> lootTable) {
        this.name = name;

        this.entityType = type;
        this.isInvulnerable = isInvulnerable;
        this.isInvisible = isInvisible;
        this.isNoGravity = isNoGravity;
        this.isNoAI = isNoAI;
        this.isWeakAI = isWeakAI;

        this.lootTable = lootTable;
    }

    private JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("base_id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());

        jsonObject.addProperty("is_invulnerable", isInvulnerable);
        jsonObject.addProperty("is_invisible", isInvisible);
        jsonObject.addProperty("is_no_gravity", isNoGravity);
        jsonObject.addProperty("is_no_ai", isNoAI);
        jsonObject.addProperty("is_weak_ai", isWeakAI);

        if (lootTable != null) {
            jsonObject.addProperty("loot_table", lootTable.toString());
        }

        return jsonObject;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            String typeURI = object.get("base_id").getAsString();
            Optional<EntityType<?>> typeOptional = EntityType.byString(typeURI);
            if (typeOptional.isPresent()) {
                String lootTableKey = DataUtils.getValue(object, "loot_table", "");
                types.put(name, new NpcType(name, typeOptional.get(),
                        DataUtils.getValue(object, "is_invulnerable", true),
                        DataUtils.getValue(object, "is_invisible", true),
                        DataUtils.getValue(object, "is_no_gravity", true),
                        DataUtils.getValue(object, "is_no_ai", true),
                        DataUtils.getValue(object, "is_weak_ai", false),
                        null));
                // TODO: loot table(and death event loot)
                return true;
            } else {
                FreeEpicGames.LOGGER.error("Unknow entity type from URI : {}", typeURI);
            }
        } else {
            FreeEpicGames.LOGGER.error("Repeated registration key : {}", name);
        }
        return false;
    }

    public static boolean unregister(String name) {
        if (types.containsKey(name)) {
            types.remove(name);
            FreeEpicGames.LOGGER.info("Unregistered npc type : {}", name);
            return true;
        }
        FreeEpicGames.LOGGER.error("Unregister failed : {}", name);
        return false;
    }

    public static List<NpcType> getAllType() {
        return new ArrayList<>(types.values());
    }

    public static List<String> getAllTypeName() {
        return new ArrayList<>(types.keySet());
    }

    @Nullable
    public static NpcType getType(String name) {
        return types.get(name);
    }

    public static void init() {
        FreeEpicGames.LOGGER.info("Loading Npc types...");
        DataUtils.getPackAllData(DataPacket.NPC_TYPE).forEach(NpcType::register);
        FreeEpicGames.LOGGER.info("Loaded {} Npc types", types.size());
    }

    public static void expire() {
        expire(false);
    }

    public static void expire(boolean forced) {
        if (!forced) {
            FreeEpicGames.LOGGER.info("Saving Npc types...");
            Map<String, JsonObject> jsonMap = new HashMap<>();
            types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
            DataUtils.savePacketAllData(DataPacket.NPC_TYPE, jsonMap);
        }
        FreeEpicGames.LOGGER.info("Expiring Npc types...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired Npc types");
    }
}
