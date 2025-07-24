package org.thexeler.freeepicgames;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = FreeEpicGames.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FreeEpicGamesConfigs {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLED_CAPTURE = BUILDER.comment("Enabled capture module.").define("EnabledCapture", true);
    private static final ForgeConfigSpec.BooleanValue ENABLED_CAPTURE_PERSISTENCE = BUILDER.comment("Persistence capture cache.").define("EnabledCapturePersistence", true);
    private static final ForgeConfigSpec.IntValue CAPTURE_TICK = BUILDER.comment("How many ticks is the interval for progress calculation.").defineInRange("CaptureTick", 20, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.BooleanValue ENABLED_JOB = BUILDER.comment("Enabled job module.").define("EnabledJob", true);
    private static final ForgeConfigSpec.BooleanValue ENABLED_JOB_PERSISTENCE = BUILDER.comment("Persistence job cache.").define("EnabledJobPersistence", true);

    private static final ForgeConfigSpec.BooleanValue ENABLED_RAID = BUILDER.comment("Enabled raid module.").define("EnabledRaid", true);
    private static final ForgeConfigSpec.BooleanValue ENABLED_RAID_PERSISTENCE = BUILDER.comment("Persistence raid cache.").define("EnabledRaidPersistence", true);

    private static final ForgeConfigSpec.BooleanValue ENABLED_CHEST_MENU_POST_EVENT_BUS = BUILDER.comment("Enabled chest menu post event bus.").define("EnabledChestMenuPostEventBus", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean isEnabledCapture;
    public static boolean isEnabledCaptureCachePersistence;
    public static int captureTick;

    public static boolean isEnabledJob;
    public static boolean isEnabledJobCachePersistence;

    public static boolean isEnabledRaid;
    public static boolean isEnabledRaidCachePersistence;

    public static boolean isEnabledChestMenuPostEventBus;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        isEnabledCapture = ENABLED_CAPTURE.get();
        isEnabledCaptureCachePersistence = ENABLED_CAPTURE_PERSISTENCE.get();
        captureTick = CAPTURE_TICK.get();

        isEnabledJob = ENABLED_JOB.get();
        isEnabledJobCachePersistence = ENABLED_JOB_PERSISTENCE.get();

        isEnabledRaid = ENABLED_RAID.get();
        isEnabledRaidCachePersistence = ENABLED_RAID_PERSISTENCE.get();

        isEnabledChestMenuPostEventBus = ENABLED_CHEST_MENU_POST_EVENT_BUS.get();
    }
}
