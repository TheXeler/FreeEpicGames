package org.thexeler.freeepicgames.command.lamp.exception;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.exception.InvalidValueException;

/**
 * Thrown when an invalid value for a {@link ServerPlayer}
 * parameter is inputted in the command
 */
public class InvalidPlayerException extends InvalidValueException {

    public InvalidPlayerException(@NotNull String input) {
        super(input);
    }
}
