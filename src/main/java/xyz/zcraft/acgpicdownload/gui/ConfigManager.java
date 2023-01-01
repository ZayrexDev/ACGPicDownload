package xyz.zcraft.acgpicdownload.gui;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.log4j.Logger;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivAccount;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private static final HashSet<PixivAccount> accounts = new HashSet<>();
    private static JSONObject config;
    public static final Logger logger = Logger.getLogger(GUI.class);
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
            logger.info("Loaded configuration from config.json");
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

            logger.info("Read " + accounts.size() + " accounts from accounts.json");
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

        logger.info("Configuration saved");
    }

    public static Set<PixivAccount> getAccounts() {
        return accounts;
    }
}
