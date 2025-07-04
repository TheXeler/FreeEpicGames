package org.thexeler.freeepicgames.command;

import com.google.gson.JsonObject;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.command.lamp.annotations.*;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.freeepicgames.database.type.RaidType;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;

import java.util.List;

@SuppressWarnings("unused")
@Command("fegraid")
public class ModelRaidCommand {
    @CommandPlaceholder
    @RequiresOP
    @Subcommand("template create <name> <x1> <y1> <z1> <x2> <y2> <z2>")
    public void commandCreateTemplate(ForgeCommandActor sender, String name, @WithTargetLocation double x1, @WithTargetLocationYZ double y1, @WithTargetLocationZ double z1, @WithTargetLocation double x2, @WithTargetLocationYZ double y2, @WithTargetLocationZ double z2) {
        if (RaidType.getType(name) == null) {
            JsonObject object = new JsonObject();
            object.addProperty("size_x", Math.abs(x2 - x1));
            object.addProperty("size_y", Math.abs(y2 - y1));
            object.addProperty("size_z", Math.abs(z2 - z1));
            RaidType.register(name, object);
            sender.reply("创建成功:已创建模板" + name);
        } else {
            sender.reply("创建失败:已存在的模板" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("template delete <name>")
    public void commandDeleteTemplate(ForgeCommandActor sender, String name) {
        if (RaidType.unregister(name)) {
            sender.reply("删除成功:已删除模板" + name);
        } else {
            sender.reply("删除失败:不存在的模板" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("template list")
    public void commandListTemplate(ForgeCommandActor sender) {
        List<String> templates = RaidType.getAllTypeName();
        StringBuilder message = new StringBuilder("———————共计" + templates.size() + "个副本模板———————\n");
        templates.forEach(value -> message.append(value).append("\n"));
        message.append("——————————————————————");
        sender.reply(message.toString());
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("instance create <type>")
    public static void commandCreateInstance(ForgeCommandActor sender, @WithRaidType String type) {
        RaidType raidType = RaidType.getType(type);
        if (raidType != null) {
            RaidInstanceView view = raidType.create();

            sender.reply("创建成功:已创建副本" + view.getId());
        } else {
            sender.reply("创建失败:不存在的模板" + type);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("instance delete <id>")
    public static void commandDeleteInstance(ForgeCommandActor sender, String id) {
        GlobalRaidDataAgent agent = GlobalRaidDataAgent.getInstance();
        RaidInstanceView view = agent.getRaidInstance(id);
        if (view != null) {
            view.destroy();
            sender.reply("删除成功:已删除副本" + id);
        } else {
            sender.reply("删除失败:不存在的副本" + id);
        }
    }
}
