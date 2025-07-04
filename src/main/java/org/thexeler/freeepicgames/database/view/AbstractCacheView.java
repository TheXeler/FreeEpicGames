package org.thexeler.freeepicgames.database.view;

import com.google.gson.JsonObject;
import net.minecraft.nbt.Tag;

public interface AbstractCacheView {
    JsonObject toCacheJson();
}
