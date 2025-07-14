package org.thexeler.lamp.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public enum WithTeamSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {

    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithTeam.class) == null) {
            return null;
        }
        return context -> {
            List<String> teamsName = new ArrayList<>();
            FreeEpicGames.OVER_WORLD.getScoreboard().getTeamNames();
            return teamsName;
        };
    }
}
