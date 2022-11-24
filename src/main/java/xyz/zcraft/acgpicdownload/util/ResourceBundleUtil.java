package xyz.zcraft.acgpicdownload.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtil {
    private static final ResourceBundle BUNDLE;

    static {
        BUNDLE = ResourceBundle.getBundle("xyz.zcraft.acgpicdownload.languages.String", Locale.CHINA);
    }

    public static String getString(String key) {
        if (BUNDLE.containsKey(key)) {
            return BUNDLE.getString(key);
        } else {
            return null;
        }
    }

    public static ResourceBundle getResource() {
        return BUNDLE;
    }
}
