package org.thexeler.lamp.annotations;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public enum WithTargetLocationSuggestionFactory implements SuggestionProvider.Factory<ForgeCommandActor> {
    INSTANCE;

    @Override
    public @Nullable SuggestionProvider<ForgeCommandActor> create(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        if (annotations.get(WithTargetLocation.class) == null) {
            return null;
        }
        return context -> {
            List<String> ret = new ArrayList<>();
            ServerPlayer player = context.actor().requirePlayer();
            if (player != null) {
                BlockPos pos = BlockPos.containing(player.pick(20, 0, false).getLocation());
                if (!player.serverLevel().getBlockState(pos).isAir()) {
                    ret.add(String.valueOf(pos.getX()));
                    ret.add(pos.getX() + " " + pos.getY());
                    ret.add(pos.getX() + " " + pos.getY() + " " + pos.getZ());
                }
            }
            return ret;
        };
    }
}
