package org.thexeler.freeepicgames;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FreeEpicGames.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class FreeEpicGamesConfigs {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLED_CAPTURE = BUILDER.comment("Enabled capture module.").define("EnabledCapture", true);
    private static final ModConfigSpec.BooleanValue ENABLED_CAPTURE_PERSISTENCE = BUILDER.comment("Persistence capture cache.").define("EnabledCapturePersistence", true);
    private static final ModConfigSpec.IntValue CAPTURE_TICK = BUILDER.comment("How many ticks is the interval for progress calculation.").defineInRange("CaptureTick", 20, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue ENABLED_JOB = BUILDER.comment("Enabled job module.").define("EnabledJob", true);
    private static final ModConfigSpec.BooleanValue ENABLED_JOB_PERSISTENCE = BUILDER.comment("Persistence job cache.").define("EnabledJobPersistence", true);

    private static final ModConfigSpec.BooleanValue ENABLED_RAID = BUILDER.comment("Enabled raid module.").define("EnabledRaid", true);
    private static final ModConfigSpec.BooleanValue ENABLED_RAID_PERSISTENCE = BUILDER.comment("Persistence raid cache.").define("EnabledRaidPersistence", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isEnabledCapture;
    public static boolean isEnabledCaptureCachePersistence;
    public static int captureTick;

    public static boolean isEnabledJob;
    public static boolean isEnabledJobCachePersistence;

    public static boolean isEnabledRaid;
    public static boolean isEnabledRaidCachePersistence;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        isEnabledCapture = ENABLED_CAPTURE.get();
        isEnabledCaptureCachePersistence = ENABLED_CAPTURE_PERSISTENCE.get();
        captureTick = CAPTURE_TICK.get();

        isEnabledJob = ENABLED_JOB.get();
        isEnabledJobCachePersistence = ENABLED_JOB_PERSISTENCE.get();

        isEnabledRaid = ENABLED_RAID.get();
        isEnabledRaidCachePersistence = ENABLED_RAID_PERSISTENCE.get();
    }
}
