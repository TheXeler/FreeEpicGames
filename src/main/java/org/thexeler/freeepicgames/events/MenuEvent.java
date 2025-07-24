package org.thexeler.freeepicgames.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraftforge.eventbus.api.Event;
import org.thexeler.slacker.events.ICancellableEvent;
import org.thexeler.freeepicgames.utils.chestmenu.EventChestMenu;

public abstract class MenuEvent extends Event {
    @Getter
    protected final Player player;
    @Getter
    protected final int index;
    @Getter
    protected final EventChestMenu menu;

    @Getter
    @Setter
    protected boolean keepVanillaLogic;

    public MenuEvent(Player player, int index, EventChestMenu menu) {
        this.player = player;
        this.index = index;
        this.menu = menu;
        this.keepVanillaLogic = false;
    }

    public static class CreateEvent extends MenuEvent {
        public CreateEvent(Player player, int index, EventChestMenu menu) {
            super(player, index, menu);
        }
    }

    public static class OpenedEvent extends MenuEvent {
        public OpenedEvent(Player player, int index, EventChestMenu menu) {
            super(player, index, menu);
        }
    }

    public static class CloseEvent extends MenuEvent {
        public CloseEvent(Player player, int index, EventChestMenu menu) {
            super(player, index, menu);
        }
    }

    public static class ClickEvent extends MenuEvent implements ICancellableEvent {
        @Getter
        private final ClickType clickType;

        public ClickEvent(Player player, int index, ClickType clickType, EventChestMenu menu) {
            super(player, index, menu);
            this.clickType = clickType;
        }
    }
}
