package org.thexeler.freeepicgames.events.kubejs;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.thexeler.freeepicgames.events.MenuEvent;
import org.thexeler.freeepicgames.events.NpcEvent;
import org.thexeler.freeepicgames.events.RaidEvent;
import org.thexeler.freeepicgames.storage.type.NpcType;

public class CommonEventsPostJS {
    // MenuEvent handlers
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void menuCreateEvent(MenuEvent.CreateEvent event) {
        if (FreeEpicGamesEventJS.MENU_CREATE_EVENT.hasListeners()) {
            FreeEpicGamesEventJS.MENU_CREATE_EVENT.post(new MenuEventJS.CreateEvent(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void menuOpenedEvent(MenuEvent.OpenedEvent event) {
        if (FreeEpicGamesEventJS.MENU_OPENED_EVENT.hasListeners()) {
            FreeEpicGamesEventJS.MENU_OPENED_EVENT.post(new MenuEventJS.OpenedEvent(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void menuCloseEvent(MenuEvent.CloseEvent event) {
        if (FreeEpicGamesEventJS.MENU_CLOSE_EVENT.hasListeners()) {
            FreeEpicGamesEventJS.MENU_CLOSE_EVENT.post(new MenuEventJS.CloseEvent(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void menuClickEvent(MenuEvent.ClickEvent event) {
        if (FreeEpicGamesEventJS.MENU_CLICK_EVENT.hasListeners()) {
            EventResult result = FreeEpicGamesEventJS.MENU_CLICK_EVENT.post(new MenuEventJS.ClickEvent(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    // NpcEvent handlers
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcCreateEvent(NpcEvent.Create event) {
        if (FreeEpicGamesEventJS.NPC_CREATE_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_CREATE_EVENT.hasListeners(npcType)) {
                FreeEpicGamesEventJS.NPC_CREATE_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Create(event));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcJoinEvent(NpcEvent.Join event) {
        if (FreeEpicGamesEventJS.NPC_JOIN_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_JOIN_EVENT.hasListeners(npcType)) {
                FreeEpicGamesEventJS.NPC_JOIN_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Join(event));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcDeathEvent(NpcEvent.Death event) {
        if (FreeEpicGamesEventJS.NPC_DEATH_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_DEATH_EVENT.hasListeners(npcType)) {
                EventResult result = FreeEpicGamesEventJS.NPC_DEATH_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Death(event));
                if (result.interruptFalse()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcInteractEvent(NpcEvent.Interact event) {
        if (FreeEpicGamesEventJS.NPC_INTERACT_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_INTERACT_EVENT.hasListeners(npcType)) {
                FreeEpicGamesEventJS.NPC_INTERACT_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Interact(event));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcDamageEvent(NpcEvent.Damage event) {
        if (FreeEpicGamesEventJS.NPC_DAMAGE_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_DAMAGE_EVENT.hasListeners(npcType)) {
                EventResult result = FreeEpicGamesEventJS.NPC_DAMAGE_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Damage(event));
                if (result.interruptFalse()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcTickEvent(NpcEvent.Tick event) {
        if (FreeEpicGamesEventJS.NPC_TICK_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_TICK_EVENT.hasListeners(npcType)) {
                FreeEpicGamesEventJS.NPC_TICK_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Tick(event));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcKilledEvent(NpcEvent.Killed event) {
        if (FreeEpicGamesEventJS.NPC_KILLED_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_KILLED_EVENT.hasListeners(npcType)) {
                EventResult result = FreeEpicGamesEventJS.NPC_KILLED_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Killed(event));
                if (result.interruptFalse()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void npcAttackEvent(NpcEvent.Attack event) {
        if (FreeEpicGamesEventJS.NPC_ATTACK_EVENT.hasListeners()) {
            NpcType npcType = event.getView().getNpcType();
            if (FreeEpicGamesEventJS.NPC_ATTACK_EVENT.hasListeners(npcType)) {
                FreeEpicGamesEventJS.NPC_ATTACK_EVENT.post(ScriptType.SERVER, npcType, new NpcEventJS.Attack(event));
            }
        }
    }

    // RaidEvent handlers
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void raidBuildEvent(RaidEvent.BuildEvent event) {
        if (FreeEpicGamesEventJS.RAID_BUILD_EVENT.hasListeners()) {
            FreeEpicGamesEventJS.RAID_BUILD_EVENT.post(new RaidEventJS.BuildEvent(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void raidDestroyEvent(RaidEvent.DestroyEvent event) {
        if (FreeEpicGamesEventJS.RAID_DESTROY_EVENT.hasListeners()) {
            EventResult result = FreeEpicGamesEventJS.RAID_DESTROY_EVENT.post(new RaidEventJS.DestroyEvent(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void raidOpenTreasureEvent(RaidEvent.OpenTreasureEvent event) {
        if (FreeEpicGamesEventJS.RAID_OPEN_TREASURE_EVENT.hasListeners()) {
            EventResult result = FreeEpicGamesEventJS.RAID_OPEN_TREASURE_EVENT.post(new RaidEventJS.OpenTreasureEvent(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void raidTickEvent(RaidEvent.TickEvent event) {
        if (FreeEpicGamesEventJS.RAID_TICK_EVENT.hasListeners()) {
            FreeEpicGamesEventJS.RAID_TICK_EVENT.post(new RaidEventJS.TickEvent(event));
        }
    }
}
