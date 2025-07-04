package org.thexeler.freeepicgames.handler;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.freeepicgames.database.type.RaidTreasureType;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;

@EventBusSubscriber
public class RaidEventHandler {

    @SubscribeEvent
    public static void onOpenContainer(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (FreeEpicGames.RAID_WORLD.equals(event.getLevel())) {
                GlobalRaidDataAgent agent = GlobalRaidDataAgent.getInstance();
                RaidInstanceView view = agent.getRaidInstance(player);
                if (view != null) {
                    RaidTreasureType treasure = view.getTreasureType(event.getPos());
                    if (treasure != null) {
                        player.openMenu(view.getMenuProvider(player, event.getPos()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GlobalRaidDataAgent agent = GlobalRaidDataAgent.getInstance();
            RaidInstanceView view = agent.getRaidInstance(player);
            if (view != null) {
                view.respawn(player);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GlobalRaidDataAgent agent = GlobalRaidDataAgent.getInstance();
            RaidInstanceView view = agent.getRaidInstance(player);
            if (view != null && player.getRespawnPosition() != null && view.isInside(player.getRespawnPosition().getCenter())) {
                view.respawn(player);
            }
        }
    }
}