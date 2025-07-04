package org.thexeler.freeepicgames.handler;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.thexeler.freeepicgames.database.agent.WorldNPCDataAgent;
import org.thexeler.freeepicgames.database.view.NPCView;
import org.thexeler.freeepicgames.events.NPCEvent;

@EventBusSubscriber
public class NPCEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        NPCView entity = NPCView.getEntity(event.getEntity());
        if (entity != null) {
            NPCEvent.NPCDeathEvent deathEvent = new NPCEvent.NPCDeathEvent(entity, event.getSource());
            NeoForge.EVENT_BUS.post(deathEvent);
            event.setCanceled(deathEvent.isCanceled());
            if (!deathEvent.isCanceled()) {
                entity.discard();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        NPCView entity = NPCView.getEntity(event.getTarget());
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new NPCEvent.NPCInteractEvent(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDamage(LivingIncomingDamageEvent event) {
        NPCView entity = NPCView.getEntity(event.getEntity());
        if (entity != null) {
            NPCEvent.NPCDamageEvent damageEvent = new NPCEvent.NPCDamageEvent(entity, event.getSource(), event.getAmount());
            NeoForge.EVENT_BUS.post(damageEvent);
            event.setCanceled(damageEvent.isCanceled());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityTick(ServerTickEvent event) {
        event.getServer().getAllLevels().forEach(serverLevel -> {
            WorldNPCDataAgent agent = WorldNPCDataAgent.getInstance(serverLevel);
            agent.getAllNPC().forEach(entity -> NeoForge.EVENT_BUS.post(new NPCEvent.NPCTickEvent(entity)));
        });
    }
}
