package org.thexeler.lamp.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.storage.type.NpcType;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.lang.reflect.Type;

public enum WithNpcTypeSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {
    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithNpcType.class) == null) {
            return null;
        }
        return context -> NpcType.getAllTypeName();
    }
}
