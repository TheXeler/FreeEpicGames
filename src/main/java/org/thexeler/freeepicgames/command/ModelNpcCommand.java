package org.thexeler.freeepicgames.command;

import net.minecraft.world.entity.Entity;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.lamp.annotations.RequiresOP;
import org.thexeler.lamp.annotations.WithNpc;
import org.thexeler.lamp.annotations.WithNpcType;
import org.thexeler.lamp.parameters.EntitySelectorList;
import org.thexeler.freeepicgames.database.agent.WorldNpcDataAgent;
import org.thexeler.freeepicgames.database.type.NpcType;
import org.thexeler.freeepicgames.database.view.NpcView;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;

@SuppressWarnings("unused")
@Command("fegnpc")
public class ModelNpcCommand {
    @CommandPlaceholder
    @RequiresOP
    @Subcommand("create <type>")
    public void create(ForgeCommandActor sender, @WithNpcType String type) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        if (agent.createNpc(NpcType.getType(type)) != null) {
            sender.reply("成功创建NPC");
        } else {
            sender.reply("创建失败");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("create <type> <selector>")
    public void create(ForgeCommandActor sender, @WithNpcType String type, EntitySelectorList<Entity> selector) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        selector.forEach(entity -> {
            if (agent.createNpc(NpcType.getType(type), entity) != null) {
                sender.reply("成功附加NPC功能到已有生物");
            } else {
                sender.reply("附加失败");
            }
        });
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("delete <id>")
    public void delete(ForgeCommandActor sender, String id) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        NpcView npc = agent.getNpcView(id);
        if (npc != null) {
            npc.discard();
            sender.reply("成功删除NPC");
        } else {
            sender.reply("删除失败");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("delete <selector>")
    public void delete(ForgeCommandActor sender, EntitySelectorList<Entity> selector) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        selector.forEach(entity -> {
            NpcView npc = agent.getNpcView(entity.getStringUUID());
            if (npc != null) {
                npc.discard();
                sender.reply("成功删除NPC");
            } else {
                sender.reply("删除失败");
            }
        });
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("discard <id>")
    public void discard(ForgeCommandActor sender, @WithNpc String id) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        NpcView npc = agent.getNpcView(id);
        if (npc != null) {
            npc.discardAdditional();
            sender.reply("成功删除NPC附加数据");
        } else {
            sender.reply("删除失败");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("discard <selector>")
    public void discard(ForgeCommandActor sender, EntitySelectorList<Entity> selector) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        selector.forEach(entity -> {
            NpcView npc = agent.getNpcView(entity.getStringUUID());
            if (npc != null) {
                npc.discardAdditional();
                sender.reply("成功删除NPC附加数据");
            } else {
                sender.reply("删除失败");
            }
        });
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("list")
    public void list(ForgeCommandActor sender) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        sender.reply("当前世界NPC列表:");
        agent.getAllNpc().forEach(npc -> sender.reply(npc.getId()));
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("info <id>")
    public void info(ForgeCommandActor sender, String id) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        NpcView npc = agent.getNpcView(id);
        if (npc != null) {
            sender.reply("NPC信息:");
            sender.reply("实体: " + npc.getNpcType().getEntityType().toString());
            sender.reply("位置: " + npc.getOriginEntity().getX() + ", " + npc.getOriginEntity().getY() + ", " + npc.getOriginEntity().getZ());
            sender.reply("ID: " + npc.getId());
            sender.reply("类型: " + npc.getNpcType().getName());
            sender.reply("附加数据: ");
            npc.getNpcData().forEach((name, value) ->
                    sender.reply("- " + name + ": " + value));
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("config <id>")
    public void config(ForgeCommandActor sender, String id) {
        WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(sender.getLevel());
        NpcView npc = agent.getNpcView(id);
        if (npc != null) {
            // TODO
            FreeEpicGames.LOGGER.warn("没做完喵，诶嘿~");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("config <selector>")
    public void config(ForgeCommandActor sender, EntitySelectorList<Entity> selector) {
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("reload")
    public void reload(ForgeCommandActor sender) {
        sender.reply("正在重载配置文件...");
        NpcType.expire();
        NpcType.init();
        sender.reply("配置文件重载完成");
    }
}
