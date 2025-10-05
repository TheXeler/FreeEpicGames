package org.thexeler.tomls.value;

import org.thexeler.tomls.exception.UnparseableException;

public class FloatValue extends ConfigValue<Double> {
    private double min;
    private double max;

    public FloatValue(String comment, Double defaultValue) {
        super(comment, defaultValue);
    }

    public FloatValue(String comment, Double  defaultValue, double min, double max) {
        super(comment, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public void parse(String str) {
        try {
            String valueStr = str.trim().replaceAll(" ", "");
            this.value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            throw new UnparseableException("Unparseable number: " + str);
        }
    }

    @Override
    public String compile() {
        return value.toString();
    }
}
