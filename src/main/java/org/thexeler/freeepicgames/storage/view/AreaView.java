package org.thexeler.freeepicgames.storage.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.thexeler.freeepicgames.storage.agent.CaptureWorldDataAgent;
import org.thexeler.freeepicgames.storage.utils.LogicTeam;

import java.util.List;

public class AreaView implements AbstractView, AbstractCacheView {
    private final CaptureWorldDataAgent rootAgent;

    @Getter
    private final String name;

    @Getter
    private final Vec3 pos, end;
    @Getter
    private final ServerBossEvent bossBar;

    @Getter
    private float schedule;
    @Getter
    private LogicTeam controller;

    @Getter
    @Setter
    private boolean locked, exportSchedule, exportScheduleFormat;

    @Setter
    private List<String> lockWhenAttackerCapture, unlockWhenAttackerCapture, lockWhenDefenderCapture, unlockWhenDefenderCapture;

    private final AABB frame;

    public AreaView(String name, JsonObject areaInfo, CaptureWorldDataAgent agent) {
        this.name = name;
        this.rootAgent = agent;

        pos = new Vec3(areaInfo.get("posX").getAsDouble(), areaInfo.get("posY").getAsDouble(), areaInfo.get("posZ").getAsDouble());
        end = new Vec3(areaInfo.get("endX").getAsDouble(), areaInfo.get("endY").getAsDouble(), areaInfo.get("endZ").getAsDouble());
        lockWhenAttackerCapture = areaInfo.get("lockWhenAttackerCapture").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        unlockWhenAttackerCapture = areaInfo.get("unlockWhenAttackerCapture").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        lockWhenDefenderCapture = areaInfo.get("lockWhenDefenderCapture").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        unlockWhenDefenderCapture = areaInfo.get("unlockWhenDefenderCapture").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();

        locked = false;
        schedule = 0.0F;
        exportSchedule = false;
        exportScheduleFormat = false;
        controller = LogicTeam.NEUTRAL;

        frame = new AABB(pos, end);
        bossBar = new ServerBossEvent(Component.literal(name),
                BossEvent.BossBarColor.WHITE,
                BossEvent.BossBarOverlay.PROGRESS);
        bossBar.setProgress(Math.abs(schedule / 2));
        bossBar.setVisible(true);
    }

    public boolean isInside(Vec3 pos) {
        return frame.contains(pos);
    }

    public void setSchedule(float num) {
        schedule = Mth.clamp(num, -100.0F, 100.0F);
        bossBar.setProgress(Mth.clamp((schedule + 100) / 200, 0.0F, 1.0F));
    }

    public void setController(LogicTeam team) {
        controller = team;
        bossBar.setColor(switch (controller) {
            case ATTACKER -> BossEvent.BossBarColor.RED;
            case DEFENDER -> BossEvent.BossBarColor.BLUE;
            default -> BossEvent.BossBarColor.WHITE;
        });

        if (team == LogicTeam.ATTACKER) {
            lockWhenAttackerCapture.forEach(name -> {
                AreaView area = rootAgent.getAreaView(name);
                if (area != null) {
                    area.setLocked(true);
                }
            });
            unlockWhenAttackerCapture.forEach(name -> {
                AreaView area = rootAgent.getAreaView(name);
                if (area != null) {
                    area.setLocked(true);
                }
            });
        } else if (team == LogicTeam.DEFENDER) {
            lockWhenDefenderCapture.forEach(name -> {
                AreaView area = rootAgent.getAreaView(name);
                if (area != null) {
                    area.setLocked(true);
                }
            });
            unlockWhenDefenderCapture.forEach(name -> {
                AreaView area = rootAgent.getAreaView(name);
                if (area != null) {
                    area.setLocked(true);
                }
            });
        }
    }


    public void loadStatus(JsonObject cacheInfo) {
        locked = cacheInfo.get("locked").getAsBoolean();
        schedule = cacheInfo.get("schedule").getAsFloat();
        controller = LogicTeam.fromString(cacheInfo.get("controller").getAsString());

        exportSchedule = cacheInfo.get("exportSchedule").getAsBoolean();
        exportScheduleFormat = cacheInfo.get("exportScheduleFormat").getAsBoolean();
    }

    @Override
    public JsonObject toCacheJson() {
        JsonObject cacheInfo = new JsonObject();

        cacheInfo.addProperty("locked", locked);
        cacheInfo.addProperty("schedule", schedule);
        cacheInfo.addProperty("controller", controller.toString());
        cacheInfo.addProperty("exportSchedule", exportSchedule);
        cacheInfo.addProperty("exportScheduleFormat", exportScheduleFormat);

        return cacheInfo;
    }

    @Override
    public JsonObject toJson() {
        JsonObject areaInfo = new JsonObject();

        areaInfo.addProperty("posX", pos.x);
        areaInfo.addProperty("posY", pos.y);
        areaInfo.addProperty("posZ", pos.z);
        areaInfo.addProperty("endX", end.x);
        areaInfo.addProperty("endY", end.y);
        areaInfo.addProperty("endZ", end.z);

        JsonArray attackerCaptureLocks = new JsonArray();
        JsonArray attackerCaptureUnlocks = new JsonArray();
        JsonArray defenderCaptureLocks = new JsonArray();
        JsonArray defenderCaptureUnlocks = new JsonArray();

        lockWhenAttackerCapture.forEach(attackerCaptureLocks::add);
        unlockWhenAttackerCapture.forEach(attackerCaptureUnlocks::add);
        lockWhenDefenderCapture.forEach(defenderCaptureLocks::add);
        unlockWhenDefenderCapture.forEach(defenderCaptureUnlocks::add);

        areaInfo.add("lockWhenAttackerCapture", attackerCaptureLocks);
        areaInfo.add("unlockWhenAttackerCapture", attackerCaptureUnlocks);
        areaInfo.add("lockWhenDefenderCapture", defenderCaptureLocks);
        areaInfo.add("unlockWhenDefenderCapture", defenderCaptureUnlocks);

        return areaInfo;
    }
}
