package org.thexeler.freeepicgames.command;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.command.lamp.annotations.*;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.freeepicgames.database.type.RaidTreasureType;
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
    public void templateCreate(ForgeCommandActor sender, String name, @WithTargetLocation int x1, @WithTargetLocationYZ int y1, @WithTargetLocationZ int z1, @WithTargetLocation int x2, @WithTargetLocationYZ int y2, @WithTargetLocationZ int z2) {
        if (RaidType.getType(name) == null) {
            JsonObject object = new JsonObject();

            object.addProperty("chunk_size_x", Math.abs(x2 - x1));
            object.addProperty("chunk_size_z", Math.abs(z2 - z1));

            RaidType.register(name, object);

            BlockPos start = new BlockPos(x1, y1, z1);
            BlockPos end = new BlockPos(x2, y2, z2);
            RaidType tempType = RaidType.getType(name);
            if (tempType != null) {
                tempType.updateConstruct(new ChunkPos(start), new ChunkPos(end));
                sender.reply("创建成功:已创建模板" + name);
            } else {
                sender.reply("创建失败:模板" + name + "注册失败");
            }
        } else {
            sender.reply("创建失败:已存在的模板" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("template delete <name>")
    public void templateDelete(ForgeCommandActor sender, @WithRaidType String name) {
        if (RaidType.unregister(name)) {
            sender.reply("删除成功:已删除模板" + name);
        } else {
            sender.reply("删除失败:不存在的模板" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("template update <name> <x> <y> <z>")
    public void templateInfo(ForgeCommandActor sender, @WithRaidType String name, @WithTargetLocation int x, @WithTargetLocationYZ int y, @WithTargetLocationZ int z) {
        RaidType raidType = RaidType.getType(name);
        if (raidType != null) {
            BlockPos pos = new BlockPos(x, y, z);
            RaidInstanceView view = RaidInstanceView.getRaidInstanceFromChunk(new ChunkPos(pos));
            if (view != null) {
                raidType.updateConstruct(view);
                sender.reply("更新成功:已更新模板" + name);
            } else {
                sender.reply("更新失败:不处于副本中");
            }
        } else {
            sender.reply("更新失败:不存在的模板" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("template list")
    public void templateList(ForgeCommandActor sender) {
        List<String> templates = RaidType.getAllTypeName();
        StringBuilder message = new StringBuilder("———————共计" + templates.size() + "个副本模板———————\n");
        templates.forEach(value -> message.append(value).append("\n"));
        message.append("——————————————————————");
        sender.reply(message.toString());
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("instance create <type>")
    public static void instanceCreate(ForgeCommandActor sender, @WithRaidType String type) {
        RaidType raidType = RaidType.getType(type);
        if (raidType != null) {
            RaidInstanceView view = raidType.create();
            view.build();
            sender.reply("创建成功:已创建副本" + view.getId());
        } else {
            sender.reply("创建失败:不存在的模板" + type);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("instance delete <id>")
    public static void instanceDelete(ForgeCommandActor sender, String id) {
        GlobalRaidDataAgent agent = GlobalRaidDataAgent.getInstance();
        RaidInstanceView view = agent.getRaidInstance(id);
        if (view != null) {
            view.destroy();
            sender.reply("删除成功:已删除副本" + id);
        } else {
            sender.reply("删除失败:不存在的副本" + id);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("reload")
    public static void reload(ForgeCommandActor sender) {
        sender.reply("正在重载配置文件...");
        RaidType.expire();
        RaidTreasureType.expire();
        RaidTreasureType.init();
        RaidType.init();
        sender.reply("配置文件重载完成");
    }
}
