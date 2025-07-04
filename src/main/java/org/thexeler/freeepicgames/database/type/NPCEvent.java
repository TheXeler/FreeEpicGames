package org.thexeler.freeepicgames.database.type;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class NPCEvent extends EntityEvent {
    @Getter
    private final NPC<?> NPC;

    public NPCEvent(NPC<?> entity) {
        super(entity.getOriginEntity());
        NPC = entity;
    }

    public static class NPCCreateEvent extends NPCEvent {
        public NPCCreateEvent(NPC<?> entity) {
            super(entity);
        }
    }

    public static class NPCJoinEvent extends NPCEvent {
        @Getter
        private final ServerLevel level;

        public NPCJoinEvent(NPC<?> entity, ServerLevel level) {
            super(entity);
            this.level = level;
        }
    }

    @Cancelable
    public static class NPCDeathEvent extends NPCEvent {
        @Getter
        private final DamageSource source;

        public NPCDeathEvent(NPC<?> entity, DamageSource source) {
            super(entity);
            this.source = source;
        }
    }

    public static class NPCInteractEvent extends NPCEvent {
        @Getter
        private final Player player;

        public NPCInteractEvent(NPC<?> entity, Player player) {
            super(entity);
            this.player = player;
        }
    }

    @Cancelable
    public static class NPCDamageEvent extends NPCEvent {
        @Getter
        private final DamageSource source;
        @Getter
        private final float amount;

        public NPCDamageEvent(NPC<?> entity, DamageSource source, float amount) {
            super(entity);
            this.source = source;
            this.amount = amount;
        }
    }

    public static class NPCTickEvent extends NPCEvent {
        public NPCTickEvent(NPC<?> entity) {
            super(entity);
        }
    }
}
