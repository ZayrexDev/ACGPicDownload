package Util.SourceUtil;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SourceManager {
    public static String readConfig() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("sources.json"));

        String line;
        while((line = reader.readLine()) != null){
            sb.append(line);
        }

        reader.close();

        return sb.toString();
    }

    public static List<Source> parse(String configString){
        JSONArray config = JSON.parseArray(configString);
        List<Source> sources = new ArrayList<>();
        config.forEach(o -> {
            Source s = JSONObject.parseObject(String.valueOf(o),Source.class);
            sources.add(s);
        });
        return sources;
    }

    public static List<Source> parseFromConfig() throws IOException{
        return parse(readConfig());
    }
}
