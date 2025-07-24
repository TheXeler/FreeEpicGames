package org.thexeler.freeepicgames.storage.view;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.storage.agent.NpcWorldDataAgent;
import org.thexeler.freeepicgames.storage.type.NpcType;
import org.thexeler.freeepicgames.storage.utils.DataUtils;
import org.thexeler.mind.MindMachine;

import java.util.HashMap;
import java.util.UUID;

public class NpcView implements AbstractView {
    private final NpcWorldDataAgent rootAgent;

    @Getter
    private final UUID id;
    @Getter
    private final NpcType npcType;
    @Getter
    private final Entity originEntity;
    @Getter
    private final HashMap<String, String> npcData;
    @Getter
    private final MindMachine mind;
    // TODO : saved

    public NpcView(@NotNull Entity originEntity, NpcType type, NpcWorldDataAgent agent) {
        this.rootAgent = agent;
        this.originEntity = originEntity;
        this.id = originEntity.getUUID();
        this.npcType = type;

        this. rootAgent.getAllNpc();

        this.npcData = new HashMap<>();
        this.mind = new MindMachine(originEntity);
    }

    public NpcView(JsonObject object, NpcWorldDataAgent agent) {
        this.rootAgent = agent;

        this.id = UUID.fromString(DataUtils.getValue(object, "id", ""));
        this.npcType = NpcType.getType(DataUtils.getValue(object, "type", ""));
        this.originEntity = agent.getWorld().getEntity(id);

        this.npcData = new HashMap<>();
        this.mind = new MindMachine(this.originEntity);
    }

    public String getName() {
        return originEntity.getName().getString();
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
        return NpcWorldDataAgent.getInstance(level).getNpcView(id);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id.toString());
        jsonObject.addProperty("type", npcType.getName());

        JsonObject npcDataJson = new JsonObject();
        npcData.forEach(npcDataJson::addProperty);
        jsonObject.add("npc_data", npcDataJson);

        return jsonObject;
    }
}
