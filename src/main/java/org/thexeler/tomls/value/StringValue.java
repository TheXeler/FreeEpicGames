package org.thexeler.tomls.value;

import org.thexeler.tomls.exception.UnparseableException;

public class StringValue extends ConfigValue<String> {
    public StringValue( String comment, String defaultValue) {
        super( comment, defaultValue);
    }

    @Override
    public void parse(String str) {
        String valueStr = str.trim();
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"") && valueStr.length() >= 2) {
            this.value = valueStr.substring(1, valueStr.length() - 1);
        } else {
            throw new UnparseableException("Unparseable StringValue: " + str);
        }
    }

    @Override
    public String compile() {
        return String.format("%s", value);
    }
}
