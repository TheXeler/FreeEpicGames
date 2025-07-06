package org.thexeler.freeepicgames;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.thexeler.freeepicgames.database.untils.ModSavedData;

@Mod(FreeEpicGames.MOD_ID)
public class FreeEpicGames {

    public static final String MOD_ID = "freeepicgames";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ModSavedData SAVED_DATA = null;
    public static ServerLevel RAID_WORLD = null;
    public static ServerLevel OVER_WORLD = null;

    public FreeEpicGames() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FreeEpicGamesConfigs.SPEC);
    }
}
