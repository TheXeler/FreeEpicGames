package org.thexeler.lamp.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.storage.type.JobType;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.lang.reflect.Type;

public enum WithJobTypeSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {
    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithJobType.class) == null) {
            return null;
        }
        return context -> JobType.getAllTypeName();
    }
}