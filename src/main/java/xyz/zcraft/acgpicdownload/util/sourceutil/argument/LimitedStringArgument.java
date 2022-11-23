package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

import java.util.Set;

public class LimitedStringArgument extends Argument<String> {
    private Set<String> vaildValues;


    public LimitedStringArgument(String name,Set<String> vaildValues) {
        super(name);
        this.vaildValues = vaildValues;
    }

    @Override
    public boolean isValid(String value) {
        return vaildValues.contains(value);
    }

    @Override
    public void set(String value) {
        if(isValid(value)){
            this.value = value;
        }else{
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
    }
}
