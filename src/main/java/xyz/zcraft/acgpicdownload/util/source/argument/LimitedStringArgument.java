package xyz.zcraft.acgpicdownload.util.source.argument;

import lombok.Getter;

import java.util.Set;

@Getter
public class LimitedStringArgument extends StringArgument {
    private final Set<String> validValues;

    public LimitedStringArgument(String name, Set<String> validValues) {
        super(name);
        this.validValues = validValues;
    }

    @Override
    public String toString() {
        return "LimitedStringArgument {name=" + name + ", value=" + value + ", validValues=" + validValues + "}";
    }

    @Override
    public boolean isValid(String value) {
        return validValues.contains(value);
    }

    @Override
    public void set(String value) {
        if (isValid(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
    }
}
