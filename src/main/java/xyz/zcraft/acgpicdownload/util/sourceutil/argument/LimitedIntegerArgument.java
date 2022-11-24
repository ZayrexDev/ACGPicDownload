package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

public class LimitedIntegerArgument extends IntegerArgument {
    public IntegerLimit getLimit() {
        return limit;
    }

    private IntegerLimit limit;

    @Override
    public String toString() {
        return "LimitedIntegerArgument {limit=" + limit + ", name" + name + ", value" + value + "}";
    }

    public LimitedIntegerArgument(String name, IntegerLimit limit) {
        super(name);
        this.limit = limit;
    }

    @Override
    public void set(Integer value) {
        if(limit.isValid(value)){
            this.value = value;
        }else{
            throw new IllegalArgumentException("Illegal value:" + value);
        }
    }

    @Override
    public boolean isValid(Integer value) {
        return limit.isValid(value);
    }

}
