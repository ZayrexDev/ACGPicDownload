package xyz.zcraft.acgpicdownload.util.source.argument;

import lombok.Getter;

@Getter
public abstract class Argument<T> {
    protected final String name;
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

}
