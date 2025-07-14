package org.thexeler.lamp.actor;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.Lamp;
import revxrsal.commands.command.CommandActor;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ForgeCommandActor(CommandSourceStack source, Lamp<?> lamp) implements CommandActor {

    @Override
    public @NotNull String name() {
        return source.getTextName();
    }

    @Override
    public @NotNull UUID uniqueId() {
        if (source.getEntity() != null) {
            return source.getEntity().getUUID();
        }
        return UUID.nameUUIDFromBytes(name().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void reply(@NotNull String message) {
        this.reply(Component.literal(message));
    }

    public void reply(@NotNull Component message){
        source.sendSystemMessage(message);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        source.sendSystemMessage(Component.literal(message));
    }

    @Override
    public void sendRawError(@NotNull String message) {
        // 16733525 is RED according to TextColor
        source.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
    }

    @Override
    public @NotNull CommandSourceStack source() {
        return source;
    }

    public @Nullable ServerPlayer requirePlayer() {
        return source.getPlayer();
    }

    public ServerLevel getLevel() {
        if (source.getPlayer() != null) {
            return source.getPlayer().serverLevel();
        } else {
            return source.getServer().overworld();
        }
    }
}
