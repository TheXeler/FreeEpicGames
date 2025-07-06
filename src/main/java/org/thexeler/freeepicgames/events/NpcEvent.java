package org.thexeler.freeepicgames.events;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.thexeler.freeepicgames.database.view.NpcView;

public abstract class NpcEvent extends Event {
    @Getter
    Entity entity;
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
        private final float amount;

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
}
