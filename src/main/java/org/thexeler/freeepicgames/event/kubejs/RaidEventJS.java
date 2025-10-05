package org.thexeler.freeepicgames.event.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import org.thexeler.freeepicgames.event.RaidEvent;
import org.thexeler.freeepicgames.storage.view.RaidInstanceView;

@Getter
@AllArgsConstructor
public abstract class RaidEventJS implements KubeEvent {
    protected final RaidInstanceView view;

    public static class BuildEvent extends RaidEventJS {
        public BuildEvent(RaidEvent.BuildEvent buildEvent) {
            super(buildEvent.getView());
        }
    }

    public static class DestroyEvent extends RaidEventJS {
        public DestroyEvent(RaidEvent.DestroyEvent destroyEvent) {
            super(destroyEvent.getView());
        }
    }

    @Getter
    public static class OpenTreasureEvent extends RaidEventJS {
        private final Container container;
        private final ServerPlayer player;

        public OpenTreasureEvent(RaidEvent.OpenTreasureEvent openTreasureEvent) {
            super(openTreasureEvent.getView());
            this.container = openTreasureEvent.getContainer();
            this.player = openTreasureEvent.getPlayer();
        }
    }

    public static class TickEvent extends RaidEventJS {
        public TickEvent(RaidEvent.TickEvent tickEvent) {
            super(tickEvent.getView());
        }
    }
}
