package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

public abstract class Argument<T extends Object> {
    protected String name;
    protected T value;

    public Argument(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Argument {name=" + name + ", value=" + value + "}";
    }

    public abstract void set(T value);

    public abstract boolean isValid(T value);

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }
}
