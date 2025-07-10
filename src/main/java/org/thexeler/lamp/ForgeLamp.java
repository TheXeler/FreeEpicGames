package org.thexeler.lamp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.lamp.annotations.*;
import org.thexeler.lamp.exception.ForgeExceptionHandler;
import org.thexeler.lamp.parameters.EntitySelectorListParameterTypeFactory;
import org.thexeler.lamp.parameters.ServerPlayerParameterType;
import revxrsal.commands.Lamp;
import revxrsal.commands.brigadier.BrigadierConverter;
import revxrsal.commands.brigadier.BrigadierParser;

public class ForgeLamp {

    public static Lamp.Builder<ForgeCommandActor> builder(CommandDispatcher<CommandSourceStack> dispatcher) {
        BrigadierConverter<ForgeCommandActor, CommandSourceStack> converter = new ForgeBrigadierConverter();
        BrigadierParser<CommandSourceStack, ForgeCommandActor> parser = new BrigadierParser<>(converter);

        Lamp.Builder<ForgeCommandActor> builder = Lamp.<ForgeCommandActor>builder()
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithAreasSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithJobTypeSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithNpcSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithNpcTypeSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithRaidTypeSuggestionFactory.INSTANCE))
                .suggestionProviders(suggestion -> suggestion.addProviderFactory(WithRaidTreasureTypeSuggestionFactory.INSTANCE))
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
                            BrigadierParser.addChild(dispatcher.getRoot(), node);
                        }));
        builder.parameterTypes()
                .addParameterTypeLast(ServerPlayer.class, new ServerPlayerParameterType())
                .addParameterTypeFactoryLast(new EntitySelectorListParameterTypeFactory());

        return builder;
    }
}
