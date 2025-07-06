package org.thexeler.freeepicgames.database.type;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.database.agent.WorldNpcDataAgent;
import org.thexeler.freeepicgames.database.untils.DataPacket;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.view.NpcView;

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

    private NpcType(String name, EntityType<?> type, boolean isInvulnerable, boolean isInvisible, boolean isNoGravity, boolean isNoAI) {
        this.name = name;
        this.entityType = type;
        this.isInvulnerable = isInvulnerable;
        this.isInvisible = isInvisible;
        this.isNoGravity = isNoGravity;
        this.isNoAI = isNoAI;
    }

    public NpcView create(ServerLevel level) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(level);
        return agent.createNPC(this, null);
    }

    private JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("base_id", entityType.toString());

        jsonObject.addProperty("is_invulnerable", isInvulnerable);
        jsonObject.addProperty("is_invisible", isInvisible);
        jsonObject.addProperty("is_no_gravity", isNoGravity);
        jsonObject.addProperty("is_no_ai", isNoAI);

        return jsonObject;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            String typeURI = object.get("base_id").getAsString();
            Optional<EntityType<?>> typeOptional = EntityType.byString(typeURI);
            if (typeOptional.isPresent()) {
                types.put(name, new NpcType(name, typeOptional.get(),
                        DataUtils.getValue(object, "is_invulnerable", true),
                        DataUtils.getValue(object, "is_invisible", true),
                        DataUtils.getValue(object, "is_no_gravity", true),
                        DataUtils.getValue(object, "is_no_ai", true)));
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
        FreeEpicGames.LOGGER.info("Loading NpcView types...");
        DataUtils.getPackAllData(DataPacket.NPC_TYPE).forEach(NpcType::register);
        FreeEpicGames.LOGGER.info("Loaded {} NpcView types", types.size());
    }

    public static void expire() {
        FreeEpicGames.LOGGER.info("Saving NpcView types...");
        Map<String, JsonObject> jsonMap = new HashMap<>();
        types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
        DataUtils.savePacketAllData(DataPacket.NPC_TYPE, jsonMap);
        FreeEpicGames.LOGGER.info("Expiring NpcView types...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired NpcView types");
    }
}
