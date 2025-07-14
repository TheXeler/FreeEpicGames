package org.thexeler.lamp.exception;

import org.jetbrains.annotations.NotNull;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.exception.*;
import revxrsal.commands.node.ParameterNode;


public class ForgeExceptionHandler extends DefaultExceptionHandler<ForgeCommandActor> {

    @HandleException
    public void onInvalidPlayer(InvalidPlayerException e, ForgeCommandActor actor) {
        actor.error("&cInvalid player: &e" + e.input() + "&c.");
    }

    @HandleException
    public void onInvalidWorld(InvalidWorldException e, ForgeCommandActor actor) {
        actor.error("&cInvalid world: &e" + e.input() + "&c.");
    }

    @HandleException
    public void onSenderNotConsole(SenderNotConsoleException e, ForgeCommandActor actor) {
        actor.error("&cYou must be the console to execute this command!");
    }

    @HandleException
    public void onSenderNotPlayer(SenderNotPlayerException e, ForgeCommandActor actor) {
        actor.error("&cYou must be a player to execute this command!");
    }

    @HandleException
    public void onMalformedEntitySelector(MalformedEntitySelectorException e, ForgeCommandActor actor) {
        actor.error("&cMalformed entity selector: &e" + e.input() + "&c. Error: &e" + e.errorMessage());
    }

    @HandleException
    public void onNonPlayerEntities(NonPlayerEntitiesException e, ForgeCommandActor actor) {
        actor.error("&cYour entity selector (&e" + e.input() + "&c) only allows players, but it contains non-player entities too.");
    }

    @HandleException
    public void onMoreThanOneEntity(MoreThanOneEntityException e, ForgeCommandActor actor) {
        actor.error("&cOnly one entity is allowed, but the provided selector allows more than one");
    }

    @HandleException
    public void onEmptyEntitySelector(EmptyEntitySelectorException e, ForgeCommandActor actor) {
        actor.error("&cNo entities were found.");
    }

    @Override public void onEnumNotFound(@NotNull EnumNotFoundException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cInvalid choice: &e" + e.input() + "&c. Please enter a valid option from the available values.");
    }

    @Override public void onExpectedLiteral(@NotNull ExpectedLiteralException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cExpected &e" + e.node().name() + "&c, found &e" + e.input() + "&c.");
    }

    @Override public void onInputParse(@NotNull InputParseException e, @NotNull ForgeCommandActor actor) {
        switch (e.cause()) {
            case INVALID_ESCAPE_CHARACTER ->
                    actor.error("&cInvalid input. Use &e\\\\ &cto include a backslash.");
            case UNCLOSED_QUOTE -> actor.error("&cUnclosed quote. Make sure to close all quotes.");
            case EXPECTED_WHITESPACE ->
                    actor.error("&cExpected whitespace to end one argument, but found trailing data.");
        }
    }

    @Override
    public void onInvalidListSize(@NotNull InvalidListSizeException e, @NotNull ForgeCommandActor actor, @NotNull ParameterNode<ForgeCommandActor, ?> parameter) {
        if (e.inputSize() < e.minimum())
            actor.error("&cYou must input at least &e" + fmt(e.minimum()) + " &centries for &e" + parameter.name() + "&c.");
        if (e.inputSize() > e.maximum())
            actor.error("&cYou must input at most &e" + fmt(e.maximum()) + " &centries for &e" + parameter.name() + "&c.");
    }

    @Override
    public void onInvalidStringSize(@NotNull InvalidStringSizeException e, @NotNull ForgeCommandActor actor, @NotNull ParameterNode<ForgeCommandActor, ?> parameter) {
        if (e.input().length() < e.minimum())
            actor.error("&cParameter &e" + parameter.name() + " &cmust be at least &e" + fmt(e.minimum()) + " &ccharacters long.");
        if (e.input().length() > e.maximum())
            actor.error("&cParameter &e" + parameter.name() + " &ccan be at most &e" + fmt(e.maximum()) + " &ccharacters long.");
    }

    @Override public void onInvalidBoolean(@NotNull InvalidBooleanException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cExpected &etrue &cor &efalse&c, found &e" + e.input() + "&c.");
    }

    @Override public void onInvalidDecimal(@NotNull InvalidDecimalException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cInvalid number: &e" + e.input() + "&c.");
    }

    @Override public void onInvalidInteger(@NotNull InvalidIntegerException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cInvalid integer: &e" + e.input() + "&c.");
    }

    @Override public void onInvalidUUID(@NotNull InvalidUUIDException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cInvalid UUID: " + e.input() + "&c.");
    }

    @Override
    public void onMissingArgument(@NotNull MissingArgumentException e, @NotNull ForgeCommandActor actor, @NotNull ParameterNode<ForgeCommandActor, ?> parameter) {
        actor.error("&cRequired parameter is missing: &e" + parameter.name() + "&c. Usage: &e/" + parameter.command().usage() + "&c.");
    }

    @Override public void onNoPermission(@NotNull NoPermissionException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cYou do not have permission to execute this command!");
    }

    @Override
    public void onNumberNotInRange(@NotNull NumberNotInRangeException e, @NotNull ForgeCommandActor actor, @NotNull ParameterNode<ForgeCommandActor, Number> parameter) {
        if (e.input().doubleValue() < e.minimum())
            actor.error("&c" + parameter.name() + " too small &e(" + fmt(e.input()) + ")&c. Must be at least &e" + fmt(e.minimum()) + "&c.");
        if (e.input().doubleValue() > e.maximum())
            actor.error("&c" + parameter.name() + " too large &e(" + fmt(e.input()) + ")&c. Must be at most &e" + fmt(e.maximum()) + "&c.");
    }

    @Override public void onInvalidHelpPage(@NotNull InvalidHelpPageException e, @NotNull ForgeCommandActor actor) {
        if (e.numberOfPages() == 1)
            actor.error("Invalid help page: &e" + e.page() + "&c. Must be 1.");
        else
            actor.error("Invalid help page: &e" + e.page() + "&c. Must be between &e1 &cand &e" + e.numberOfPages());
    }

    @Override public void onUnknownCommand(@NotNull UnknownCommandException e, @NotNull ForgeCommandActor actor) {
        actor.error("&cUnknown command: &e" + e.input() + "&c.");
    }
}
