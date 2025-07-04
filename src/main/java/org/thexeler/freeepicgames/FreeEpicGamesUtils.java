package org.thexeler.freeepicgames;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class FreeEpicGamesUtils {
    public static class ChestInterfaceHelper {
        public static void openVirtualChest(ServerPlayer player, Container container) {
            openVirtualChest(player, container, "");
        }

        public static void openVirtualChest(ServerPlayer player, Container container, String title) {
            switch (container.getContainerSize()) {
                case 27:
                    player.closeContainer();
                    player.openMenu(new SimpleMenuProvider((ci, i, p) ->
                            ChestMenu.threeRows(ci, i, container), Component.literal(title)));
                    break;
                case 54:
                    player.closeContainer();
                    player.openMenu(new SimpleMenuProvider((ci, i, p) ->
                            ChestMenu.sixRows(ci, i, container), Component.literal(title)));
                    break;
                default:
                    FreeEpicGames.LOGGER.warn("Virtual chest size is not supported. (Please use 27/54 size container)");
            }
        }

        public static void openChestMenu(ServerPlayer player, Container container) {
            openChestMenu(player, container, "");
        }

        public static void openChestMenu(ServerPlayer player, Container container, String title) {
            switch (container.getContainerSize()) {
                case 27:
                    player.closeContainer();
                    player.openMenu(new SimpleMenuProvider((ci, i, p) ->
                            ChestMenu.threeRows(ci, i, container), Component.literal(title)));
                    break;
                case 54:
                    player.closeContainer();
                    player.openMenu(new SimpleMenuProvider((ci, i, p) ->
                            ChestMenu.sixRows(ci, i, container), Component.literal(title)));
                    break;
                default:
                    FreeEpicGames.LOGGER.warn("Chest menu size is not supported. (Please use 27/54 size container)");
            }
        }
    }
}
