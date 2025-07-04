package org.thexeler.freeepicgames.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.thexeler.freeepicgames.database.agent.WorldNPCDataAgent;
import org.thexeler.freeepicgames.database.view.NPCView;
import org.thexeler.freeepicgames.events.NPCEvent;

@Mod.EventBusSubscriber
public class NPCEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        NPCView entity = NPCView.getEntity(event.getEntity());
        if (entity != null) {
            NPCEvent.NPCDeathEvent deathEvent = new NPCEvent.NPCDeathEvent(entity, event.getSource());
            MinecraftForge.EVENT_BUS.post(deathEvent);
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
            MinecraftForge.EVENT_BUS.post(new NPCEvent.NPCInteractEvent(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDamage(LivingDamageEvent event) {
        NPCView entity = NPCView.getEntity(event.getEntity());
        if (entity != null) {
            NPCEvent.NPCDamageEvent damageEvent = new NPCEvent.NPCDamageEvent(entity, event.getSource(), event.getAmount());
            MinecraftForge.EVENT_BUS.post(damageEvent);
            event.setCanceled(damageEvent.isCanceled());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityTick(TickEvent.ServerTickEvent event) {
        event.getServer().getAllLevels().forEach(serverLevel -> {
            WorldNPCDataAgent agent = WorldNPCDataAgent.getInstance(serverLevel);
            agent.getAllNPC().forEach(entity -> MinecraftForge.EVENT_BUS.post(new NPCEvent.NPCTickEvent(entity)));
        });
    }
}
