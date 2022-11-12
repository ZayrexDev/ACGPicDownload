package Util.SourceUtil;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SourceManager {
    public static String readConfig() throws IOException {
        StringBuilder sb = new StringBuilder();
        File f = new File("sources.json");
        if (f.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            return sb.toString();
        } else {
            f.createNewFile();
            return "";
        }
    }

    public static List<Source> parse(String configString) throws JSONException{
        JSONArray config = JSON.parseArray(configString);
        List<Source> sources = new ArrayList<>();
        config.forEach(o -> {
            Source s = JSONObject.parseObject(String.valueOf(o), Source.class);
            sources.add(s);
        });
        return sources;
    }

    public static List<Source> parseFromConfig() throws IOException {
        return parse(readConfig());
    }

    public static Source getSourceByName(List<Source> sources, String name) throws IOException {
        for (int index = 0; index < sources.size(); index++) {
            if (sources.get(index).getName().equals(name)) {
                return sources.get(index);
            }
        }
        return null;
    }
}
