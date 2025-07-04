package org.thexeler.freeepicgames.database.agent;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.database.type.JobType;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.untils.ModSavedData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GlobalJobDataAgent implements AbstractDataAgent {
    private static GlobalJobDataAgent instance;

    private final JsonObject playerData;

    private final Map<String, String> playerJobMap = Collections.synchronizedMap(new HashMap<>());

    private GlobalJobDataAgent() {
        if (FreeEpicGamesConfigs.isEnabledJobCachePersistence) {
            playerData = ModSavedData.getGlobalData("JobsCache");
        } else {
            playerData = new JsonObject();
        }

        load();
    }

    public static GlobalJobDataAgent getInstance() {
        if (instance == null) {
            instance = new GlobalJobDataAgent();
        }
        return instance;
    }

    public Collection<String> getAllPlayerJob() {
        return playerJobMap.values();
    }

    public boolean setPlayerJob(ServerPlayer player, String jobName) {
        if (JobType.getType(jobName) != null) {
            playerJobMap.put(player.getStringUUID(), jobName);
            return true;
        }
        return false;
    }

    @Nullable
    public String getPlayerJob(ServerPlayer player) {
        return playerJobMap.get(player.getStringUUID());
    }

    @Override
    public void load() {
        if (FreeEpicGamesConfigs.isEnabledJobCachePersistence) {
            playerData.asMap().forEach((key, value) ->
                    playerJobMap.put(key, value.getAsString()));
        }
    }

    @Override
    public void save() {
        if (FreeEpicGamesConfigs.isEnabledJobCachePersistence) {
            DataUtils.clearDeprecatedKey(playerData, playerJobMap);
            playerJobMap.forEach(playerData::addProperty);
        }
    }
}
