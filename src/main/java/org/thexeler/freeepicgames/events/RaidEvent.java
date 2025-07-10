package org.thexeler.freeepicgames.events;

import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;

public abstract class RaidEvent extends Event {
    // TODO

    @Getter
    private final RaidInstanceView view;

    public RaidEvent(RaidInstanceView view) {
        this.view = view;
    }

    public static class BuildEvent extends RaidEvent {
        public BuildEvent(RaidInstanceView view) {
            super(view);
        }
    }

    public static class DestroyEvent extends RaidEvent implements ICancellableEvent {
        public DestroyEvent(RaidInstanceView view) {
            super(view);
        }
    }

    public static class OpenTreasureEvent extends RaidEvent implements ICancellableEvent {
        @Getter
        private final Container container;
        @Getter
        private final ServerPlayer player;

        public OpenTreasureEvent(RaidInstanceView view, Container container, ServerPlayer player) {
            super(view);
            this.container = container;
            this.player = player;
        }
    }

    public static class TickEvent extends RaidEvent {
        public TickEvent(RaidInstanceView view) {
            super(view);
        }
    }
}
