package org.thexeler.freeepicgames.command;

import com.google.gson.JsonObject;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.command.lamp.annotations.RequiresOP;
import org.thexeler.freeepicgames.command.lamp.annotations.WithJobType;
import org.thexeler.freeepicgames.command.lamp.parameters.EntitySelectorList;
import org.thexeler.freeepicgames.database.agent.GlobalJobDataAgent;
import org.thexeler.freeepicgames.database.type.JobType;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;

import java.util.List;

@SuppressWarnings("unused")
@Command("fegjob")
public class ModelJobCommand {

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("job create <name>")
    public void jobCreate(ForgeCommandActor actor, String name) {
        if (JobType.register(name, new JsonObject())) {
            actor.reply("成功创建职业" + name);
        } else {
            actor.reply("创建失败:已存在的职业" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("job delete <name>")
    public void jobDelete(ForgeCommandActor actor, @WithJobType String name) {
        if (JobType.unregister(name)) {
            actor.reply("成功删除职业" + name);
        } else {
            actor.reply("删除失败:不存在的职业" + name);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("job remove <name>")
    public void jobRemove(ForgeCommandActor sender, @WithJobType String name) {
        ServerPlayer senderPlayer = sender.requirePlayer();
        if (senderPlayer != null) {
            JobType type = JobType.getType(name);
            if (type != null) {
                type.removeItem(senderPlayer.getMainHandItem());
                sender.reply("已清空指定物品");
            } else {
                sender.error("设置失败:不存在的职业");
            }
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("job set <name>")
    public void jobSet(ForgeCommandActor sender, @WithJobType String name) {
        ServerPlayer senderPlayer = sender.requirePlayer();
        if (senderPlayer != null) {
            ItemStack stack = senderPlayer.getMainHandItem();
            if (!stack.is(Items.AIR)) {
                JobType type = JobType.getType(name);
                if (type != null) {
                    type.setItem(stack);
                    sender.reply("已将职业" + name + "中的物品" +
                            stack.getDisplayName() + "设置为" +
                            stack.getCount() + "个");
                } else {
                    sender.reply("设置失败:不存在的职业或物品类型");
                }
            } else {
                sender.reply("设置失败:目标物品为空");
            }
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("job set <name> <num>")
    public void jobSet(ForgeCommandActor sender, @WithJobType String name, int num) {
        ServerPlayer senderPlayer = sender.requirePlayer();
        if (senderPlayer != null) {
            ItemStack stack = senderPlayer.getMainHandItem();
            if (!stack.is(Items.AIR)) {
                JobType type = JobType.getType(name);
                if (type != null) {
                    type.setItem(stack, num);
                    sender.reply("已将职业" + name + "中的物品" +
                            stack.getDisplayName() + "设置为" +
                            stack.getCount() + "个");
                } else {
                    sender.error("设置失败:不存在的职业");
                }
            } else {
                sender.reply("设置失败:目标物品为空");
            }
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("job list")
    public void listJob(ForgeCommandActor sender) {
        List<String> jobs = JobType.getAllTypeName();
        StringBuilder message = new StringBuilder("————————共计" + jobs.size() + "个职业————————\n");
        jobs.forEach(value -> message.append(value).append("\n"));
        message.append("——————————————————————");
        sender.reply(message.toString());
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("player get <player>")
    public void playerGet(ForgeCommandActor sender, ServerPlayer player) {
        GlobalJobDataAgent agent = GlobalJobDataAgent.getInstance();
        sender.reply("玩家" + player.getDisplayName() + "的职业为:" + agent.getPlayerJob(player));
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("player set <player> <name>")
    public void playerSet(ForgeCommandActor sender, ServerPlayer player, @WithJobType String name) {
        GlobalJobDataAgent agent = GlobalJobDataAgent.getInstance();
        if (agent.setPlayerJob(player, name)) {
            sender.reply("玩家" + player.getDisplayName().getString() + "已被设置为" + name);
        } else {
            sender.error("设置失败:不存在的职业");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("player set <selector> <name>")
    public void playerSet(ForgeCommandActor sender, EntitySelectorList<ServerPlayer> selector, @WithJobType String name) {
        GlobalJobDataAgent agent = GlobalJobDataAgent.getInstance();
        selector.stream().filter(entity -> entity instanceof ServerPlayer).forEach(player -> {
                    if (agent.setPlayerJob(player, name)) {
                        sender.reply("玩家" + player.getDisplayName().getString() + "已被设置为" + name);
                    } else {
                        sender.error("设置失败:不存在的职业");
                    }
                }
        );
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("player refresh <player>")
    public void playerRefresh(ForgeCommandActor sender, ServerPlayer player) {
        GlobalJobDataAgent agent = GlobalJobDataAgent.getInstance();
        JobType type = JobType.getType(agent.getPlayerJob(player));
        player.getInventory().clearContent();
        if (type != null) {
            type.getAllItems().forEach(stack -> player.getInventory().add(stack));
        }
        sender.reply("已刷新玩家" + player.getDisplayName().getString() + "的背包");
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("player refresh <selector>")
    public void playerRefresh(ForgeCommandActor sender, EntitySelectorList<ServerPlayer> selector) {
        GlobalJobDataAgent agent = GlobalJobDataAgent.getInstance();
        selector.stream().filter(entity -> entity instanceof ServerPlayer).forEach(player -> {
                    JobType type = JobType.getType(agent.getPlayerJob(player));
                    player.getInventory().clearContent();
                    if (type != null) {
                        type.getAllItems().forEach(stack -> player.getInventory().add(stack));
                    }
                    sender.reply("已刷新玩家" + player.getDisplayName().getString() + "的背包");
                }
        );
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("bind <command>")
    public void bind(ForgeCommandActor sender, String command) {
        ServerPlayer player = sender.requirePlayer();
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.is(Items.AIR)) {
                CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                CompoundTag tag;
                if (data == null) data = CustomData.of(new CompoundTag());
                tag = (CompoundTag) data.copyTag().put("custom_command", StringTag.valueOf(command));
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                sender.reply("命令绑定成功");
            } else {
                sender.reply("绑定失败:目标物品为空");
            }
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("reload")
    public void reload(ForgeCommandActor sender) {
        sender.reply("正在重载配置文件...");
        JobType.expire();
        JobType.init();
        sender.reply("配置文件重载完成");
    }
}
