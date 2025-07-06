package org.thexeler.freeepicgames.database.type;

import lombok.Getter;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NpcEntity<T extends Entity> {

    public static final Map<UUID, NpcEntity<?>> entities = new HashMap<>();
    @Getter
    private final NpcType entityType;
    @Getter
    private final T originEntity;

    public NpcEntity(NpcType type, T origin) {
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

    public static @Nullable NpcEntity<?> getEntity(Entity entity) {
        return getEntity(entity.getUUID());
    }

    public static @Nullable NpcEntity<?> getEntity(UUID uuid) {
        return entities.get(uuid);
    }
}
