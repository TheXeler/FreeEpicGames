package org.thexeler.freeepicgames.storage.agent;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import org.thexeler.freeepicgames.storage.utils.ModSavedData;

public abstract class AbstractDataAgent {
    public AbstractDataAgent() {
    }

    public abstract void load();

    public abstract void save();
}
