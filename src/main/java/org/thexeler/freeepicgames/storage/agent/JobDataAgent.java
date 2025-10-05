package org.thexeler.freeepicgames.storage.agent;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.storage.type.JobType;
import org.thexeler.freeepicgames.storage.utils.DataUtils;
import org.thexeler.freeepicgames.storage.utils.ModSavedData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JobDataAgent extends AbstractDataAgent {
    private static JobDataAgent instance;

    private final JsonObject playerData;

    private final Map<String, String> playerJobMap = Collections.synchronizedMap(new HashMap<>());

    private JobDataAgent() {
        if (FreeEpicGamesConfigs.isEnabledClassesCachePersistence) {
            playerData = ModSavedData.getGlobalData("JobsCache");
        } else {
            playerData = new JsonObject();
        }

        load();
    }

    public static JobDataAgent getInstance() {
        if (instance == null) {
            instance = new JobDataAgent();
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
        if (FreeEpicGamesConfigs.isEnabledClassesCachePersistence) {
            playerData.asMap().forEach((key, value) ->
                    playerJobMap.put(key, value.getAsString()));
        }
    }

    @Override
    public void save() {
        if (FreeEpicGamesConfigs.isEnabledClassesCachePersistence) {
            DataUtils.clearDeprecatedKey(playerData, playerJobMap);
            playerJobMap.forEach(playerData::addProperty);
        }
    }

    public static void expire() {
        instance = null;
    }
}
