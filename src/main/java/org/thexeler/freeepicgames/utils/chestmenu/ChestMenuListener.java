package org.thexeler.freeepicgames.utils.chestmenu;

import net.minecraft.world.inventory.ClickType;

public abstract class ChestMenuListener {
    public abstract void onClose();

    public abstract void onClick(ClickType clickType, int slotId);
}
