package org.thexeler.tomls.value;

import lombok.Getter;
import lombok.Setter;

public abstract class ConfigValue<T> {
    @Getter
    protected String comment;
    @Getter
    @Setter
    protected T value;
    @Getter
    protected T defaultValue;

    public ConfigValue(String comment, T defaultValue) {
        this.comment = comment;
        this.defaultValue = defaultValue;

        this.value = defaultValue;
    }

    public abstract void parse(String str);

    public abstract String compile();
}
