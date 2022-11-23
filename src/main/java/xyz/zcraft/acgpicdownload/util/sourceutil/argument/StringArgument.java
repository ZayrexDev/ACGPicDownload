package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

public class StringArgument extends Argument<String> {
    public StringArgument(String name) {
        super(name);
    }

    public StringArgument(String name, String value) {
        super(name);
        set(value);
    }

    @Override
    public boolean isValid(String value) {
        return true;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }
}
