package org.thexeler.freeepicgames.command.lamp.annotations;

import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum WithTeamSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {

    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithTeam.class) == null) {
            return null;
        }
        return context -> {
            List<String> teamsName = new ArrayList<>();
            Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getScoreboard().getTeamNames());
            return teamsName;
        };
    }
}
