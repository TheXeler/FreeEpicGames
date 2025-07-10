package org.thexeler.lamp;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.lamp.exception.SenderNotPlayerException;
import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.process.SenderResolver;

public final class ForgeSenderResolver implements SenderResolver<ForgeCommandActor> {

    @Override
    public boolean isSenderType(@NotNull CommandParameter parameter) {
        Class<?> type = parameter.type();
        return CommandSourceStack.class.isAssignableFrom(type)
                || CommandSource.class.isAssignableFrom(type);
    }

    @Override
    public @NotNull Object getSender(@NotNull Class<?> customSenderType, @NotNull ForgeCommandActor actor, @NotNull ExecutableCommand<ForgeCommandActor> command) {
        if (CommandSourceStack.class.isAssignableFrom(customSenderType))
            return actor.source();
        if (ServerPlayer.class.isAssignableFrom(customSenderType)) {
            ServerPlayer player = actor.source().getPlayer();
            if (player == null)
                throw new SenderNotPlayerException();
            return player;
        }
        if (CommandSource.class.isAssignableFrom(customSenderType)) {
            return actor.source();
        }
        throw new IllegalArgumentException("Should not be reached.");
    }
}
