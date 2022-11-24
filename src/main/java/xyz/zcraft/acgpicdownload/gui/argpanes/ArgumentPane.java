package xyz.zcraft.acgpicdownload.gui.argpanes;

import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;

public interface ArgumentPane<T> {
    T getValueString();

    String getName();

    Argument<T> getValue();
}
