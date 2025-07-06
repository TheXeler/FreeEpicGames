package org.thexeler.freeepicgames.database.agent;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.database.type.NPCType;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.untils.ModSavedData;
import org.thexeler.freeepicgames.database.view.NPCView;
import org.thexeler.freeepicgames.events.NpcEvent;

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
                if (type.isNoAI()) {
                    if(origin instanceof Mob mob){
                        mob.setNoAi(true);
                    }
                    origin.getPersistentData().putBoolean("NoAI", true);
                }

                NeoForge.EVENT_BUS.post(new NpcEvent.CreateEvent(view));
                world.addFreshEntity(origin);
                NeoForge.EVENT_BUS.post(new NpcEvent.JoinEvent(view, world));
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
