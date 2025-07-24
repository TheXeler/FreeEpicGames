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
import org.thexeler.freeepicgames.storage.view.NpcView;

public abstract class NpcEvent extends Event {
    @Getter
    protected final Entity entity;
    @Getter
    protected final NpcView view;

    public NpcEvent(NpcView npc) {
        this.entity = npc.getOriginEntity();
        this.view = npc;
    }

    public static class Create extends NpcEvent {
        public Create(NpcView npc) {
            super(npc);
        }
    }

    public static class Join extends NpcEvent {
        @Getter
        private final ServerLevel level;

        public Join(NpcView npc, ServerLevel level) {
            super(npc);
            this.level = level;
        }
    }

    public static class Death extends NpcEvent implements ICancellableEvent {
        @Getter
        private final DamageSource source;

        public Death(NpcView npc, DamageSource source) {
            super(npc);
            this.source = source;
        }
    }

    public static class Interact extends NpcEvent {
        @Getter
        private final Player player;

        public Interact(NpcView npc, Player player) {
            super(npc);
            this.player = player;
        }
    }

    public static class Damage extends NpcEvent implements ICancellableEvent {
        @Getter
        private final DamageSource source;
        @Getter
        @Setter
        private float amount;
        @Getter
        private final DamageContainer container;

        public Damage(NpcView npc, DamageSource source, float amount, DamageContainer container) {
            super(npc);
            this.source = source;
            this.amount = amount;
            this.container = container;
        }
    }

    public static class Tick extends NpcEvent {
        public Tick(NpcView npc) {
            super(npc);
        }
    }

    public static class Killed extends NpcEvent implements ICancellableEvent {
        @Getter
        private final Entity entity;
        @Getter
        private final DamageSource source;

        public Killed(NpcView npc, Entity entity, DamageSource source) {
            super(npc);
            this.entity = entity;
            this.source = source;
        }
    }

    public static class Attack extends NpcEvent {
        @Getter
        private final Entity target;
        @Getter
        private final DamageSource source;
        @Getter
        @Setter
        private float amount;
        @Getter
        private final DamageContainer container;

        public Attack(NpcView npc, Entity target, DamageSource source, float amount, DamageContainer container) {
            super(npc);
            this.target = target;
            this.source = source;
            this.amount = amount;
            this.container = container;
        }
    }
}