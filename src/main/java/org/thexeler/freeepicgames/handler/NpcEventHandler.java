package org.thexeler.freeepicgames.handler;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.thexeler.freeepicgames.database.agent.WorldNpcDataAgent;
import org.thexeler.freeepicgames.database.view.NpcView;
import org.thexeler.freeepicgames.events.NpcEvent;

public class NpcEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityDeath(LivingDeathEvent event) {
        NpcView view = NpcView.getEntity(event.getEntity());
        if (view != null) {
            NpcEvent.DeathEvent npcEvent = new NpcEvent.DeathEvent(view, event.getSource());
            if (!NeoForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                view.discard();
            } else {
                event.setCanceled(true);
                if (view.getOriginEntity() instanceof LivingEntity entity) {
                    entity.setHealth(1.0F);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        NpcView entity = NpcView.getEntity(event.getTarget());
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new NpcEvent.InteractEvent(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityDamage(LivingIncomingDamageEvent event) {
        NpcView entity = NpcView.getEntity(event.getEntity());
        if (entity != null) {
            NpcEvent.DamageEvent npcEvent = new NpcEvent.DamageEvent(entity, event.getSource(), event.getAmount(), event.getContainer());
            if (!NeoForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                event.setAmount(npcEvent.getAmount());
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityTick(ServerTickEvent.Post event) {
        event.getServer().getAllLevels().forEach(serverLevel -> {
            WorldNpcDataAgent agent = WorldNpcDataAgent.getInstance(serverLevel);
            agent.getAllNpc().forEach(entity -> NeoForge.EVENT_BUS.post(new NpcEvent.TickEvent(entity)));
        });
    }
}
