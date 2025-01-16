package xyz.zcraft.acgpicdownload.util.source.argument;

import lombok.Getter;
import lombok.Setter;

@Getter
public class IntegerLimit {
    @Setter
    private Integer minValue = null;
    @Setter
    private Integer maxValue = null;
    @Setter
    private Integer step = null;
    private boolean hasMax = false;
    private boolean hasMin = false;
    private boolean hasStep = false;

    private IntegerLimit() {
    }

    public IntegerLimit(Integer minValue, Integer maxValue) {
        hasMax = true;
        hasMin = true;
        hasStep = false;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = 1;
    }

    public IntegerLimit(Integer minValue, Integer maxValue, Integer step) {
        if (minValue != null) {
            hasMin = true;
            this.minValue = minValue;
        }
        if (maxValue != null) {
            hasMax = true;
            this.maxValue = maxValue;
        }
        if (step != null) {
            hasStep = true;
            this.step = step;
        }
    }

    public static IntegerLimit biggerThan(Integer minValue, Integer step) {
        IntegerLimit t = new IntegerLimit();
        t.setMaxValue(minValue);
        t.setStep(step);
        t.hasMax = false;
        t.hasMin = true;
        t.hasStep = true;
        return t;
    }

    public static IntegerLimit biggerThan(Integer minValue) {
        IntegerLimit t = new IntegerLimit();
        t.setMaxValue(minValue);
        t.setStep(1);
        t.hasMax = false;
        t.hasMin = true;
        t.hasStep = false;
        return t;
    }

    public static IntegerLimit smallThan(Integer maxValue) {
        IntegerLimit t = new IntegerLimit();
        t.setMaxValue(maxValue);
        t.setStep(1);
        t.hasMax = true;
        t.hasMin = false;
        t.hasStep = false;
        return t;
    }

    public static IntegerLimit smallThan(Integer maxValue, Integer step) {
        IntegerLimit t = new IntegerLimit();
        t.setMaxValue(maxValue);
        t.setStep(step);
        t.hasMax = true;
        t.hasMin = false;
        t.hasStep = true;
        return t;
    }

    public static IntegerLimit range(Integer minValue, Integer maxValue, Integer step) {
        return new IntegerLimit(minValue, maxValue, step);
    }

    public static IntegerLimit range(Integer minValue, Integer maxValue) {
        return new IntegerLimit(minValue, maxValue);
    }

    public boolean isValid(Integer value) {
        if (hasMax && value > maxValue) {
            return false;
        }
        if (hasMin && value < minValue) {
            return false;
        }
        if (hasStep) {
            if (hasMax && (value - maxValue) % step != 0) {
                return false;
            } else if (hasMin && (value - minValue) % step != 0) {
                return false;
            } else {
                return value % step == 0;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "IntegerLimit [minValue=" + minValue + ", maxValue=" + maxValue + ", step=" + step + "]";
    }

}
