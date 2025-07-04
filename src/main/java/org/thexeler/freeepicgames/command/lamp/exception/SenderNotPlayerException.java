package org.thexeler.freeepicgames.command.lamp.exception;

import revxrsal.commands.exception.ThrowableFromCommand;

/**
 * Thrown when a player-only command is executed by a non-player
 */
@ThrowableFromCommand
public class SenderNotPlayerException extends RuntimeException {

}
