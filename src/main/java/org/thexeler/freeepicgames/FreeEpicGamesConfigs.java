package org.thexeler.freeepicgames;

import org.thexeler.tomls.TomlConfigSpec;
import org.thexeler.tomls.exception.TomlException;
import org.thexeler.tomls.value.BooleanValue;
import org.thexeler.tomls.value.IntegerValue;

import java.io.IOException;

public class FreeEpicGamesConfigs {
    private static final TomlConfigSpec.Builder BUILDER = new TomlConfigSpec.Builder(FreeEpicGames.MOD_ID);

    private static final BooleanValue ENABLED_CAPTURE = BUILDER.comment("Enabled capture module.").define("EnabledCapture", false);
    private static final BooleanValue ENABLED_CAPTURE_PERSISTENCE = BUILDER.comment("Persistence capture cache.").define("EnabledCapturePersistence", true);
    private static final IntegerValue CAPTURE_TICK = BUILDER.comment("How many ticks is the interval for progress calculation.").defineInRange("CaptureTick", 20, 0, Integer.MAX_VALUE);

    private static final BooleanValue ENABLED_CLASSES = BUILDER.comment("Enabled classes module.").define("EnabledClasses", false);
    private static final BooleanValue ENABLED_CLASSES_PERSISTENCE = BUILDER.comment("Persistence classes cache.").define("EnabledClassesPersistence", true);

    private static final BooleanValue ENABLED_RAID = BUILDER.comment("Enabled raid module.").define("EnabledRaid", false);
    private static final BooleanValue ENABLED_RAID_PERSISTENCE = BUILDER.comment("Persistence raid cache.").define("EnabledRaidPersistence", true);

    private static final BooleanValue ENABLED_CHEST_MENU_POST_EVENT_BUS = BUILDER.comment("Enabled chest menu post event bus.").define("EnabledChestMenuPostEventBus", false);

    private static final TomlConfigSpec SPEC = BUILDER.build();

    public static boolean isEnabledCapture;
    public static boolean isEnabledCaptureCachePersistence;
    public static int captureTick;

    public static boolean isEnabledClasses;
    public static boolean isEnabledClassesCachePersistence;

    public static boolean isEnabledRaid;
    public static boolean isEnabledRaidCachePersistence;

    public static boolean isEnabledChestMenuPostEventBus;

    static void load() {
        try {
            SPEC.load();

            isEnabledCapture = ENABLED_CAPTURE.getValue();
            isEnabledCaptureCachePersistence = ENABLED_CAPTURE_PERSISTENCE.getValue();
            captureTick = CAPTURE_TICK.getValue();

            isEnabledClasses = ENABLED_CLASSES.getValue();
            isEnabledClassesCachePersistence = ENABLED_CLASSES_PERSISTENCE.getValue();

            isEnabledRaid = ENABLED_RAID.getValue();
            isEnabledRaidCachePersistence = ENABLED_RAID_PERSISTENCE.getValue();

            isEnabledChestMenuPostEventBus = ENABLED_CHEST_MENU_POST_EVENT_BUS.getValue();

            SPEC.save();
        } catch (TomlException | IOException e) {
            FreeEpicGames.LOGGER.error(e.getMessage());
        }
    }
}
