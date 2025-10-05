package org.thexeler.mind.intention;

import net.minecraft.world.entity.Entity;
import org.thexeler.mind.api.BaseIntention;
import org.thexeler.mind.api.IntentionPriority;
import org.thexeler.mind.api.IntentionType;

public abstract class SimpleIntention extends BaseIntention {
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
