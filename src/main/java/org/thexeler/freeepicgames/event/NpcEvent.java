package org.thexeler.freeepicgames.event;

import lombok.AllArgsConstructor;
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

@Getter
@AllArgsConstructor
public abstract class NpcEvent extends Event {
    protected final Entity entity;
    protected final NpcView view;

    public static class Create extends NpcEvent {
        public Create(NpcView npc) {
            super(npc.getOriginEntity(), npc);
        }
    }

    @Getter
    public static class Join extends NpcEvent {
        private final ServerLevel level;

        public Join(NpcView npc, ServerLevel level) {
            super(npc.getOriginEntity(), npc);
            this.level = level;
        }
    }

    @Getter
    public static class Death extends NpcEvent implements ICancellableEvent {
        private final DamageSource source;

        public Death(NpcView npc, DamageSource source) {
            super(npc.getOriginEntity(), npc);
            this.source = source;
        }
    }

    @Getter
    public static class Interact extends NpcEvent {
        private final Player player;

        public Interact(NpcView npc, Player player) {
            super(npc.getOriginEntity(), npc);
            this.player = player;
        }
    }

    @Getter
    public static class Damage extends NpcEvent implements ICancellableEvent {
        private final DamageSource source;
        @Setter
        private float amount;
        private final DamageContainer container;

        public Damage(NpcView npc, DamageSource source, float amount, DamageContainer container) {
            super(npc.getOriginEntity(), npc);
            this.source = source;
            this.amount = amount;
            this.container = container;
        }
    }

    public static class Tick extends NpcEvent {
        public Tick(NpcView npc) {
            super(npc.getOriginEntity(), npc);
        }
    }

    @Getter
    public static class Killed extends NpcEvent implements ICancellableEvent {
        private final Entity entity;
        private final DamageSource source;

        public Killed(NpcView npc, Entity entity, DamageSource source) {
            super(npc.getOriginEntity(), npc);
            this.entity = entity;
            this.source = source;
        }
    }

    @Getter
    public static class Attack extends NpcEvent {
        private final Entity target;
        private final DamageSource source;
        @Setter
        private float amount;
        private final DamageContainer container;

        public Attack(NpcView npc, Entity target, DamageSource source, float amount, DamageContainer container) {
            super(npc.getOriginEntity(), npc);
            this.target = target;
            this.source = source;
            this.amount = amount;
            this.container = container;
        }
    }
}