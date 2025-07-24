package org.thexeler.freeepicgames.handler;

import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.thexeler.freeepicgames.events.NpcEvent;
import org.thexeler.freeepicgames.storage.agent.NpcWorldDataAgent;
import org.thexeler.freeepicgames.storage.view.NpcView;

public class NpcEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDeath(LivingDeathEvent event) {
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null) {
            NpcEvent.KilledEvent npcEvent = new NpcEvent.KilledEvent(source, event.getEntity(), event.getSource());
            if (MinecraftForge.EVENT_BUS.post(npcEvent)) {
                event.setCanceled(true);
                event.getEntity().setHealth(1.0F);
            }
        }

        NpcView view = NpcView.getEntity(event.getEntity());
        if (view != null) {
            NpcEvent.DeathEvent npcEvent = new NpcEvent.DeathEvent(view, event.getSource());
            if (!MinecraftForge.EVENT_BUS.post(npcEvent)) {
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
            MinecraftForge.EVENT_BUS.post(new NpcEvent.InteractEvent(entity, event.getEntity()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDamage(LivingDamageEvent event) {
        NpcView source = NpcView.getEntity(event.getSource().getEntity());
        if (source != null && event.getSource().is(DamageTypes.MOB_ATTACK)) {
            NpcEvent.MeleeAttackEvent npcEvent = new NpcEvent.MeleeAttackEvent(source, event.getEntity(), event.getSource(), event.getAmount());
            MinecraftForge.EVENT_BUS.post(npcEvent);
            event.setAmount(npcEvent.getAmount());
        }

        NpcView entity = NpcView.getEntity(event.getEntity());
        if (entity != null) {
            NpcEvent.DamageEvent npcEvent = new NpcEvent.DamageEvent(entity, event.getSource(), event.getAmount());
            if (!MinecraftForge.EVENT_BUS.post(npcEvent)) {
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
                MinecraftForge.EVENT_BUS.post(new NpcEvent.TickEvent(entity));
            });
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onProjectileFire(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile) {
            NpcView view = NpcView.getEntity(projectile.getOwner());
            if (view != null) {
                MinecraftForge.EVENT_BUS.post(new NpcEvent.RangeAttack.FireEvent(view, projectile));
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getProjectile().getOwner();
        if (entity != null) {
            NpcView view = NpcView.getEntity(entity);
            if (view != null) {
                NpcEvent.RangeAttack.HitEvent npcEvent = new NpcEvent.RangeAttack.HitEvent(view, event.getProjectile(), event.getRayTraceResult(), event.getEntity());
                MinecraftForge.EVENT_BUS.post(npcEvent);
            }
        }
    }
}
