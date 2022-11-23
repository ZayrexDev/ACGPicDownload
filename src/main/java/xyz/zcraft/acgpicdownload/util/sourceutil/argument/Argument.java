package xyz.zcraft.acgpicdownload.util.sourceutil.argument;

public abstract class Argument<T> {
    public Argument(String name) {
        this.name = name;
    }

    public abstract void set(T value);
    public abstract boolean isValid(T value);

    protected String name;
    protected T value;
    public String getName() {
        return name;
    }
    public T getValue() {
        return value;
    }
}
