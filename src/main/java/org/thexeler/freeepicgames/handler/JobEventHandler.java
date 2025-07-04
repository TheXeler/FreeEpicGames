package org.thexeler.freeepicgames.handler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.database.agent.GlobalJobDataAgent;
import org.thexeler.freeepicgames.database.type.JobType;

@Mod.EventBusSubscriber
public class JobEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (FreeEpicGamesConfigs.isEnabledJob && event.getEntity() instanceof ServerPlayer player) {
            player.getInventory().clearContent();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (FreeEpicGamesConfigs.isEnabledJob && event.getEntity() instanceof ServerPlayer player) {
            GlobalJobDataAgent agent = GlobalJobDataAgent.getInstance();
            JobType type = JobType.getType(agent.getPlayerJob(player));
            if (type != null) {
                type.getAllItems().forEach(stack -> {
                    EquipmentSlot slot = ServerPlayer.getEquipmentSlotForItem(stack);
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
    public static void onItemUse(LivingEntityUseItemEvent event) {
        if (FreeEpicGamesConfigs.isEnabledJob && event.getEntity() instanceof ServerPlayer player) {
            if (event.getItem().getTag() != null) {
                String customCommand = event.getItem().getTag().getCompound("CustomCommand").getAsString();
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
