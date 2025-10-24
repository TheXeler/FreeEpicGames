package org.thexeler.freeepicgames.utils.chestmenu;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.event.MenuEvent;
import org.thexeler.slacker.SlackerForge;

import java.util.HashMap;
import java.util.Map;

public class EventChestMenu extends AbstractContainerMenu {
    @Getter
    private final Container container;
    @Getter
    private final int containerRows;

    private final Map<String, ChestMenuListener> listeners;

    public static EventChestMenu SmallMenu(int containerId, Player player, Container container) {
        return new EventChestMenu(MenuType.GENERIC_9x3, containerId, player, container, 3);
    }

    public static EventChestMenu LargeMenu(int containerId, Player player, Container container) {
        return new EventChestMenu(MenuType.GENERIC_9x6, containerId, player, container, 6);
    }

    protected EventChestMenu(MenuType<?> type, int containerId, Player player, Container container, int rows) {
        super(type, containerId);

        if (FreeEpicGamesConfigs.isEnabledChestMenuPostEventBus) {
            SlackerForge.EVENT_BUS.post(new MenuEvent.CreateEvent(player, containerId, this));
        }

        this.container = container;
        this.containerRows = rows;
        this.listeners = new HashMap<>();
        container.startOpen(player);
        int i = (this.containerRows - 4) * 18;

        for (
                int j = 0;
                j < this.containerRows; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(container, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (
                int l = 0;
                l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(player.getInventory(), j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (
                int i1 = 0;
                i1 < 9; i1++) {
            this.addSlot(new Slot(player.getInventory(), i1, 8 + i1 * 18, 161 + i));
        }

        if (FreeEpicGamesConfigs.isEnabledChestMenuPostEventBus) {
            SlackerForge.EVENT_BUS.post(new MenuEvent.OpenedEvent(player, containerId, this));
        }
    }

    public void registerListener(ClickType type, String key, ChestMenuListener listener) {
        this.listeners.put(key, listener);
    }

    public void unregisterListener(ClickType type, String key) {
        this.listeners.remove(key);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        this.listeners.forEach((key, listener) -> listener.onClick(clickType, slotId));

        boolean isCanceled = true;
        if (FreeEpicGamesConfigs.isEnabledChestMenuPostEventBus) {
            isCanceled = SlackerForge.EVENT_BUS.post(new MenuEvent.ClickEvent(player, slotId, clickType, this)).isCanceled();
        }
        if (!isCanceled) {
            super.clicked(slotId, button, clickType, player);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;

        this.listeners.forEach((key, listener) -> listener.onClick(ClickType.QUICK_MOVE, index));

        boolean isCanceled = true;
        if (FreeEpicGamesConfigs.isEnabledChestMenuPostEventBus) {
            isCanceled = SlackerForge.EVENT_BUS.post(new MenuEvent.ClickEvent(player, index, ClickType.QUICK_MOVE, this)).isCanceled();
        }
        if (!isCanceled) {
            Slot slot = this.slots.get(index);
            if (slot.hasItem()) {
                ItemStack itemStack1 = slot.getItem();
                itemstack = itemStack1.copy();
                if (index < this.containerRows * 9) {
                    if (!this.moveItemStackTo(itemStack1, this.containerRows * 9, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemStack1, 0, this.containerRows * 9, false)) {
                    return ItemStack.EMPTY;
                }

                if (itemStack1.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }
        }

        return itemstack;
    }

    @Override
    public void removed(@NotNull Player player) {
        this.listeners.forEach((key, listener) -> listener.onClose());

        if (FreeEpicGamesConfigs.isEnabledChestMenuPostEventBus) {
            SlackerForge.EVENT_BUS.post(new MenuEvent.CloseEvent(player, this.containerId, this));
        }
        super.removed(player);
        this.container.stopOpen(player);
    }
}
