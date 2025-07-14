package org.thexeler.freeepicgames.database.view;

import com.google.gson.JsonObject;

public interface AbstractCacheView {
    JsonObject toCacheJson();
}
