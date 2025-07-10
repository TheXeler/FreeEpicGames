package org.thexeler.lamp;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.brigadier.BrigadierConverter;
import revxrsal.commands.brigadier.types.ArgumentTypes;
import revxrsal.commands.node.ParameterNode;

public class ForgeBrigadierConverter implements BrigadierConverter<ForgeCommandActor, CommandSourceStack> {

    private final ArgumentTypes<ForgeCommandActor> argumentTypes;

    public ForgeBrigadierConverter() {
        this(ArgumentTypes.<ForgeCommandActor>builder().build());
    }

    public ForgeBrigadierConverter(ArgumentTypes<ForgeCommandActor> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    @Override
    public @NotNull ArgumentType<?> getArgumentType(@NotNull ParameterNode<ForgeCommandActor, ?> parameter) {
        return argumentTypes.type(parameter);
    }

    @Override
    public @NotNull ForgeCommandActor createActor(@NotNull CommandSourceStack sender, @NotNull Lamp<ForgeCommandActor> lamp) {
        return new ForgeCommandActor(sender, lamp);
    }
}
