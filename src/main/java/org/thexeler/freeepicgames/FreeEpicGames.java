package org.thexeler.freeepicgames;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;
import org.thexeler.freeepicgames.database.agent.GlobalJobDataAgent;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.freeepicgames.database.agent.WorldCaptureDataAgent;
import org.thexeler.freeepicgames.database.agent.WorldNpcDataAgent;
import org.thexeler.freeepicgames.database.type.JobType;
import org.thexeler.freeepicgames.database.type.NpcType;
import org.thexeler.freeepicgames.database.type.RaidTreasureType;
import org.thexeler.freeepicgames.database.type.RaidType;
import org.thexeler.freeepicgames.database.untils.ModSavedData;
import org.thexeler.freeepicgames.handler.*;

import java.util.ArrayList;
import java.util.List;

@Mod(FreeEpicGames.MOD_ID)
public class FreeEpicGames {
    public static final String MOD_ID = "freeepicgames";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ModSavedData SAVED_DATA = null;
    public static ServerLevel RAID_WORLD = null;
    public static ServerLevel OVER_WORLD = null;

    public static final List<Object> EVENT_LISTENER = new ArrayList<>();

    public FreeEpicGames(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.SERVER, FreeEpicGamesConfigs.SPEC);

        EVENT_LISTENER.add(new CaptureEventHandler());
        EVENT_LISTENER.add(new CommandEventHandler());
        EVENT_LISTENER.add(new JobEventHandler());
        EVENT_LISTENER.add(new NpcEventHandler());
        EVENT_LISTENER.add(new RaidEventHandler());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOverWorldLoad(ServerStartedEvent event) {
        FreeEpicGames.RAID_WORLD = event.getServer().getLevel(FreeEpicGamesKeys.RAID_WORLD_KEY);
        FreeEpicGames.OVER_WORLD = event.getServer().overworld();

        if (FreeEpicGamesConfigs.isEnabledJob) {
            JobType.init();
        }
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            RaidTreasureType.init();
            RaidType.init();
        }
        NpcType.init();
        FreeEpicGames.SAVED_DATA = FreeEpicGames.OVER_WORLD.getDataStorage().computeIfAbsent(new SavedData.Factory<>(ModSavedData::create, ModSavedData::load), FreeEpicGames.MOD_ID);

        EVENT_LISTENER.forEach(NeoForge.EVENT_BUS::register);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onOverWorldUnload(ServerStoppedEvent event) {
        FreeEpicGames.RAID_WORLD = null;
        FreeEpicGames.OVER_WORLD = null;

        if (FreeEpicGamesConfigs.isEnabledJob) {
            JobType.expire();
        }
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            RaidTreasureType.expire();
            RaidType.expire();
        }
        NpcType.expire();
        WorldCaptureDataAgent.expire();
        GlobalJobDataAgent.expire();
        GlobalRaidDataAgent.expire();
        WorldNpcDataAgent.expire();

        FreeEpicGames.SAVED_DATA = null;

        EVENT_LISTENER.forEach(NeoForge.EVENT_BUS::unregister);
    }
}
