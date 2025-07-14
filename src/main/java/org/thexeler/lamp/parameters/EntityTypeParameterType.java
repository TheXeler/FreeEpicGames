package org.thexeler.lamp.parameters;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.thexeler.slacker.registry.SlackerRegistries;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.ArrayList;
import java.util.List;

public final class EntityTypeParameterType implements ParameterType<ForgeCommandActor, EntityType<?>> {

    @Override
    public EntityType<?> parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull ForgeCommandActor> context) {
        String value = input.readString();
        return SlackerRegistries.ENTITY_TYPE.get(new ResourceLocation(value));
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull ForgeCommandActor> defaultSuggestions() {
        List<String> entityTypeKeys = new ArrayList<>();
        SlackerRegistries.ENTITY_TYPE.keySet().forEach(entityKey -> entityTypeKeys.add(entityKey.getNamespace() + ":" + entityKey.getPath()));
        return SuggestionProvider.of(entityTypeKeys);
    }
}
