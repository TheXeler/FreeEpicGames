package org.thexeler.freeepicgames.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
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
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.KilledEvent npcEvent = new NpcEvent.KilledEvent(source, event.getEntity(), event.getSource());
            if (NeoForge.EVENT_BUS.post(npcEvent).isCanceled()) {
                event.setCanceled(true);
                if (event.getEntity() instanceof LivingEntity entity) {
                    entity.setHealth(1.0F);
                }
            }
        }

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
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.MeleeAttackEvent npcEvent = new NpcEvent.MeleeAttackEvent(source, event.getEntity(), event.getSource(), event.getAmount(), event.getContainer());
            NeoForge.EVENT_BUS.post(npcEvent);
            event.setAmount(npcEvent.getAmount());
        }

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
            agent.getAllNpc().forEach(entity -> {
                if (entity.getNpcType().isWeakAI()) {
                    entity.getMind().tick();
                }
                NeoForge.EVENT_BUS.post(new NpcEvent.TickEvent(entity));
            });
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onProjectileFire(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile) {
            NpcView view = NpcView.getEntity(projectile.getOwner());
            if (view != null) {
                NeoForge.EVENT_BUS.post(new NpcEvent.RangeAttack.FireEvent(view, projectile));
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getProjectile().getOwner();
        if (entity != null) {
            NpcView view = NpcView.getEntity(entity);
            if (view != null) {
                NpcEvent.RangeAttack.HitEvent npcEvent = new NpcEvent.RangeAttack.HitEvent(view, event.getProjectile(), event.getRayTraceResult(), event.getEntity());
                event.setCanceled(NeoForge.EVENT_BUS.post(npcEvent).isCanceled());
            }
        }
    }
}
