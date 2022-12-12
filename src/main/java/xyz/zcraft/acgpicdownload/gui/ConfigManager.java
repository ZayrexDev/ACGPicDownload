package xyz.zcraft.acgpicdownload.gui;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivAccount;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private static final HashSet<PixivAccount> accounts = new HashSet<>();
    private static JSONObject config;
    private static PixivAccount selectedAccount;

    public static PixivAccount getSelectedAccount() {
        return selectedAccount;
    }

    public static void setSelectedAccount(PixivAccount selectedAccount) {
        ConfigManager.selectedAccount = selectedAccount;
    }

    public static JSONObject getConfig() {
        return Objects.requireNonNull(config);
    }

    public static Object getValue(String key, Object defaultValue) {
        return Objects.requireNonNullElse(config.get(key), defaultValue);
    }

    public static double getDoubleIfExist(String key, double defaultValue) {
        if (config.containsKey(key)) return config.getDouble(key);
        return defaultValue;
    }

    public static void readConfig() throws IOException {
        File f = new File("config.json");
        if (!f.exists()) {
            f.createNewFile();
            config = new JSONObject();
        } else {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            config = Objects.requireNonNullElse(JSONObject.parse(sb.toString()), new JSONObject());
        }

        f = new File("accounts.json");
        if (!f.exists()) {
            f.createNewFile();
        } else {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            JSONArray acc = Objects.requireNonNullElse(JSONArray.parse(sb.toString()), new JSONArray());
            for (int i = 0; i < acc.size(); i++) {
                accounts.add(acc.getJSONObject(i).to(PixivAccount.class));
            }
        }
    }

    public static void saveConfig() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("config.json"));
        bw.write(config.toString(JSONWriter.Feature.PrettyFormat));
        bw.close();

        JSONArray arr = new JSONArray();
        bw = new BufferedWriter(new FileWriter("accounts.json"));
        arr.addAll(accounts);
        bw.write(arr.toString(JSONWriter.Feature.PrettyFormat));
        bw.close();
    }

    public static Set<PixivAccount> getAccounts() {
        return accounts;
    }
}
