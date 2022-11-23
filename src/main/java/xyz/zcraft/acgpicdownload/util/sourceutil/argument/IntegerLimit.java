package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

public class IntegerLimit {
    private Integer minValue = null;
    private Integer maxValue = null;
    private Integer step = null;
    private boolean hasMax = false;
    private boolean hasMin = false;
    private boolean hasStep = false;

    public boolean isValid(Integer value) {
        if(hasMax && value > maxValue){
            return false;
        }
        if(hasMin && value < minValue){
            return false;
        }
        if(hasStep){
            if(hasMax && (value - maxValue) % step != 0){
                return false;
            }else if(hasMin && (value - minValue) % step != 0){
                return false;
            }else{
                return value % step == 0;
            }
        }
        return true;
    }

    public static IntegerLimit biggerThan(Integer minValue, Integer step){
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

    public static IntegerLimit smallThan(Integer maxValue){
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

    private IntegerLimit(){}

    public static IntegerLimit range(Integer minValue, Integer maxValue, Integer step){
        return new IntegerLimit(minValue, maxValue,step);
    }

    public static IntegerLimit range(Integer minValue, Integer maxValue) {
        return new IntegerLimit(minValue, maxValue);
    }

    public IntegerLimit(Integer minValue, Integer maxValue) {
        hasMax = true;
        hasMin = true;
        hasStep = false;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = 1;
    }

    public boolean isHasMax() {
        return hasMax;
    }

    public boolean isHasMin() {
        return hasMin;
    }

    public boolean isHasStep() {
        return hasStep;
    }

    public IntegerLimit(Integer minValue, Integer maxValue, Integer step) {
        if(minValue != null){
            hasMin = true;
            this.minValue = minValue;
        }
        if(maxValue != null){
            hasMax = true;
            this.maxValue = maxValue;
        }
        if(step != null){
            hasStep = true;
            this.step = step;
        }
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
