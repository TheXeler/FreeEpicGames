package org.thexeler.mind.intention;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public abstract class Intention {
    @Getter
    protected IntentionPriority priority;
    @Getter
    protected IntentionType type;
    @Getter
    protected final Entity origin;

    public Intention(Entity origin, IntentionType type) {
        this.type = type;
        this.origin = origin;
        this.priority = IntentionPriority.NORMAL;
    }

    public Intention(Entity origin, IntentionType type, IntentionPriority priority) {
        this.type = type;
        this.origin = origin;
        this.priority = priority;
    }

    public abstract boolean execute();

    public abstract void hold();

    public abstract static class SimpleIntention extends Intention {
        public SimpleIntention(Entity origin, IntentionType type) {
            super(origin, type);
        }

        public SimpleIntention(Entity origin, IntentionType type, IntentionPriority priority) {
            super(origin, type, priority);
        }

        @Override
        public void hold() {
        }
    }

    public static class MoveIntention extends SimpleIntention {
        @Getter
        protected final Vec3 pos;

        public MoveIntention(Entity origin, Vec3 pos) {
            super(origin, IntentionType.MOVE);
            this.pos = pos;
        }

        public MoveIntention(Entity origin, Vec3 pos, IntentionPriority priority) {
            super(origin, IntentionType.MOVE, priority);
            this.pos = pos;
        }

        @Override
        public boolean execute() {
            if (this.origin.position().equals(this.pos)) {
                return true;
            } else {
                this.origin.move(MoverType.SELF, this.pos.subtract(this.origin.position()));
                return false;
            }
        }
    }

    public static class FollowIntention extends SimpleIntention {
        @Getter
        protected final Entity target;
        @Getter
        @Setter
        protected double distance;
        @Getter
        @Setter
        protected boolean forceDistance;

        public FollowIntention(Entity origin, Entity target) {
            super(origin, IntentionType.FOLLOW);
            this.target = target;
            this.distance = 0.1F;
            this.forceDistance = false;
        }

        public FollowIntention(Entity origin, Entity target, IntentionPriority priority) {
            super(origin, IntentionType.FOLLOW, priority);
            this.target = target;
            this.distance = 0.1F;
            this.forceDistance = false;
        }

        public FollowIntention(Entity origin, Entity target, double distance) {
            super(origin, IntentionType.FOLLOW);
            this.target = target;
            this.distance = distance;
            this.forceDistance = false;
        }

        public FollowIntention(Entity origin, Entity target, double distance, IntentionPriority priority) {
            super(origin, IntentionType.FOLLOW, priority);
            this.target = target;
            this.distance = distance;
            this.forceDistance = false;
        }

        public FollowIntention(Entity origin, Entity target, double distance, boolean forcedDistance) {
            super(origin, IntentionType.FOLLOW);
            this.target = target;
            this.distance = distance;
            this.forceDistance = forcedDistance;
        }

        public FollowIntention(Entity origin, Entity target, double distance, boolean forcedDistance, IntentionPriority priority) {
            super(origin, IntentionType.FOLLOW, priority);
            this.target = target;
            this.distance = distance;
            this.forceDistance = forcedDistance;
        }

        @Override
        public boolean execute() {
            Vec3 originPos = this.origin.position();
            Vec3 targetPos = this.target.position();

            double dx = targetPos.x - originPos.x,
                    dy = targetPos.y - originPos.y,
                    dz = targetPos.z - originPos.z;

            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (this.distance != distance) {
                if (forceDistance || this.distance < distance) {
                    // movePos应为圆心为targetPos半径为distance的圆上最近一点
                    this.origin.move(MoverType.SELF, new Vec3(
                            originPos.x + (dx * distance),
                            originPos.y + (dy * distance),
                            originPos.z + (dz * distance)));
                }
            }

            return false;
        }
    }

    public static class MeleeAttackIntention extends FollowIntention {
        @Getter
        @Setter
        protected float damage;

        public MeleeAttackIntention(Entity origin, Entity target) {
            super(origin, target);
        }

        public MeleeAttackIntention(Entity origin, Entity target, IntentionPriority priority) {
            super(origin, target, priority);
        }

        @Override
        public boolean execute() {
            super.execute();
            if (this.target.isAttackable() && this.target.isAlive()) {
                if (this.origin.position().distanceTo(this.target.position()) < this.distance) {
                    if (origin instanceof LivingEntity livingEntity) {
                        target.hurt(origin.damageSources().mobAttack(livingEntity), this.damage);
                    } else {
                        target.hurt(origin.damageSources().generic(), this.damage);
                    }
                }
                return this.target.isAlive();
            }
            return true;
        }
    }

    public static class RangeAttackIntention extends FollowIntention {
        @Setter
        protected BiConsumer<Entity, Entity> attackFunction;

        public RangeAttackIntention(Entity origin, Entity target, double distance, BiConsumer<Entity, Entity> attackFunction) {
            super(origin, target, distance, true);
            this.attackFunction = attackFunction;
        }

        public RangeAttackIntention(Entity origin, Entity target, double distance, BiConsumer<Entity, Entity> attackFunction, IntentionPriority priority) {
            super(origin, target, distance, true, priority);
            this.attackFunction = attackFunction;
        }

        @Override
        public boolean execute() {
            super.execute();
            if (this.target.isAttackable() && this.target.isAlive()) {
                attackFunction.accept(this.origin, this.target);
                return this.target.isAlive();
            }
            return true;
        }
    }
}
