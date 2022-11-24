package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

import java.util.Set;

public class LimitedStringArgument extends StringArgument {
    private final Set<String> validValues;

    public LimitedStringArgument(String name, Set<String> vaildValues) {
        super(name);
        this.validValues = vaildValues;
    }

    public Set<String> getValidValues() {
        return validValues;
    }

    @Override
    public String toString() {
        return "LimitedStringArgument {name=" + name + ", value=" + value + ", vaildValues=" + validValues + "}";
    }

    @Override
    public boolean isValid(String value) {
        return validValues.contains(value);
    }

    @Override
    public void set(String value) {
        if (isValid(value)) {
            this.value = value;
        }else{
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
    }
}
