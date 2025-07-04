package org.thexeler.freeepicgames.command.lamp.annotations;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.command.CommandPermission;

public class RequiresOPPermissionFactory implements CommandPermission.Factory<ForgeCommandActor> {
    @Override
    public @Nullable CommandPermission<ForgeCommandActor> create(@NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        return actor -> {
            ServerPlayer player = actor.requirePlayer();
            if (player != null) {
                try {
                    return player.server.getPlayerList().isOp(player.getGameProfile());
                } catch (NullPointerException e) {
                    return false;
                }
            } else {
                return true;
            }
        };
    }
}
