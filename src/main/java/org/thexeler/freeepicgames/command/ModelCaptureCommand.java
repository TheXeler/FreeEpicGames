package org.thexeler.freeepicgames.command;

import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.thexeler.freeepicgames.storage.agent.CaptureWorldDataAgent;
import org.thexeler.freeepicgames.storage.utils.LogicTeam;
import org.thexeler.freeepicgames.storage.view.AreaView;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.lamp.annotations.*;
import org.thexeler.lamp.parameters.EntitySelectorList;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
@Command("fegcapture")
public class ModelCaptureCommand {

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area create <area> <x1> <y1> <z1> <x2> <y2> <z2>")
    public void areaCreate(ForgeCommandActor sender, String area, @WithTargetLocation double x1, @WithTargetLocationYZ double y1, @WithTargetLocationZ double z1, @WithTargetLocation double x2, @WithTargetLocationYZ double y2, @WithTargetLocationZ double z2) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        if (agent.createArea(area, x1, y1, z1, x2, y2, z2)) {
            sender.reply("成功创建区域" + area);
        } else {
            sender.reply("创建失败:已存在的区域:" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area delete <area>")
    public void areaDelete(ForgeCommandActor sender, @WithAreas String area) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        if (agent.deleteArea(area)) {
            sender.reply("成功删除区域" + area);
        } else {
            sender.reply("删除失败:不存在的区域:" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area set <area> LockWhenAttackerCapture <areas>")
    public void areaSetLockWhenAttackerCapture(ForgeCommandActor sender, @WithAreas String area, @WithAreas String[] areas) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setLockWhenAttackerCapture(Arrays.asList(areas));
            sender.reply("设置成功");
        } else {
            sender.reply("设置失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area set <area> UnlockWhenAttackerCapture <areas>")
    public void areaSetUnlockWhenAttackerCapture(ForgeCommandActor sender, @WithAreas String area, @WithAreas String[] areas) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setUnlockWhenAttackerCapture(Arrays.asList(areas));
            sender.reply("设置成功");
        } else {
            sender.reply("设置失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area set <area> LockWhenDefenderCapture <areas>")
    public void areaSetLockWhenDefenderCapture(ForgeCommandActor sender, @WithAreas String area, @WithAreas String[] areas) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setLockWhenDefenderCapture(Arrays.asList(areas));
            sender.reply("设置成功");
        } else {
            sender.reply("设置失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area set <area> UnlockWhenDefenderCapture <areas>")
    public void areaSetUnlockWhenDefenderCapture(ForgeCommandActor sender, @WithAreas String area, @WithAreas String[] areas) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setUnlockWhenDefenderCapture(Arrays.asList(areas));
            sender.reply("设置成功");
        } else {
            sender.reply("设置失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area set <area> ExportSchedule <bool>")
    public void areaSetExportSchedule(ForgeCommandActor sender, @WithAreas String area, boolean bool) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setExportSchedule(bool);
            sender.reply("设置成功");
        } else {
            sender.reply("设置失败:不存在的区域:" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area set <area> ScheduleFormatExport <bool>")
    public void areaSetScheduleFormatExport(ForgeCommandActor sender, @WithAreas String area, boolean bool) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setExportScheduleFormat(bool);
            sender.reply("设置成功");
        } else {
            sender.reply("设置失败:不存在的区域:" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area list")
    public void areaList(ForgeCommandActor sender) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        Collection<AreaView> areaList = agent.getAllAreas();
        StringBuilder message = new StringBuilder("——————————共计" + areaList.size() + "个区域——————————\n");
        areaList.forEach(value -> {
            ServerScoreboard board = sender.getLevel().getScoreboard();
            double schedule = value.getSchedule();
            LogicTeam controllerTeam = value.getController();
            String controller = "中立";
            if (controllerTeam != LogicTeam.NEUTRAL) {
                controller = Objects.requireNonNull(board.getPlayerTeam(
                                controllerTeam == LogicTeam.ATTACKER ? agent.getAttacker() : agent.getDefender()))
                        .getDisplayName().getString();
            }
            message.append("名称:").
                    append(value.getName()).append("  占领进度:").
                    append(Math.abs(schedule)).
                    append("%  控制者:").
                    append(controller).
                    append("  是否已锁定:").
                    append(value.isLocked()).append("\n");
        });
        message.append("——————————————————————————");
        sender.reply(message.toString());
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area lock <area>")
    public void areaLock(ForgeCommandActor sender, @WithAreas String area) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setLocked(true);
            sender.reply("区域" + area + "已锁定");
        } else {
            sender.reply("操作失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area unlock <area>")
    public void areaUnlock(ForgeCommandActor sender, @WithAreas String area) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            view.setLocked(false);
            sender.reply("区域" + area + "已解锁");
        } else {
            sender.reply("操作失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area teleport <area>")
    public void areaTeleport(ForgeCommandActor sender, @WithAreas String area) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        ServerPlayer player = sender.requirePlayer();
        if (player != null) {
            if (view != null) {
                Random random = new Random();
                double maxX = view.getEnd().x - view.getPos().x;
                double maxZ = view.getEnd().z - view.getPos().z;
                player.teleportTo(view.getPos().x + (random.nextDouble() % maxX), view.getPos().y + 1, view.getPos().z + (random.nextDouble() % maxZ));
                sender.reply("传送成功");
            } else {
                sender.reply("传送失败:不存在的区域" + area);
            }
        } else {
            sender.reply("传送失败:该指令仅限玩家使用");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area teleport <area> <player>")
    public void areaTeleport(ForgeCommandActor sender, @WithAreas String area, ServerPlayer player) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            Random random = new Random();
            double maxX = view.getEnd().x - view.getPos().x;
            double maxZ = view.getEnd().z - view.getPos().z;
            player.setPos(new Vec3(view.getPos().x + (random.nextDouble() % maxX), view.getPos().y + 1, view.getPos().z + (random.nextDouble() % maxZ)));
            sender.reply("传送成功");
        } else {
            sender.reply("传送失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("area teleport <area> <selector>")
    public void areaTeleport(ForgeCommandActor sender, @WithAreas String area, EntitySelectorList<ServerPlayer> selector) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        AreaView view = agent.getAreaView(area);
        if (view != null) {
            Random random = new Random();
            selector.stream().filter(entity -> entity instanceof ServerPlayer).forEach(player -> {
                double maxX = view.getEnd().x - view.getPos().x;
                double maxZ = view.getEnd().z - view.getPos().z;
                player.setPos(view.getPos().x + (random.nextDouble() % maxX), view.getPos().y + 1, view.getPos().z + (random.nextDouble() % maxZ));
            });
            sender.reply("传送成功");
        } else {
            sender.reply("传送失败:不存在的区域" + area);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("set Rate <rate>")
    public void setRate(ForgeCommandActor sender, float rate) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        agent.setRate(rate);
        sender.reply("设置成功");
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("set OutputObjectiveName <object>")
    public void setOutputObjectiveName(ForgeCommandActor sender, String object) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        agent.setExportObjectiveName(object);
        sender.reply("设置成功");
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("set team Attacker <team>")
    public void setTeamAttacker(ForgeCommandActor sender, @WithTeam String team) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        if (sender.getLevel().getScoreboard().getPlayerTeam(team) != null) {
            agent.setAttacker(team);
            sender.reply("成功设置进攻方为队伍" + team);
        } else {
            sender.reply("设置失败:不存在的队伍名" + team);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("set team Defender <team>")
    public void setTeamDefender(ForgeCommandActor sender, @WithTeam String team) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        if (sender.getLevel().getScoreboard().getTeamNames().contains(team)) {
            agent.setDefender(team);
            sender.reply("成功设置防守方为队伍" + team);
        } else {
            sender.reply("设置失败:不存在的队伍名" + team);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("set team AttackerCommander <player>")
    public void setTeamAttackerCommander(ForgeCommandActor sender, ServerPlayer player) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        agent.setAttackerCommander(player.getStringUUID());
        sender.reply("成功设置进攻方指挥官为" + player.getDisplayName().getString());
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("set team DefenderCommander <player>")
    public void setTeamDefenderCommander(ForgeCommandActor sender, ServerPlayer player) {
        CaptureWorldDataAgent agent = CaptureWorldDataAgent.getInstance(sender.getLevel());
        agent.setDefenderCommander(player.getStringUUID());
        sender.reply("成功设置防守方指挥官为 {}" + player.getDisplayName().getString());
    }
}
