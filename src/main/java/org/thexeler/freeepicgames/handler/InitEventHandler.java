package org.thexeler.freeepicgames.handler;

import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.FreeEpicGamesKeys;
import org.thexeler.freeepicgames.database.type.JobType;
import org.thexeler.freeepicgames.database.type.NpcType;
import org.thexeler.freeepicgames.database.type.RaidTreasureType;
import org.thexeler.freeepicgames.database.type.RaidType;
import org.thexeler.freeepicgames.database.untils.ModSavedData;

@EventBusSubscriber
public class InitEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverWorldLoad(ServerStartedEvent event) {
        FreeEpicGames.RAID_WORLD = event.getServer().getLevel(FreeEpicGamesKeys.RAID_WORLD_KEY);
        FreeEpicGames.OVER_WORLD = event.getServer().overworld();

        if (FreeEpicGamesConfigs.isEnabledJob) {
            JobType.init();
        }
        NpcType.init();
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            RaidTreasureType.init();
            RaidType.init();
        }

        FreeEpicGames.SAVED_DATA = FreeEpicGames.OVER_WORLD.getDataStorage().computeIfAbsent(new SavedData.Factory<>(ModSavedData::create, ModSavedData::load), FreeEpicGames.MOD_ID);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOverWorldUnload(ServerStoppedEvent event) {
        FreeEpicGames.RAID_WORLD = null;
        FreeEpicGames.OVER_WORLD = null;

        if (FreeEpicGamesConfigs.isEnabledJob) {
            JobType.expire();
        }
        NpcType.expire();
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            RaidTreasureType.expire();
            RaidType.expire();
        }

        FreeEpicGames.SAVED_DATA = null;
    }
}
