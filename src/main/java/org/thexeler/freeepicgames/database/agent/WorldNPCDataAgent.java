package org.thexeler.freeepicgames.database.agent;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.database.type.NPCType;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.untils.ModSavedData;
import org.thexeler.freeepicgames.database.view.NPCView;
import org.thexeler.freeepicgames.events.NPCEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldNPCDataAgent implements AbstractDataAgent {
    private static final Map<ServerLevel, WorldNPCDataAgent> instances = new HashMap<>();

    @Getter
    private final ServerLevel world;
    private final JsonObject optionData;
    private final JsonObject npcData;

    private final Map<String, NPCView> npcViewMap = Collections.synchronizedMap(new HashMap<>());

    private WorldNPCDataAgent(ServerLevel world) {
        this.world = world;

        optionData = ModSavedData.getWorldData(world, "NPCSettings");
        npcData = ModSavedData.getWorldData(world, "NPCData");

        load();
    }

    public static WorldNPCDataAgent getInstance(ServerLevel world) {
        return instances.computeIfAbsent(world, WorldNPCDataAgent::new);
    }

    public Collection<NPCView> getAllNPC() {
        return npcViewMap.values();
    }

    public NPCView createNPC(NPCType type) {
        return createNPC(type, null);
    }

    public NPCView createNPC(NPCType type, Entity origin) {
        NPCView view = null;
        if (origin == null) {
            origin = type.getEntityType().create(world);
        }

        if (origin != null) {
            if (npcData.get(origin.getStringUUID()) == null) {
                // JsonObject areaCacheInfo = new JsonObject();
                view = new NPCView(origin, type, this);
                origin.setInvulnerable(type.isInvulnerable());
                origin.setInvisible(type.isInvisible());
                origin.setNoGravity(type.isNoGravity());
                CompoundTag tags = origin.serializeNBT();
                if (type.isNoAI()) {
                    tags.putInt("NoAI", 1);
                }
                origin.deserializeNBT(tags);

                MinecraftForge.EVENT_BUS.post(new NPCEvent.NPCCreateEvent(view));
                world.addFreshEntity(origin);
                MinecraftForge.EVENT_BUS.post(new NPCEvent.NPCJoinEvent(view, world));
                npcViewMap.put(view.getId(), view);
            } else {
                FreeEpicGames.LOGGER.error("NPC {} already exists", origin.getStringUUID());
            }
        } else {
            FreeEpicGames.LOGGER.error("Entity type {} not found", type.getEntityType());
        }

        return view;
    }

    public boolean deleteNPC(String id) {
        if (npcViewMap.get(id) != null) {
            npcViewMap.get(id).discard();
            npcViewMap.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public NPCView getNPCView(String id) {
        return npcViewMap.get(id);
    }

    @Override
    public void load() {
        // attacker = DataUtils.getValue(optionData, "attacker", "");

        npcData.keySet().forEach(id -> {
            NPCView view = new NPCView(npcData.getAsJsonObject(id), this);
            if (view.getOriginEntity() == null) {
                npcViewMap.put(id, view);
            }
        });
    }

    @Override
    public void save() {
        // optionData.addProperty("Attacker", attacker);

        DataUtils.computeViewMap(npcViewMap, npcData);
    }
}
