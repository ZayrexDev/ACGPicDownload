package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

public class IntegerArgument extends Argument<Integer> {

    public IntegerArgument(String name) {
        super(name);
    }

    @Override
    public void set(Integer value) {
        this.value = value;
    }

    @Override
    public boolean isValid(Integer value) {
        return true;
    }

}
