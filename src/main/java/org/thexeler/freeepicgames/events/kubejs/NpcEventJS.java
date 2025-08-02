package org.thexeler.freeepicgames.events.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.thexeler.freeepicgames.events.NpcEvent;
import org.thexeler.freeepicgames.storage.view.NpcView;

@Getter
@AllArgsConstructor
public abstract class NpcEventJS implements KubeEvent {
    protected final Entity entity;
    protected final NpcView view;

    public static class Create extends NpcEventJS {
        public Create(NpcEvent.Create createEvent) {
            super(createEvent.getEntity(), createEvent.getView());
        }
    }

    @Getter
    public static class Join extends NpcEventJS {
        private final ServerLevel level;

        public Join(NpcEvent.Join joinEvent) {
            super(joinEvent.getEntity(), joinEvent.getView());
            this.level = joinEvent.getLevel();
        }
    }

    @Getter
    public static class Death extends NpcEventJS {
        private final DamageSource source;

        public Death(NpcEvent.Death deathEvent) {
            super(deathEvent.getEntity(), deathEvent.getView());
            this.source = deathEvent.getSource();
        }
    }

    @Getter
    public static class Interact extends NpcEventJS {
        private final Player player;

        public Interact(NpcEvent.Interact interactEvent) {
            super(interactEvent.getEntity(), interactEvent.getView());
            this.player = interactEvent.getPlayer();
        }
    }

    @Getter
    public static class Damage extends NpcEventJS {
        private final DamageSource source;
        private float amount;
        private final DamageContainer container;

        public Damage(NpcEvent.Damage damageEvent) {
            super(damageEvent.getEntity(), damageEvent.getView());
            this.source = damageEvent.getSource();
            this.amount = damageEvent.getAmount();
            this.container = damageEvent.getContainer();
        }
    }

    public static class Tick extends NpcEventJS {
        public Tick(NpcEvent.Tick tickEvent) {
            super(tickEvent.getEntity(), tickEvent.getView());
        }
    }

    @Getter
    public static class Killed extends NpcEventJS {
        private final Entity killedEntity;
        private final DamageSource source;

        public Killed(NpcEvent.Killed killedEvent) {
            super(killedEvent.getEntity(), killedEvent.getView());
            this.killedEntity = killedEvent.getEntity();
            this.source = killedEvent.getSource();
        }
    }

    @Getter
    public static class Attack extends NpcEventJS {
        private final Entity target;
        private final DamageSource source;
        private float amount;
        private final DamageContainer container;

        public Attack(NpcEvent.Attack attackEvent) {
            super(attackEvent.getEntity(), attackEvent.getView());
            this.target = attackEvent.getTarget();
            this.source = attackEvent.getSource();
            this.amount = attackEvent.getAmount();
            this.container = attackEvent.getContainer();
        }
    }
}