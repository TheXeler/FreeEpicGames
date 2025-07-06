package org.thexeler.freeepicgames.events;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.thexeler.freeepicgames.database.view.NPCView;

public abstract class NpcEvent extends Event {
    @Getter
    Entity entity;
    @Getter
    private final NPCView view;

    public NpcEvent(NPCView npc) {
        this.entity = npc.getOriginEntity();
        this.view = npc;
    }

    public static class CreateEvent extends NpcEvent {
        public CreateEvent(NPCView npc) {
            super(npc);
        }
    }

    public static class JoinEvent extends NpcEvent {
        @Getter
        private final ServerLevel level;

        public JoinEvent(NPCView npc, ServerLevel level) {
            super(npc);
            this.level = level;
        }
    }


    public static class DeathEvent extends NpcEvent implements ICancellableEvent {
        @Getter
        private final DamageSource source;

        public DeathEvent(NPCView npc, DamageSource source) {
            super(npc);
            this.source = source;
        }
    }

    public static class InteractEvent extends NpcEvent {
        @Getter
        private final Player player;

        public InteractEvent(NPCView npc, Player player) {
            super(npc);
            this.player = player;
        }
    }

    public static class DamageEvent extends NpcEvent implements ICancellableEvent {
        @Getter
        private final DamageSource source;
        @Getter
        private final float amount;

        public DamageEvent(NPCView npc, DamageSource source, float amount) {
            super(npc);
            this.source = source;
            this.amount = amount;
        }
    }

    public static class TickEvent extends NpcEvent {
        public TickEvent(NPCView npc) {
            super(npc);
        }
    }
}
