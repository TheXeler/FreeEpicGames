package org.thexeler.freeepicgames.command.lamp.parameters;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.command.lamp.exception.EmptyEntitySelectorException;
import org.thexeler.freeepicgames.command.lamp.exception.MalformedEntitySelectorException;
import org.thexeler.freeepicgames.command.lamp.exception.MoreThanOneEntityException;
import org.thexeler.freeepicgames.command.lamp.exception.NonPlayerEntitiesException;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.List;

public final class ServerPlayerParameterType implements ParameterType<ForgeCommandActor, ServerPlayer> {

    private static @NotNull ServerPlayer fromSelector(@NotNull CommandSourceStack sender, @NotNull String selectorString) {
        try {
            EntitySelector selector = new EntitySelectorParser(new StringReader(selectorString)).getSelector();
            List<ServerPlayer> entityList = selector.findPlayers(sender);
            if (entityList.isEmpty())
                throw new EmptyEntitySelectorException(selectorString);
            if (entityList.size() != 1)
                throw new MoreThanOneEntityException(selectorString);
            Entity entity = entityList.get(0);
            if (!(entity instanceof ServerPlayer player))
                throw new NonPlayerEntitiesException(selectorString);
            return player;
        } catch (IllegalArgumentException e) {
            throw new MalformedEntitySelectorException(selectorString, e.getCause().getMessage());
        } catch (CommandSyntaxException e) {
            throw new CommandErrorException(e.getCause().getMessage());
        } catch (NoSuchMethodError e) {
            throw new CommandErrorException("Entity selectors on legacy versions are not supported yet!");
        }
    }

    @Override
    public ServerPlayer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull ForgeCommandActor> context) {
        String value = input.readString();
        return fromSelector(context.actor().source(), value);
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull ForgeCommandActor> defaultSuggestions() {
        return SuggestionProvider.empty();
    }
}
