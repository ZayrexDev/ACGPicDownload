package xyz.zcraft.acgpicdownload.util.source.argument;

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


    @Override
    public String toString() {
        return "StringArgument {" + "name=" + name + ", value=" + value + "}";
    }

}
