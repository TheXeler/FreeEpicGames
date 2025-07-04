package org.thexeler.freeepicgames;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.thexeler.freeepicgames.database.untils.ModSavedData;

@Mod(FreeEpicGames.MOD_ID)
public class FreeEpicGames {
    public static final String MOD_ID = "freeepicgames";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ModSavedData SAVED_DATA = null;
    public static ServerLevel RAID_WORLD = null;
    public static ServerLevel OVER_WORLD = null;

    public FreeEpicGames(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);
        modContainer.registerConfig(ModConfig.Type.SERVER, FreeEpicGamesConfigs.SPEC);
    }
}
