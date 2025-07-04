package org.thexeler.freeepicgames.command.lamp.annotations;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public enum WithTargetLocationYZSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {
    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithTargetLocationYZ.class) == null) {
            return null;
        }
        return context -> {
            List<String> ret = new ArrayList<>();
            ServerPlayer player = context.actor().requirePlayer();
            if (player != null) {
                BlockPos pos = BlockPos.containing(player.pick(20, 0, false).getLocation());
                try (Level level = player.level()) {
                    if (!level.getBlockState(pos).isAir()) {
                        ret.add(String.valueOf(pos.getY()));
                        ret.add(pos.getY() + " " + pos.getZ());
                    }
                } catch (IOException e) {
                    FreeEpicGames.LOGGER.error(e.getMessage());
                }
            }
            return ret;
        };
    }
}
