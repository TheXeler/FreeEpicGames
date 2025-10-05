package org.thexeler.tomls.value;

import org.thexeler.tomls.exception.UnparseableException;

public class BooleanValue extends ConfigValue<Boolean> {
    public BooleanValue(String comment, Boolean defaultValue) {
        super(comment, defaultValue);
    }

    @Override
    public void parse(String str) {
        String valueStr = str.trim().toLowerCase().replaceAll(" ", "");
        if (valueStr.equals("true")) {
            this.value = true;
        } else if (valueStr.equals("false")) {
            this.value = false;
        } else {
            throw new UnparseableException("Unparseable boolean: " + str);
        }
    }

    @Override
    public String compile() {
        return (value ? "true" : "false");
    }
}
