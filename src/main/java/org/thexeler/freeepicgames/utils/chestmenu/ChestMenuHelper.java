package org.thexeler.freeepicgames.utils.chestmenu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import org.thexeler.freeepicgames.FreeEpicGames;

import java.util.function.Consumer;

public class ChestMenuHelper {
    public static void openVirtualChest(ServerPlayer player, Container container) {
        openVirtualChest(player, container, "", null);
    }

    public static void openVirtualChest(ServerPlayer player, Container container, Consumer<Integer> onClick) {
        openVirtualChest(player, container, "", onClick);
    }

    public static void openVirtualChest(ServerPlayer player, Container container, String title) {
        openVirtualChest(player, container, title, null);
    }

    public static void openVirtualChest(ServerPlayer player, Container container, String title, Consumer<Integer> onClick) {
        SimpleMenuProvider provider;

        switch (container.getContainerSize()) {
            case 27:
                player.closeContainer();
                provider = new SimpleMenuProvider((ci, i, p) ->
                        EventChestMenu.SmallMenu(ci, p, container), Component.literal(title));
                break;
            case 54:
                player.closeContainer();
                provider = new SimpleMenuProvider((ci, i, p) ->
                        EventChestMenu.LargeMenu(ci, p, container), Component.literal(title));
                break;
            default:
                FreeEpicGames.LOGGER.warn("Virtual chest size is not supported. (Please use 27/54 size container)");
                return;
        }

        player.closeContainer();
        player.openMenu(provider);
    }

}
