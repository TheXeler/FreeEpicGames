package org.thexeler.mind;


import lombok.Getter;
import net.minecraft.world.entity.Entity;
import org.thexeler.mind.intention.Intention;
import org.thexeler.mind.intention.IntentionPriority;
import org.thexeler.mind.intention.IntentionType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MindMachine {
    @Getter
    private Entity origin;

    private final List<Intention> intentions;

    public MindMachine() {
        this.intentions = Collections.synchronizedList(new LinkedList<>());
    }

    public void addIntention(Intention intention) {
        if (intention.getPriority() == IntentionPriority.URGENT) {
            this.intentions.addFirst(intention);
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

    public void step() {
        Intention intention = intentions.getFirst();

        if (intention != null) {
            if (intention.getType() != IntentionType.IDLE) {
                if (intention.execute()) {
                    if (intention.getType() != IntentionType.IDLE) {
                        intentions.removeFirst();
                    }
                }
                intentions.forEach(i -> {
                    if (i != intention) {
                        i.hold();
                    }
                });
                intentions.sort((i1, i2) -> i2.getPriority().compareTo(i1.getPriority()));
            }
        } else {
            intentions.add(new Intention.SimpleIntention(origin, IntentionType.IDLE, IntentionPriority.LOWEST) {
                @Override
                public boolean execute() {
                    return false;
                }
            });
        }
    }
}
