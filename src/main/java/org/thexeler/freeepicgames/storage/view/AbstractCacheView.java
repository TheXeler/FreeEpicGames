package org.thexeler.freeepicgames.storage.view;

import com.google.gson.JsonObject;

public interface AbstractCacheView {
    JsonObject toCacheJson();
}
