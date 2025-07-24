package org.thexeler.freeepicgames;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.thexeler.freeepicgames.handler.CaptureEventHandler;
import org.thexeler.freeepicgames.handler.JobEventHandler;
import org.thexeler.freeepicgames.handler.NpcEventHandler;
import org.thexeler.freeepicgames.handler.RaidEventHandler;
import org.thexeler.freeepicgames.storage.agent.JobDataAgent;
import org.thexeler.freeepicgames.storage.agent.RaidDataAgent;
import org.thexeler.freeepicgames.storage.agent.CaptureWorldDataAgent;
import org.thexeler.freeepicgames.storage.agent.NpcWorldDataAgent;
import org.thexeler.freeepicgames.storage.type.JobType;
import org.thexeler.freeepicgames.storage.type.NpcType;
import org.thexeler.freeepicgames.storage.type.RaidTreasureType;
import org.thexeler.freeepicgames.storage.type.RaidType;
import org.thexeler.freeepicgames.storage.utils.ModSavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
        EVENT_LISTENER.add(new JobEventHandler());
        EVENT_LISTENER.add(new NpcEventHandler());
        EVENT_LISTENER.add(new RaidEventHandler());
    }

    @SubscribeEvent
    public void onReloadListenerAdd(AddReloadListenerEvent event) {
        event.addListener(new PreparableReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
                if (FreeEpicGamesConfigs.isEnabledJob) {
                    JobType.expire(true);
                    JobType.init();
                }
                if (FreeEpicGamesConfigs.isEnabledRaid) {
                    RaidTreasureType.expire(true);
                    RaidTreasureType.init();
                    RaidType.expire(true);
                    RaidType.init();
                }
                NpcType.expire(true);
                NpcType.init();

                return preparationBarrier.wait(CompletableFuture.completedFuture(null)).thenRunAsync(() -> LOGGER.info("配置文件重载完成"), gameExecutor);
            }
        });
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

    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {

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
        CaptureWorldDataAgent.expire();
        JobDataAgent.expire();
        RaidDataAgent.expire();
        NpcWorldDataAgent.expire();

        FreeEpicGames.SAVED_DATA = null;

        EVENT_LISTENER.forEach(NeoForge.EVENT_BUS::unregister);
    }
}
