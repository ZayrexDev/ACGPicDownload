package xyz.zcraft.acgpicdownload.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtil {
    private static ResourceBundle BUNDLE;

    static {
        loadDefault();
    }

    public static void load(String language) {
        BUNDLE = ResourceBundle.getBundle("xyz.zcraft.acgpicdownload.languages.String", Locale.forLanguageTag(language));
    }

    public static void loadDefault() {
        BUNDLE = ResourceBundle.getBundle("xyz.zcraft.acgpicdownload.languages.String", Locale.getDefault());
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
