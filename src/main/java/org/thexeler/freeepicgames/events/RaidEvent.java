package org.thexeler.freeepicgames.events;

import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.thexeler.slacker.events.ICancellableEvent;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;

import java.util.List;

public abstract class RaidEvent extends Event {
    // TODO

    @Getter
    private final RaidInstanceView view;

    public RaidEvent(RaidInstanceView view) {
        this.view = view;
    }

    public static class CreateEvent extends RaidEvent {
        public CreateEvent(RaidInstanceView view) {
            super(view);
        }
    }

    public static class DestroyEvent extends RaidEvent implements ICancellableEvent {
        public DestroyEvent(RaidInstanceView view) {
            super(view);
        }
    }

    public static class JoinEvent extends RaidEvent {
        public JoinEvent(RaidInstanceView view, List<ServerPlayer> players) {
            super(view);
        }
    }

    public static class TickEvent extends RaidEvent {
        public TickEvent(RaidInstanceView view) {
            super(view);
        }
    }
}
