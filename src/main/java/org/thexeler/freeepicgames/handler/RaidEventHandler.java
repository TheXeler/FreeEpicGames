package org.thexeler.freeepicgames.handler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesKeys;
import org.thexeler.freeepicgames.events.RaidEvent;
import org.thexeler.freeepicgames.storage.agent.RaidDataAgent;
import org.thexeler.freeepicgames.storage.type.RaidTreasureType;
import org.thexeler.freeepicgames.storage.view.RaidInstanceView;
import org.thexeler.freeepicgames.utils.chestmenu.ChestMenuHelper;
import oshi.util.tuples.Pair;

import java.util.Collections;

public class RaidEventHandler {

    @SubscribeEvent
    public void onOpenContainer(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (FreeEpicGames.RAID_WORLD.equals(event.getLevel())) {
                RaidDataAgent agent = RaidDataAgent.getInstance();
                RaidInstanceView view = agent.getRaidInstance(player);
                if (view != null) {
                    RaidTreasureType treasure = view.getTreasureType(event.getPos());
                    if (treasure != null) {
                        if (!NeoForge.EVENT_BUS.post(new RaidEvent.OpenTreasureEvent(
                                view, view.getTreasureContainer(player, event.getPos()), player)).isCanceled()) {
                            Container container = view.getTreasureContainer(player, event.getPos());
                            ChestMenuHelper.openVirtualChest(player, container, treasure.getTitle());
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.level().equals(FreeEpicGames.RAID_WORLD)) {
                RaidDataAgent agent = RaidDataAgent.getInstance();
                RaidInstanceView view = agent.getRaidInstance(player);
                if (view != null) {
                    view.respawn(player);
                } else {
                    Pair<String, Vec3> backPosInfo = agent.getBackPos(player);
                    ServerLevel level = FreeEpicGames.OVER_WORLD;
                    if (backPosInfo != null) {
                        if (player.getServer() != null) {
                            level = player.getServer().getLevel(FreeEpicGamesKeys.parseWorldKey(backPosInfo.getA()));
                        }
                        if (level != null) {
                            player.teleportTo(level, backPosInfo.getB().x, backPosInfo.getB().y, backPosInfo.getB().z, Collections.emptySet(), 0.0F, 0.0F);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaidDataAgent agent = RaidDataAgent.getInstance();
            RaidInstanceView view = agent.getRaidInstance(player);
            if (view != null && player.getRespawnPosition() != null && view.isInside(player.getRespawnPosition().getCenter())) {
                view.respawn(player);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRaidTick(ServerTickEvent.Post event) {
        RaidDataAgent agent = RaidDataAgent.getInstance();
        agent.getAllRaidInstance().forEach(view -> {
            NeoForge.EVENT_BUS.post(new RaidEvent.TickEvent(view));
        });
    }
}