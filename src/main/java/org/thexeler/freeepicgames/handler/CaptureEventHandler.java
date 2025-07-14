package org.thexeler.freeepicgames.handler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.database.agent.WorldCaptureDataAgent;
import org.thexeler.freeepicgames.database.untils.LogicTeam;
import org.thexeler.freeepicgames.database.view.AreaView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptureEventHandler {
    private int tickCount = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (FreeEpicGamesConfigs.isEnabledCapture) {
            tickCount++;
            if (tickCount % FreeEpicGamesConfigs.captureTick == 0) {
                tickCount -= FreeEpicGamesConfigs.captureTick;
                event.getServer().getAllLevels().forEach(serverLevel -> {
                    WorldCaptureDataAgent agent = WorldCaptureDataAgent.getInstance(serverLevel);
                    String attackerName = agent.getAttacker();
                    String defenderName = agent.getDefender();

                    agent.getAllAreas().stream().filter(AreaView::isLocked).forEach(area -> {
                        float areaSchedule = area.getSchedule();
                        AtomicInteger scheduleCount = new AtomicInteger();
                        LogicTeam controller = area.getController();
                        ServerBossEvent bossBar = area.getBossBar();
                        PlayerTeam attacker = serverLevel.getServer().getScoreboard().getPlayerTeam(attackerName);
                        PlayerTeam defender = serverLevel.getServer().getScoreboard().getPlayerTeam(defenderName);

                        serverLevel.players().forEach(player -> {
                            if (area.isInside(player.position())) {
                                if (attacker != null && attacker.getPlayers().contains(player.getName().getString())) {
                                    scheduleCount.getAndIncrement();
                                }
                                if (defender != null && defender.getPlayers().contains(player.getName().getString())) {
                                    scheduleCount.getAndDecrement();
                                }
                                bossBar.addPlayer(player);
                            } else {
                                bossBar.removePlayer(player);
                            }
                        });

                        if (scheduleCount.get() != 0) {
                            area.setSchedule((scheduleCount.get() * agent.getRate()) + area.getSchedule());
                        }

                        if (areaSchedule >= 100.0F) {
                            if (controller != LogicTeam.ATTACKER) {
                                serverLevel.players().forEach(player -> player.sendSystemMessage(Component.literal("进攻方已夺取区域" + area.getName() + "!")));
                                area.setController(LogicTeam.ATTACKER);
                            }
                        } else if (areaSchedule <= -100.0F) {
                            if (controller != LogicTeam.DEFENDER) {
                                serverLevel.players().forEach(player -> player.sendSystemMessage(Component.literal("防守方已夺取区域" + area.getName() + "!")));
                                area.setController(LogicTeam.DEFENDER);
                            }
                        } else if ((areaSchedule <= 50.0F && controller == LogicTeam.ATTACKER) || (areaSchedule >= -50.0F && controller == LogicTeam.DEFENDER)) {
                            serverLevel.players().forEach(player -> player.sendSystemMessage(Component.literal("区域" + area.getName() + "已被" +
                                    switch (controller) {
                                        case ATTACKER -> "防守方";
                                        case DEFENDER -> "进攻方";
                                        default -> "";
                                    } + "中立化!")));
                            area.setController(LogicTeam.NEUTRAL);
                        }
                    });
                });
            }
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof SpectralArrow arrow) {
            try (Level world = arrow.level()) {
                if (world.isClientSide()) return;
                if (arrow.getOwner() == null) return;
                WorldCaptureDataAgent agent = WorldCaptureDataAgent.getInstance((ServerLevel) world);
                EntityType<?> cannonType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("createbigcannons", "drop_mortar_shell"));

                String uuid = arrow.getOwner().getStringUUID();
                if (uuid.equals(agent.getAttackerCommander()) || uuid.equals(agent.getDefenderCommander())) {
                    Random random = new Random();
                    createCannonShell(cannonType, world,
                            arrow.getBlockX(),
                            arrow.getBlockY() + 100.0,
                            arrow.getBlockZ());
                    int i = 0;
                    while (i++ < 80) {
                        createCannonShell(cannonType, world,
                                arrow.getBlockX() + random.nextDouble(-20.0, 20),
                                arrow.getBlockY() + 300.0 + (i * 20.0),
                                arrow.getBlockZ() + random.nextDouble(-20.0, 20));
                    }
                    arrow.discard();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private Entity createCannonShell(EntityType<?> cannonType, Level world, double x, double y, double z) {
        Entity entity = cannonType.create(world);
        if (entity != null) {
            CompoundTag fuze = new CompoundTag();
            fuze.putInt("Count", 1);
            fuze.putString("id", "createbigcannons:impact_fuze");
            entity.getPersistentData().put("Fuze", fuze);
            entity.setPos(x, y, z);
            world.addFreshEntity(entity);
        } else {
            FreeEpicGames.LOGGER.error("Can not create entity by type \"{}\" !", cannonType);
        }
        return entity;
    }
}
