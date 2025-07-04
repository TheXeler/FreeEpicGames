package org.thexeler.freeepicgames.events;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.thexeler.freeepicgames.database.view.NPCView;

public class NPCEvent extends EntityEvent {
    @Getter
    private final NPCView NPCView;

    public NPCEvent(NPCView entity) {
        super(entity.getOriginEntity());
        NPCView = entity;
    }

    public static class NPCCreateEvent extends NPCEvent {
        public NPCCreateEvent(NPCView entity) {
            super(entity);
        }
    }

    public static class NPCJoinEvent extends NPCEvent {
        @Getter
        private final ServerLevel level;

        public NPCJoinEvent(NPCView entity, ServerLevel level) {
            super(entity);
            this.level = level;
        }
    }

    @Cancelable
    public static class NPCDeathEvent extends NPCEvent {
        @Getter
        private final DamageSource source;

        public NPCDeathEvent(NPCView entity, DamageSource source) {
            super(entity);
            this.source = source;
        }
    }

    public static class NPCInteractEvent extends NPCEvent {
        @Getter
        private final Player player;

        public NPCInteractEvent(NPCView entity, Player player) {
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

        public NPCDamageEvent(NPCView entity, DamageSource source, float amount) {
            super(entity);
            this.source = source;
            this.amount = amount;
        }
    }

    public static class NPCTickEvent extends NPCEvent {
        public NPCTickEvent(NPCView entity) {
            super(entity);
        }
    }
}
