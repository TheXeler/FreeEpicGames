package org.thexeler.freeepicgames.command;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.storage.agent.NpcWorldDataAgent;
import org.thexeler.freeepicgames.storage.type.NpcType;
import org.thexeler.freeepicgames.storage.view.NpcView;
import org.thexeler.lamp.actor.ForgeCommandActor;
import org.thexeler.lamp.annotations.*;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;

import java.util.UUID;

@SuppressWarnings("unused")
@Command("fegnpc")
public class ModelNpcCommand {
    @CommandPlaceholder
    @RequiresOP
    @Subcommand("create <type>")
    public void create(ForgeCommandActor sender, @WithNpcType String type) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
        NpcType npcType = NpcType.getType(type);
        if (npcType != null) {
            ServerPlayer player = sender.requirePlayer();
            if (player != null) {
                if (agent.createNpc(npcType, player.getX(), player.getY(), player.getZ()) != null) {
                    sender.reply("成功创建NPC");
                } else {
                    sender.reply("创建失败：不受支持的实体类型 " + npcType.getEntityType().toString());
                }
            } else {
                sender.reply("创建失败：命令行执行此命令时不能忽略坐标");
            }
        } else {
            sender.reply("创建失败：不存在的NPC类型 " + type);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("create <type> <x> <y> <z>")
    public void create(ForgeCommandActor sender, @WithNpcType String type, @WithTargetLocation float x, @WithTargetLocationYZ float y, @WithTargetLocationZ float z) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
        NpcType npcType = NpcType.getType(type);
        if (npcType != null) {
            if (agent.createNpc(npcType, x, y, z) != null) {
                sender.reply("成功创建NPC");
            } else {
                sender.reply("创建失败：不受支持的实体类型 " + npcType.getEntityType().toString());
            }
        } else {
            sender.reply("创建失败：不存在的NPC类型 " + type);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("create <type> <id>")
    public void create(ForgeCommandActor sender, @WithNpcType String type, @WithNpc String id) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
        NpcType npcType = NpcType.getType(type);
        if (npcType != null) {
            Entity target = sender.getLevel().getEntity(UUID.fromString(id));
            if (target != null) {
                if (agent.getNpcView(id) != null) {
                    if (agent.createNpc(npcType, target) != null) {
                        sender.reply("成功附加NPC功能到已有实体");
                    } else {
                        sender.reply("创建失败：不受支持的实体类型 " + npcType.getEntityType().toString());
                    }
                } else {
                    sender.reply("创建失败：目标实体" + id + "已为NPC");
                }
            } else {
                sender.reply("创建失败：目标实体" + id + "不存在");
            }
        } else {
            sender.reply("创建失败：不存在的NPC类型 " + type);
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("delete <id>")
    public void delete(ForgeCommandActor sender, @WithNpc String id) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
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
    @Subcommand("discard <id>")
    public void discard(ForgeCommandActor sender, @WithNpc String id) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
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
    @Subcommand("list")
    public void list(ForgeCommandActor sender) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
        sender.reply("当前世界NPC列表:");
        agent.getAllNpc().forEach(npc -> sender.reply(npc.getId().toString()));
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("info <id>")
    public void info(ForgeCommandActor sender, @WithNpc String id) {
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
        NpcView npc = agent.getNpcView(id);
        if (npc != null) {
            sender.reply("NPC信息:");
            sender.reply("名称: " + npc.getOriginEntity().getName());
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
        NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(sender.getLevel());
        NpcView npc = agent.getNpcView(id);
        if (npc != null) {
            // TODO
            FreeEpicGames.LOGGER.warn("没做完喵，诶嘿~");
        }
    }

    @CommandPlaceholder
    @RequiresOP
    @Subcommand("reload")
    public void reload(ForgeCommandActor sender) {
        sender.reply("正在重载配置文件...");
        NpcType.expire(true);
        NpcType.init();
        sender.reply("配置文件重载完成");
    }
}
