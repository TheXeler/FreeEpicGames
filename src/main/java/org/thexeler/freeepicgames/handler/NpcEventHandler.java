package org.thexeler.freeepicgames.handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.thexeler.freeepicgames.event.NpcEvent;
import org.thexeler.freeepicgames.storage.agent.NpcWorldDataAgent;
import org.thexeler.freeepicgames.storage.view.NpcView;
import org.thexeler.slacker.SlackerForge;

public class NpcEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDeath(LivingDeathEvent event) {
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.Killed npcEvent = new NpcEvent.Killed(source, event.getEntity(), event.getSource());
            if (SlackerForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                event.setCanceled(true);
                event.getEntity().setHealth(1.0F);
            }
        }

        NpcView view = NpcView.getEntity(event.getEntity());
        if (view != null) {
            NpcEvent.Death npcEvent = new NpcEvent.Death(view, event.getSource());
            if (!SlackerForge.EVENT_BUS.post(npcEvent).isCanceled()) {
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
            SlackerForge.EVENT_BUS.post(new NpcEvent.Interact(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDamage(LivingDamageEvent event) {
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.Attack npcEvent = new NpcEvent.Attack(source, event.getEntity(), event.getSource(), event.getAmount());
            SlackerForge.EVENT_BUS.post(npcEvent);
            event.setAmount(npcEvent.getAmount());
        }

        NpcView entity = NpcView.getEntity(event.getEntity());
        if (entity != null) {
            NpcEvent.Damage npcEvent = new NpcEvent.Damage(entity, event.getSource(), event.getAmount());
            if (!SlackerForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                event.setAmount(npcEvent.getAmount());
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityTick(TickEvent.ServerTickEvent event) {
        event.getServer().getAllLevels().forEach(serverLevel -> {
            NpcWorldDataAgent agent = NpcWorldDataAgent.getInstance(serverLevel);
            agent.getAllNpc().forEach(entity -> {
                if (entity.getNpcType().isWeakAI()) {
                    entity.getMind().tick();
                }
                SlackerForge.EVENT_BUS.post(new NpcEvent.Tick(entity));
            });
        });
    }
}
