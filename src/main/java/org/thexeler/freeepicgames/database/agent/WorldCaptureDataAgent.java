package org.thexeler.freeepicgames.database.agent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.database.untils.DataUtils;
import org.thexeler.freeepicgames.database.untils.ModSavedData;
import org.thexeler.freeepicgames.database.view.AreaView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldCaptureDataAgent implements AbstractDataAgent {
    private static final Map<ServerLevel, WorldCaptureDataAgent> instances = new HashMap<>();
    private final JsonObject optionData;
    private final JsonObject areasData;
    private final JsonObject areasCache;

    @Getter
    private final ServerLevel world;
    @Getter
    @Setter
    private String attacker, attackerCommander, defender, defenderCommander;
    @Getter
    @Setter
    private float rate;
    @Getter
    @Setter
    private String exportObjectiveName;
    private final Map<String, AreaView> areaViewMap = Collections.synchronizedMap(new HashMap<>());

    private WorldCaptureDataAgent(ServerLevel world) {
        this.world = world;

        optionData = ModSavedData.getWorldData(world, "CaptureSettings");
        areasData = ModSavedData.getWorldData(world, "AreasData");
        if (FreeEpicGamesConfigs.isEnabledCaptureCachePersistence) {
            areasCache = ModSavedData.getWorldData(world, "AreasCache");
        } else {
            areasCache = new JsonObject();
        }

        load();
    }

    public static WorldCaptureDataAgent getInstance(ServerLevel world) {
        return instances.computeIfAbsent(world, WorldCaptureDataAgent::new);
    }

    public Collection<AreaView> getAllAreas() {
        return areaViewMap.values();
    }

    public boolean createArea(String name, double pos1X, double pos1Y, double pos1Z, double pos2X, double pos2Y, double pos2Z) {
        if (areasData.get(name) == null) {
            JsonObject areaCacheInfo = new JsonObject();

            areaCacheInfo.addProperty("locked", false);
            areaCacheInfo.addProperty("schedule", 0.0);
            areaCacheInfo.addProperty("controller", "Neutral");
            areaCacheInfo.addProperty("exportSchedule", false);
            areaCacheInfo.addProperty("exportScheduleFormat", false);

            return createArea(name, pos1X, pos1Y, pos1Z, pos2X, pos2Y, pos2Z, areaCacheInfo);
        } else {
            return false;
        }
    }

    public boolean createArea(String name, double pos1X, double pos1Y, double pos1Z, double pos2X, double pos2Y, double pos2Z, JsonObject cacheInfo) {
        if (areasData.get(name) == null) {
            JsonObject areaInfo = new JsonObject();

            areaInfo.addProperty("posX", Math.min(pos1X, pos2X));
            areaInfo.addProperty("posY", Math.min(pos1Y, pos2Y) - 1.0);
            areaInfo.addProperty("posZ", Math.min(pos1Z, pos2Z));
            areaInfo.addProperty("endX", Math.max(pos1X, pos2X) + 1.0);
            areaInfo.addProperty("endY", Math.max(pos1Y, pos2Y) + 1.0);
            areaInfo.addProperty("endZ", Math.max(pos1Z, pos2Z) + 1.0);
            areaInfo.addProperty("objectName", "");
            areaInfo.add("lockWhenAttackerCapture", new JsonArray());
            areaInfo.add("unlockWhenAttackerCapture", new JsonArray());
            areaInfo.add("lockWhenDefenderCapture", new JsonArray());
            areaInfo.add("unlockWhenDefenderCapture", new JsonArray());

            AreaView view = new AreaView(name, areaInfo, this);
            view.loadStatus(cacheInfo);
            areaViewMap.put(name, view);

            return true;
        } else {
            return false;
        }
    }

    public boolean deleteArea(String name) {
        if (areaViewMap.get(name) != null) {
            areaViewMap.remove(name);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public AreaView getAreaView(String name) {
        return areaViewMap.get(name);
    }

    @Override
    public void load() {
        attacker = DataUtils.getValue(optionData, "attacker", "");
        defender = DataUtils.getValue(optionData, "defender", "");
        rate = DataUtils.getValue(optionData, "capture_rate", 1.0F);
        attackerCommander = DataUtils.getValue(optionData, "attacker_commander", "");
        defenderCommander = DataUtils.getValue(optionData, "defender_commander", "");

        areasData.keySet().forEach(name -> {
            AreaView view = new AreaView(name, areasData.getAsJsonObject(name), this);
            areaViewMap.put(name, view);
            if (FreeEpicGamesConfigs.isEnabledCaptureCachePersistence) {
                if (areasCache != null && areasCache.get(name) != null) {
                    view.loadStatus(areasCache.getAsJsonObject(name));
                }
            }

        });
    }

    @Override
    public void save() {
        optionData.addProperty("Attacker", attacker);
        optionData.addProperty("Defender", defender);
        optionData.addProperty("CaptureRate", rate);
        optionData.addProperty("AttackerCommander", attackerCommander);
        optionData.addProperty("DefenderCommander", defenderCommander);
        optionData.addProperty("exportObjectiveName", exportObjectiveName);

        DataUtils.computeViewMap(areaViewMap, areasData);
        DataUtils.computeCacheViewMap(areaViewMap, areasCache);
    }
}