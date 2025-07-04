package org.thexeler.freeepicgames.database.type;

import lombok.Getter;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPC<T extends Entity> {

    public static final Map<UUID, NPC<?>> entities = new HashMap<>();
    @Getter
    private final NPCType entityType;
    @Getter
    private final T originEntity;

    public NPC(NPCType type, T origin) {
        entities.put(origin.getUUID(), this);
        entityType = type;
        originEntity = origin;
    }

    public void discard() {
        entities.remove(originEntity.getUUID());
        originEntity.discard();
    }

    public static void discardAdditional(Entity entity) {
        discardAdditional(entity.getUUID());
    }

    public static void discardAdditional(UUID uuid) {
        entities.remove(uuid);
    }

    public static @Nullable NPC<?> getEntity(Entity entity) {
        return getEntity(entity.getUUID());
    }

    public static @Nullable NPC<?> getEntity(UUID uuid) {
        return entities.get(uuid);
    }
}
