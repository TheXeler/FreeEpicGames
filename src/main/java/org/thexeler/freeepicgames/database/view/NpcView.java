package org.thexeler.freeepicgames.database.view;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.database.agent.WorldNpcDataAgent;
import org.thexeler.freeepicgames.database.type.NpcType;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.mind.MindMachine;

import java.util.HashMap;
import java.util.UUID;

public class NpcView implements AbstractView {
    private final WorldNpcDataAgent rootAgent;

    @Getter
    private final String id;
    @Getter
    private final NpcType npcType;
    @Getter
    private final Entity originEntity;
    @Getter
    private final HashMap<String, String> npcData;
    @Getter
    private final MindMachine mind;
    // TODO : saved

    public NpcView(@NotNull Entity origin, NpcType type, WorldNpcDataAgent agent) {
        this.rootAgent = agent;
        this.originEntity = origin;
        this.id = origin.getStringUUID();
        this.npcType = type;

        this.npcData = new HashMap<>();
        this.mind = new MindMachine(origin);
    }

    public NpcView(JsonObject object, WorldNpcDataAgent agent) {
        this.rootAgent = agent;

        this.id = DataUtils.getValue(object, "id", "");
        this.npcType = NpcType.getType(DataUtils.getValue(object, "type", ""));
        this.originEntity = agent.getWorld().getEntity(UUID.fromString(id));

        this.npcData = new HashMap<>();
        this.mind = new MindMachine(this.originEntity);
    }

    public void discard() {
        this.discardAdditional();
        if (originEntity != null) {
            originEntity.discard();
        }
    }

    public void discardAdditional() {
        rootAgent.deleteNpc(id);
    }

    public static NpcView getEntity(Entity entity) {
        if (entity != null) {
            if (entity.level() instanceof ServerLevel level) {
                return getEntity(level, entity.getStringUUID());
            }
        }
        return null;
    }

    public static NpcView getEntity(ServerLevel level, UUID uuid) {
        return getEntity(level, uuid.toString());
    }

    public static NpcView getEntity(ServerLevel level, String id) {
        return WorldNpcDataAgent.getInstance(level).getNpcView(id);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id);
        jsonObject.addProperty("type", npcType.getName());

        JsonObject npcDataJson = new JsonObject();
        npcData.forEach(npcDataJson::addProperty);
        jsonObject.add("npc_data", npcDataJson);

        return jsonObject;
    }
}
