package org.thexeler.tomls.value;

import lombok.Getter;
import org.thexeler.tomls.exception.UnparseableException;

public class IntegerValue extends ConfigValue<Integer> {
    @Getter
    public int min;
    @Getter
    public int max;

    public IntegerValue(String comment, Integer defaultValue) {
        super(comment, defaultValue);
    }

    public IntegerValue(String comment, int defaultValue, int min, int max) {
        super(comment, defaultValue);
        
        this.min = min;
        this.max = max;
    }

    @Override
    public void parse(String str) {
        String valueStr = str.trim();
        try {
            this.value = Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            throw new UnparseableException("Unparseable StringValue: " + str);
        }
    }

    @Override
    public String compile() {
        return value.toString();
    }
}
