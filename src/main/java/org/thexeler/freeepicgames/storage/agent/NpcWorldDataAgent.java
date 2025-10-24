package org.thexeler.freeepicgames.storage.agent;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.event.NpcEvent;
import org.thexeler.freeepicgames.storage.type.NpcType;
import org.thexeler.freeepicgames.storage.utils.DataUtils;
import org.thexeler.freeepicgames.storage.utils.ModSavedData;
import org.thexeler.freeepicgames.storage.view.NpcView;

import java.util.*;

public class NpcWorldDataAgent extends AbstractWorldDataAgent {
    private static final Map<ServerLevel, NpcWorldDataAgent> instances = new HashMap<>();

    private final JsonObject optionData;
    private final JsonObject npcData;

    @Getter
    @Setter
    private boolean isReinforceMode;

    private final Map<String, NpcView> npcViewMap = Collections.synchronizedMap(new HashMap<>());

    private NpcWorldDataAgent(ServerLevel world) {
        super(world);

        optionData = ModSavedData.getWorldData(world, "NPCSettings");
        npcData = ModSavedData.getWorldData(world, "NPCData");

        load();
    }

    public static NpcWorldDataAgent getInstance(ServerLevel world) {
        return instances.computeIfAbsent(world, NpcWorldDataAgent::new);
    }

    public Collection<NpcView> getAllNpc() {
        return npcViewMap.values();
    }

    public NpcView createNpc(NpcType type, double x, double y, double z) {
        return createNpc(type, x, y, z, null);
    }

    public NpcView createNpc(NpcType type, @NotNull Entity origin) {
        return createNpc(type, origin.getX(), origin.getY(), origin.getZ(), origin);
    }

    public NpcView createNpc(NpcType type, double x, double y, double z, Entity origin) {
        boolean freshEntity = false;
        NpcView view = null;
        if (origin == null) {
            if (type.getEntityType() != EntityType.PLAYER) {
                origin = type.getEntityType().create(world);
                freshEntity = true;
            } else {
                UUID uuid = UUID.randomUUID();
                GameProfile profile = new GameProfile(uuid, uuid.toString());
                origin = FakePlayerFactory.get(world, profile);
            }
        }

        if (origin != null) {
            if (npcData.get(origin.getStringUUID()) == null) {
                view = new NpcView(origin, type, this);
                origin.setInvulnerable(type.isInvulnerable());
                origin.setInvisible(type.isInvisible());
                origin.setNoGravity(type.isNoGravity());

                // TODO
                if (origin instanceof Mob mob) {
                    if (type.isNoAI()) {
                        mob.setNoAi(true);
                    }
                    if (type.getLootTable() != null) {
                        //mob.setData(Attachment.);//(BuiltInLootTables.valueOf(type.getLootTable()));
                    }
                } else {
                    origin.getPersistentData().putBoolean("NoAI", true);
                    //origin.getPersistentData().putString("DeathLootTable", type.getLootTable());
                }

                MinecraftForge.EVENT_BUS.post(new NpcEvent.Create(view));
                if (freshEntity) {
                    origin.teleportTo(x, y, z);
                    world.addFreshEntity(origin);
                    MinecraftForge.EVENT_BUS.post(new NpcEvent.Join(view, world));
                }
                npcViewMap.put(origin.getUUID().toString(), view);
            } else {
                FreeEpicGames.LOGGER.error("NpcEntity {} already exists", origin.getStringUUID());
            }
        } else {
            FreeEpicGames.LOGGER.error("Entity type {} not found", type.getEntityType());
        }

        return view;
    }

    public boolean deleteNpc(UUID id) {
        if (npcViewMap.get(id.toString()) != null) {
            npcViewMap.remove(id.toString());
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public NpcView getNpcView(String id) {
        return npcViewMap.get(id);
    }

    @Override
    public void load() {
        isReinforceMode = DataUtils.getValue(optionData, "is_reinforce_mode", false);

        npcData.keySet().forEach(id -> {
            NpcView view = new NpcView(npcData.getAsJsonObject(id), this);
            if (view.getOriginEntity() == null) {
                npcViewMap.put(id, view);
            }
        });
    }

    @Override
    public void save() {
        optionData.addProperty("is_reinforce_mode", isReinforceMode);

        DataUtils.computeViewMap(npcViewMap, npcData);
    }

    public static void expire() {
        instances.clear();
    }
}
