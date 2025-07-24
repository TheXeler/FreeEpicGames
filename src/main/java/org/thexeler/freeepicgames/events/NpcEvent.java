package org.thexeler.freeepicgames.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Event;
import org.thexeler.freeepicgames.storage.view.NpcView;
import org.thexeler.slacker.events.ICancellableEvent;

public abstract class NpcEvent extends Event {
    @Getter
    protected final Entity entity;
    @Getter
    protected final NpcView view;

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

        public DamageEvent(NpcView npc, DamageSource source, float amount) {
            super(npc);
            this.source = source;
            this.amount = amount;
        }
    }

    public static class TickEvent extends NpcEvent {
        public TickEvent(NpcView npc) {
            super(npc);
        }
    }

    public static class KilledEvent extends NpcEvent implements ICancellableEvent {
        @Getter
        private final Entity entity;
        @Getter
        private final DamageSource source;

        public KilledEvent(NpcView npc, Entity entity, DamageSource source) {
            super(npc);
            this.entity = entity;
            this.source = source;
        }
    }

    public static class MeleeAttackEvent extends NpcEvent {
        @Getter
        private final Entity target;
        @Getter
        private final DamageSource source;
        @Getter
        @Setter
        private float amount;

        public MeleeAttackEvent(NpcView npc, Entity target, DamageSource source, float amount) {
            super(npc);
            this.target = target;
            this.source = source;
            this.amount = amount;
        }
    }

    public abstract static class RangeAttack extends NpcEvent {
        @Getter
        private final Projectile projectile;

        public RangeAttack(NpcView npc, Projectile projectile) {
            super(npc);
            this.projectile = projectile;
        }

        public static class FireEvent extends RangeAttack {
            public FireEvent(NpcView npc, Projectile projectile) {
                super(npc, projectile);
            }
        }

        public static class HitEvent extends RangeAttack implements ICancellableEvent {
            @Getter
            private final HitResult hitResult;
            @Getter
            private final Entity target;

            public HitEvent(NpcView npc, Projectile projectile, HitResult hitResult, Entity target) {
                super(npc, projectile);
                this.hitResult = hitResult;
                this.target = target;
            }
        }
    }
}