package xyz.zcraft.acgpicdownload.gui;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.*;
import java.util.Objects;

public class ConfigManager {
    private static JSONObject config;

    public static JSONObject getConfig() {
        return config;
    }

    public static void readConfig() throws IOException {
        File f = new File("config.json");
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
