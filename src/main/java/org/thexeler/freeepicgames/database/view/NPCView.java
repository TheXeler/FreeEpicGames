package org.thexeler.freeepicgames.database.view;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.database.agent.WorldNPCDataAgent;
import org.thexeler.freeepicgames.database.type.NPCType;
import org.thexeler.freeepicgames.database.untils.DataUtils;

import java.util.HashMap;
import java.util.UUID;

public class NPCView implements AbstractView {
    private final WorldNPCDataAgent rootAgent;

    @Getter
    private final String id;
    @Getter
    private final NPCType entityType;
    @Getter
    private final Entity originEntity;
    @Getter
    private final HashMap<String, String> npcData;

    public NPCView(@NotNull Entity origin, NPCType type, WorldNPCDataAgent agent) {
        this.rootAgent = agent;
        this.originEntity = origin;
        this.id = origin.getStringUUID();
        this.entityType = type;

        this.npcData = new HashMap<>();
    }

    public NPCView(JsonObject object, WorldNPCDataAgent agent) {
        this.rootAgent = agent;

        this.id = DataUtils.getValue(object, "id", "");
        this.entityType = NPCType.getType(DataUtils.getValue(object, "type", ""));
        this.originEntity = agent.getWorld().getEntity(UUID.fromString(id));

        this.npcData = new HashMap<>();
    }

    public void discard() {
        this.discardAdditional();
        if (originEntity != null) {
            originEntity.discard();
        }
    }

    public void discardAdditional() {
        rootAgent.deleteNPC(id);
    }

    public static NPCView getEntity(@NotNull Entity entity) {
        if (entity.level() instanceof ServerLevel level) {
            return getEntity(level, entity.getStringUUID());
        }
        return null;
    }

    public static NPCView getEntity(ServerLevel level, UUID uuid) {
        return getEntity(level, uuid.toString());
    }

    public static NPCView getEntity(ServerLevel level, String id) {
        return WorldNPCDataAgent.getInstance(level).getNPCView(id);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        jsonObject.addProperty("id", id);
        jsonObject.addProperty("type", entityType.getName());

        JsonObject npcDataJson = new JsonObject();
        npcData.forEach(npcDataJson::addProperty);
        jsonObject.add("npc_data", npcDataJson);

        return jsonObject;
    }
}
