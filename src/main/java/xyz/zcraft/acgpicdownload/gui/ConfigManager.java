package xyz.zcraft.acgpicdownload.gui;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class ConfigManager {
    private static JSONObject config;

    private static final HashMap<String, String> tempConfig = new HashMap<>();

    public static HashMap<String, String> getTempConfig() {
        return tempConfig;
    }

    public static JSONObject getConfig() {
        return Objects.requireNonNull(config);
    }

    public static Object getValue(String key, Object defaultValue) {
        return Objects.requireNonNullElse(config.get(key), defaultValue);
    }

    public static Object getSubValue(String sub, String key, Object defaultValue) {
        if(config.getJSONObject("sub") == null) return defaultValue;
        return Objects.requireNonNullElse(config.getJSONObject("sub").get(key), defaultValue);
    }

    public static double getDoubleIfExist(String key,double defaultValue) {
        if(config.containsKey(key)) return config.getDouble(key);
        return defaultValue;
    }

    public static void readConfig() throws IOException {
        File f = new File("config.json");
        if (!f.exists()) {
            f.createNewFile();
            config = new JSONObject();
            return;
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(f));

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        reader.close();

        config = Objects.requireNonNullElse(JSONObject.parse(sb.toString()), new JSONObject());
    }

    public static void saveConfig() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("config.json"));
        bw.write(config.toString(JSONWriter.Feature.PrettyFormat));
        bw.close();
    }
}
