package org.thexeler.freeepicgames.handler;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.thexeler.freeepicgames.event.NpcEvent;
import org.thexeler.freeepicgames.storage.agent.NpcWorldDataAgent;
import org.thexeler.freeepicgames.storage.view.NpcView;

public class NpcEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDeath(LivingDeathEvent event) {
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.Killed npcEvent = new NpcEvent.Killed(source, event.getEntity(), event.getSource());
            if (NeoForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                event.setCanceled(true);
                if (event.getEntity() instanceof LivingEntity entity) {
                    entity.setHealth(1.0F);
                }
            }
        }

        NpcView view = NpcView.getEntity(event.getEntity());
        if (view != null) {
            NpcEvent.Death npcEvent = new NpcEvent.Death(view, event.getSource());
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        NpcView entity = NpcView.getEntity(event.getTarget());
        if (entity != null) {
            NeoForge.EVENT_BUS.post(new NpcEvent.Interact(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDamage(LivingIncomingDamageEvent event) {
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.Attack npcEvent = new NpcEvent.Attack(source, event.getEntity(), event.getSource(), event.getAmount(), event.getContainer());
            NeoForge.EVENT_BUS.post(npcEvent);
            event.setAmount(npcEvent.getAmount());
        }

        NpcView entity = NpcView.getEntity(event.getEntity());
        if (entity != null) {
            NpcEvent.Damage npcEvent = new NpcEvent.Damage(entity, event.getSource(), event.getAmount(), event.getContainer());
            if (!NeoForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                event.setAmount(npcEvent.getAmount());
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityTick(ServerTickEvent.Post event) {
        event.getServer().getAllLevels().forEach(serverLevel -> {
            NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(serverLevel);
            agent.getAllNpc().forEach(entity -> {
                if (entity.getNpcType().isWeakAI()) {
                    entity.getMind().tick();
                }
                NeoForge.EVENT_BUS.post(new NpcEvent.Tick(entity));
            });
        });
    }
}
