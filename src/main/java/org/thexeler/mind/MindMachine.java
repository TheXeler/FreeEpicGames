package org.thexeler.mind;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.entity.Entity;
import org.thexeler.mind.intention.Intention;
import org.thexeler.mind.intention.IntentionPriority;
import org.thexeler.mind.intention.IntentionType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@Slf4j
public class MindMachine {
    @Getter
    private final Entity origin;
    @Getter
    private final int stepTick;
    @Getter
    @Setter
    private int tickCount;

    private final List<Intention> intentions;

    public MindMachine(Entity origin) {
        this.origin = origin;

        this.stepTick = 5;
        this.tickCount = 0;
        this.intentions = Collections.synchronizedList(new LinkedList<>());

        intentions.add(new Intention.SimpleIntention(origin, IntentionType.IDLE, IntentionPriority.LOWEST) {
            @Override
            public boolean execute() {
                return false;
            }
        });
    }


    public void addIntention(Intention intention) {
        if (intention.getPriority() == IntentionPriority.URGENT) {
            this.intentions.add(0, intention);
        } else {
            ListIterator<Intention> i = this.intentions.listIterator();

            while (i.hasNext()) {
                Intention current = i.next();
                if (!intention.getPriority().biggerThan(current.getPriority())) {
                    i.previous();
                    i.add(intention);
                    return;
                }
            }

            this.intentions.add(intention);
        }
    }

    public void tick() {
        tickCount++;
        if (tickCount >= stepTick) {
            tickCount -= stepTick;
            step();
        }
    }


    private void step() {
        Intention intention = intentions.get(0);

        if (intention != null) {
            if (intention.getType() != IntentionType.IDLE) {
                if (intention.execute()) {
                    if (intention.getType() != IntentionType.IDLE) {
                        intentions.remove(0);
                    }
                }
                intentions.forEach(i -> {
                    if (i != intention) {
                        i.hold();
                    }
                });
                intentions.sort((i1, i2) -> i2.getPriority().compareTo(i1.getPriority()));
            }
        }
    }

    public boolean interrupt() {
        if (intentions.size() > 1) {
            intentions.remove(0);
            return true;
        }
        return false;
    }
}
