package org.thexeler.freeepicgames.events.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import org.thexeler.freeepicgames.events.MenuEvent;
import org.thexeler.freeepicgames.utils.chestmenu.EventChestMenu;

@Getter
@AllArgsConstructor
public abstract class MenuEventJS implements KubeEvent {
    protected final Player player;
    protected final int index;
    protected final EventChestMenu menu;

    public static class CreateEvent extends MenuEventJS {
        public CreateEvent(MenuEvent.CreateEvent createEvent) {
            super(createEvent.getPlayer(), createEvent.getIndex(), createEvent.getMenu());
        }
    }

    public static class OpenedEvent extends MenuEventJS {
        public OpenedEvent(MenuEvent.OpenedEvent openedEvent) {
            super(openedEvent.getPlayer(), openedEvent.getIndex(), openedEvent.getMenu());
        }
    }

    public static class CloseEvent extends MenuEventJS {
        public CloseEvent(MenuEvent.CloseEvent closeEvent) {
            super(closeEvent.getPlayer(), closeEvent.getIndex(), closeEvent.getMenu());
        }
    }

    @Getter
    public static class ClickEvent extends MenuEventJS {
        private final ClickType clickType;

        public ClickEvent(MenuEvent.ClickEvent clickEvent) {
            super(clickEvent.getPlayer(), clickEvent.getIndex(), clickEvent.getMenu());
            this.clickType = clickEvent.getClickType();
        }
    }
}
