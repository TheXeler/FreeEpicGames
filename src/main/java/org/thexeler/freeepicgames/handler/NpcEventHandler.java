package org.thexeler.freeepicgames.handler;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.thexeler.freeepicgames.database.agent.WorldNpcDataAgent;
import org.thexeler.freeepicgames.database.view.NpcView;
import org.thexeler.freeepicgames.events.NpcEvent;

@EventBusSubscriber
public class NpcEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        NpcView entity = NpcView.getEntity(event.getEntity());
        if (entity != null) {
            NpcEvent.DeathEvent deathEvent = new NpcEvent.DeathEvent(entity, event.getSource());
            NeoForge.EVENT_BUS.post(deathEvent);
            event.setCanceled(deathEvent.isCanceled());
            if (!deathEvent.isCanceled()) {
                entity.discard();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        NpcView entity = NpcView.getEntity(event.getTarget());
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new NpcEvent.InteractEvent(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDamage(LivingIncomingDamageEvent event) {
        NpcView entity = NpcView.getEntity(event.getEntity());
        if (entity != null) {
            NpcEvent.DamageEvent damageEvent = new NpcEvent.DamageEvent(entity, event.getSource(), event.getAmount());
            NeoForge.EVENT_BUS.post(damageEvent);
            event.setCanceled(damageEvent.isCanceled());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityTick(ServerTickEvent.Post event) {
        event.getServer().getAllLevels().forEach(serverLevel -> {
            WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(serverLevel);
            agent.getAllNPC().forEach(entity -> NeoForge.EVENT_BUS.post(new NpcEvent.TickEvent(entity)));
        });
    }
}
