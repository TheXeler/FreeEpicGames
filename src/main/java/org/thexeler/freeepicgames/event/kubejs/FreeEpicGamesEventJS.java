package org.thexeler.freeepicgames.event.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import org.thexeler.freeepicgames.storage.type.NpcType;

public interface FreeEpicGamesEventJS {
    EventGroup MENU_EVENT = EventGroup.of("MenuEvent");
    EventGroup NPC_EVENT = EventGroup.of("NpcEvent");
    EventGroup RAID_EVENT = EventGroup.of("RaidEvent");

    Extra NPC_TYPE_EXTRA = new Extra().transformer(o -> {
        if (o == null) {
            return null;
        } else if (o instanceof NpcType npcType) {
            return npcType;
        } else if (o instanceof String name) {
            return name.isBlank() ? null : NpcType.getType(name);
        }
        return null;
    }).describeType(cxt -> cxt.javaType(NpcType.class));

    // MenuEvent handlers
    EventHandler MENU_CREATE_EVENT = MENU_EVENT.server("create", () -> MenuEventJS.CreateEvent.class);
    EventHandler MENU_OPENED_EVENT = MENU_EVENT.server("opened", () -> MenuEventJS.OpenedEvent.class);
    EventHandler MENU_CLOSE_EVENT = MENU_EVENT.server("close", () -> MenuEventJS.CloseEvent.class);
    EventHandler MENU_CLICK_EVENT = MENU_EVENT.server("click", () -> MenuEventJS.ClickEvent.class).hasResult();

    // NpcEvent handlers
    EventHandler NPC_CREATE_EVENT = NPC_EVENT.server("create", () -> NpcEventJS.Create.class).extra(NPC_TYPE_EXTRA);
    EventHandler NPC_JOIN_EVENT = NPC_EVENT.server("join", () -> NpcEventJS.Join.class).extra(NPC_TYPE_EXTRA);
    EventHandler NPC_TICK_EVENT = NPC_EVENT.server("tick", () -> NpcEventJS.Tick.class).extra(NPC_TYPE_EXTRA);
    EventHandler NPC_ATTACK_EVENT = NPC_EVENT.server("attack", () -> NpcEventJS.Attack.class).extra(NPC_TYPE_EXTRA);
    EventHandler NPC_INTERACT_EVENT = NPC_EVENT.server("interact", () -> NpcEventJS.Interact.class).extra(NPC_TYPE_EXTRA);
    EventHandler NPC_DEATH_EVENT = NPC_EVENT.server("death", () -> NpcEventJS.Death.class).extra(NPC_TYPE_EXTRA).hasResult();
    EventHandler NPC_DAMAGE_EVENT = NPC_EVENT.server("damage", () -> NpcEventJS.Damage.class).extra(NPC_TYPE_EXTRA).hasResult();
    EventHandler NPC_KILLED_EVENT = NPC_EVENT.server("killed", () -> NpcEventJS.Killed.class).extra(NPC_TYPE_EXTRA).hasResult();

    // RaidEvent handlers
    EventHandler RAID_BUILD_EVENT = RAID_EVENT.server("build", () -> RaidEventJS.BuildEvent.class);
    EventHandler RAID_DESTROY_EVENT = RAID_EVENT.server("destroy", () -> RaidEventJS.DestroyEvent.class).hasResult();
    EventHandler RAID_OPEN_TREASURE_EVENT = RAID_EVENT.server("openTreasure", () -> RaidEventJS.OpenTreasureEvent.class).hasResult();
    EventHandler RAID_TICK_EVENT = RAID_EVENT.server("tick", () -> RaidEventJS.TickEvent.class);
}
