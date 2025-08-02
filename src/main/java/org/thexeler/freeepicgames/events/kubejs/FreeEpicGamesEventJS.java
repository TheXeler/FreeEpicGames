package org.thexeler.freeepicgames.events.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.thexeler.freeepicgames.storage.type.NpcType;

public interface FreeEpicGamesEventJS {
    EventGroup MENU_EVENT = EventGroup.of("MenuEvent");
    EventGroup NPC_EVENT = EventGroup.of("NpcEvent");
    EventGroup RAID_EVENT = EventGroup.of("RaidEvent");

    EventTargetType<NpcType> NPC_TYPE = EventTargetType.create(NpcType.class).transformer(o -> {
        if (o == null) {
            return null;
        } else if (o instanceof NpcType npcType) {
            return npcType;
        } else if (o instanceof String name) {
            return name.isBlank() ? null : NpcType.getType(name);
        }
        return null;
    }).describeType(TypeInfo.of(NpcType.class));

    // MenuEvent handlers
    EventHandler MENU_CREATE_EVENT = MENU_EVENT.server("Create", () -> MenuEventJS.CreateEvent.class);
    EventHandler MENU_OPENED_EVENT = MENU_EVENT.server("Opened", () -> MenuEventJS.OpenedEvent.class);
    EventHandler MENU_CLOSE_EVENT = MENU_EVENT.server("Close", () -> MenuEventJS.CloseEvent.class);
    EventHandler MENU_CLICK_EVENT = MENU_EVENT.server("Click", () -> MenuEventJS.ClickEvent.class).hasResult();

    // NpcEvent handlers
    TargetedEventHandler<NpcType> NPC_CREATE_EVENT = NPC_EVENT.server("Create", () -> NpcEventJS.Create.class).supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_JOIN_EVENT = NPC_EVENT.server("Join", () -> NpcEventJS.Join.class).supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_DEATH_EVENT = NPC_EVENT.server("Death", () -> NpcEventJS.Death.class).hasResult().supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_INTERACT_EVENT = NPC_EVENT.server("Interact", () -> NpcEventJS.Interact.class).supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_DAMAGE_EVENT = NPC_EVENT.server("Damage", () -> NpcEventJS.Damage.class).hasResult().supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_TICK_EVENT = NPC_EVENT.server("Tick", () -> NpcEventJS.Tick.class).supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_KILLED_EVENT = NPC_EVENT.server("Killed", () -> NpcEventJS.Killed.class).hasResult().supportsTarget(NPC_TYPE);
    TargetedEventHandler<NpcType> NPC_ATTACK_EVENT = NPC_EVENT.server("Attack", () -> NpcEventJS.Attack.class).supportsTarget(NPC_TYPE);

    // RaidEvent handlers
    EventHandler RAID_BUILD_EVENT = RAID_EVENT.server("Build", () -> RaidEventJS.BuildEvent.class);
    EventHandler RAID_DESTROY_EVENT = RAID_EVENT.server("Destroy", () -> RaidEventJS.DestroyEvent.class).hasResult();
    EventHandler RAID_OPEN_TREASURE_EVENT = RAID_EVENT.server("OpenTreasure", () -> RaidEventJS.OpenTreasureEvent.class).hasResult();
    EventHandler RAID_TICK_EVENT = RAID_EVENT.server("Tick", () -> RaidEventJS.TickEvent.class);
}
