package org.thexeler.freeepicgames.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.thexeler.freeepicgames.database.view.NpcView;

public abstract class NpcEvent extends Event {
    @Getter
    private final Entity entity;
    @Getter
    private final NpcView view;

    public NpcEvent(NpcView npc) {
        this.entity = npc.getOriginEntity();
        this.view = npc;
    }

    public static class CreateEvent extends NpcEvent {
        public CreateEvent(NpcView npc) {
            super(npc);
        }
    }

    public static class JoinEvent extends NpcEvent {
        @Getter
        private final ServerLevel level;

        public JoinEvent(NpcView npc, ServerLevel level) {
            super(npc);
            this.level = level;
        }
    }

    public static class DeathEvent extends NpcEvent implements ICancellableEvent {
        @Getter
        private final DamageSource source;

        public DeathEvent(NpcView npc, DamageSource source) {
            super(npc);
            this.source = source;
        }
    }

    public static class InteractEvent extends NpcEvent {
        @Getter
        private final Player player;

        public InteractEvent(NpcView npc, Player player) {
            super(npc);
            this.player = player;
        }
    }

    public static class DamageEvent extends NpcEvent implements ICancellableEvent {
        @Getter
        private final DamageSource source;
        @Getter
        @Setter
        private float amount;
        @Getter
        private final DamageContainer container;

        public DamageEvent(NpcView npc, DamageSource source, float amount, DamageContainer container) {
            super(npc);
            this.source = source;
            this.amount = amount;
            this.container = container;
        }
    }

    public static class TickEvent extends NpcEvent {
        public TickEvent(NpcView npc) {
            super(npc);
        }
    }

    public static class KillEntityEvent extends NpcEvent {
        @Getter
        private final Entity entity;

        public KillEntityEvent(NpcView npc, Entity entity) {
            super(npc);
            this.entity = entity;
        }
    }

    public static class MeleeAttackEvent extends NpcEvent {
        @Getter
        private final Entity target;

        public MeleeAttackEvent(NpcView npc, Entity target) {
            super(npc);
            this.target = target;
        }
    }

    public static class RangedAttackEvent extends NpcEvent {
        @Getter
        private final Entity target;

        public RangedAttackEvent(NpcView npc, Entity target) {
            super(npc);
            this.target = target;
        }
    }
}