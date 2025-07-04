package org.thexeler.freeepicgames.command.lamp;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.command.lamp.annotations.*;
import org.thexeler.freeepicgames.command.lamp.exception.ForgeExceptionHandler;
import org.thexeler.freeepicgames.command.lamp.parameters.EntitySelectorListParameterTypeFactory;
import org.thexeler.freeepicgames.command.lamp.parameters.ServerPlayerParameterType;
import revxrsal.commands.Lamp;
import revxrsal.commands.brigadier.BrigadierConverter;
import revxrsal.commands.brigadier.BrigadierParser;

public class ForgeLamp {

    public static Lamp.Builder<ForgeCommandActor> builder(RegisterCommandsEvent event) {
        BrigadierConverter<ForgeCommandActor, CommandSourceStack> converter = new ForgeBrigadierConverter();
        BrigadierParser<CommandSourceStack, ForgeCommandActor> parser = new BrigadierParser<>(converter);

        Lamp.Builder<ForgeCommandActor> builder = Lamp.<ForgeCommandActor>builder()
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithAreasSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithJobTypeSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithTargetLocationSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithTargetLocationYZSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithTargetLocationZSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithTeamSuggestionFactory.INSTANCE))
                .permissionFactory(new RequiresOPPermissionFactory())
                .senderResolver(new ForgeSenderResolver())
                .exceptionHandler(new ForgeExceptionHandler())
                .hooks(hooks ->
                        hooks.onCommandRegistered((command, cancelHandle) -> {
                            LiteralCommandNode<CommandSourceStack> node = parser.createNode(command);
                            BrigadierParser.addChild(event.getDispatcher().getRoot(), node);
                        }));
        builder.parameterTypes()
                .addParameterTypeLast(ServerPlayer.class, new ServerPlayerParameterType())
                .addParameterTypeFactoryLast(new EntitySelectorListParameterTypeFactory());

        return builder;
    }
}
