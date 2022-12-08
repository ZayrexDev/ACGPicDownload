package xyz.zcraft.acgpicdownload.gui;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {
    public static URL loadURL(String fileName) {
        return ResourceLoader.class.getResource(fileName);
    }

    public static InputStream loadStream(String fileName) {
        return ResourceLoader.class.getResourceAsStream(fileName);
    }
}
