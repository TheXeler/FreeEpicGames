package org.thexeler.lamp.annotations;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.storage.agent.CaptureWorldDataAgent;
import org.thexeler.freeepicgames.storage.view.AreaView;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.lang.reflect.Type;

public enum WithAreasSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {
    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithAreas.class) == null) {
            return null;
        }
        return context -> {
            ServerPlayer player = context.actor().requirePlayer();
            ServerLevel world = (player != null) ? player.serverLevel() : FreeEpicGames.OVER_WORLD;

            return CaptureWorldDataAgent.getInstance(world).getAllAreas().stream().map(AreaView::getName).toList();
        };
    }
}
