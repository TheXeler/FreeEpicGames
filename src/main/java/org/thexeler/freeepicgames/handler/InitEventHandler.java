package org.thexeler.freeepicgames.handler;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.FreeEpicGamesKeys;
import org.thexeler.freeepicgames.database.type.JobType;
import org.thexeler.freeepicgames.database.type.NPCType;
import org.thexeler.freeepicgames.database.type.RaidTreasureType;
import org.thexeler.freeepicgames.database.type.RaidType;
import org.thexeler.freeepicgames.database.untils.ModSavedData;

@Mod.EventBusSubscriber
public class InitEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverWorldLoad(ServerStartedEvent event) {
        FreeEpicGames.RAID_WORLD = ServerLifecycleHooks.getCurrentServer().getLevel(FreeEpicGamesKeys.RAID_WORLD_KEY);
        FreeEpicGames.OVER_WORLD = ServerLifecycleHooks.getCurrentServer().overworld();

        if (FreeEpicGamesConfigs.isEnabledJob) {
            JobType.init();
        }
        NPCType.init();
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            RaidTreasureType.init();
            RaidType.init();
        }

        FreeEpicGames.SAVED_DATA = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(ModSavedData::load, ModSavedData::create, FreeEpicGames.MOD_ID);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOverWorldUnload(ServerStoppedEvent event) {
        FreeEpicGames.RAID_WORLD = null;
        FreeEpicGames.OVER_WORLD = null;

        if (FreeEpicGamesConfigs.isEnabledJob) {
            JobType.expire();
        }
        NPCType.expire();
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            RaidTreasureType.expire();
            RaidType.expire();
        }

        FreeEpicGames.SAVED_DATA = null;
    }
}
