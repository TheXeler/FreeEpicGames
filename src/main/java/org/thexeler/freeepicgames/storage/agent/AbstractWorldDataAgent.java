package org.thexeler.freeepicgames.storage.agent;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import org.thexeler.freeepicgames.storage.utils.ModSavedData;

public abstract class AbstractWorldDataAgent {
    @Getter
    protected final ServerLevel world;

    public AbstractWorldDataAgent(ServerLevel world) {
        this.world = world;

        ModSavedData.register(this);
    }

    public abstract void load();

    public abstract void save();
}
