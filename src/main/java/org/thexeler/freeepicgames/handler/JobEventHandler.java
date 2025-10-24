package org.thexeler.freeepicgames.handler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.storage.agent.JobDataAgent;
import org.thexeler.freeepicgames.storage.type.JobType;

public class JobEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (FreeEpicGamesConfigs.isEnabledClasses && event.getEntity() instanceof ServerPlayer player) {
            player.getInventory().clearContent();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (FreeEpicGamesConfigs.isEnabledClasses && event.getEntity() instanceof ServerPlayer player) {
            JobDataAgent agent = JobDataAgent.getInstance();
            JobType type = JobType.getType(agent.getPlayerJob(player));
            if (type != null) {
                type.getAllItems().forEach(stack -> {
                    EquipmentSlot slot = player.getEquipmentSlotForItem(stack);
                    if (player.getItemBySlot(slot).isEmpty()) {
                        player.getInventory().add(stack);
                    } else {
                        switch (slot) {
                            case HEAD:
                            case CHEST:
                            case LEGS:
                            case FEET:
                                player.onEquipItem(slot, player.getItemBySlot(slot), stack);
                                break;
                            default:
                                player.getInventory().add(stack);
                                break;
                        }
                    }
                });
            } else {
                agent.setPlayerJob(player, "");
            }
        }
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent event) {
        if (FreeEpicGamesConfigs.isEnabledClasses && event.getEntity() instanceof ServerPlayer player) {
            CompoundTag data = event.getItem().serializeNBT();
            if (data != null && data.contains("custom_command")) {
                String customCommand = data.getString("custom_command");
                if (!customCommand.isEmpty()) {
                    try {
                        player.server.getCommands().getDispatcher().execute(customCommand, player.server.createCommandSourceStack());
                    } catch (CommandSyntaxException e) {
                        FreeEpicGames.LOGGER.error(e.getMessage());
                    }
                }
            }
        }
    }
}
