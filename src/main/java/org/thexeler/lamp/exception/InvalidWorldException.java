package org.thexeler.lamp.exception;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.exception.InvalidValueException;

/**
 * Thrown when an invalid value for a {@link ServerLevel} parameter
 * is inputted in the command
 */
public class InvalidWorldException extends InvalidValueException {

    public InvalidWorldException(@NotNull String input) {
        super(input);
    }
}
