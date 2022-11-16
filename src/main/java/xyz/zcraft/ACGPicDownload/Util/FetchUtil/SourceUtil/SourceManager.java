package xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SourceManager {
    private static List<Source> sources;

    public static void readConfig() throws IOException, JSONException{
        sources = parse(readStringFromConfig());
    }
    private static String readStringFromConfig() throws IOException {
        StringBuilder sb = new StringBuilder();
        File f = new File("sources.json");
        if(!f.exists()){
            if(!f.createNewFile()){
                throw new IOException("Could not create " + f);
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(SourceManager.class.getResource("sources.json")).openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                bw.write(line);
                bw.newLine();
            }

            bw.close();
            reader.close();
        }else {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();
        }

        return sb.toString();
    }

    private static final List<String> returnTypes = new ArrayList<>(Arrays.asList("json", "redirect"));

    private static List<Source> parse(String configString) throws JSONException {
        JSONArray config = JSON.parseArray(configString);
        if(config == null){
            return List.of();
        }
        List<Source> sources = new ArrayList<>();
        config.forEach(o -> {
            Source s = JSONObject.parseObject(String.valueOf(o), Source.class);
            try {
                verifySource(s);
                sources.add(s);
            } catch (Exception e) {
                System.err.println("Failed to parse source " + s.getName() + " : " + e);
            }
        });
        return sources;
    }

    private static void verifySource(Source source) throws Exception {
        if (isEmpty(source.getName())) {
            throw new Exception("Source name must not be empty");
        }
        if (isEmpty(source.getUrl())) {
            throw new Exception("Source url must not be empty");
        }
        if (isEmpty(source.getPicUrl())) {
            throw new Exception("Source's picUrl must not be empty");
        }
        if (!isEmpty(source.getReturnType()) && !returnTypes.contains(source.getReturnType())) {
            throw new Exception("Unknown return type:" + source.getReturnType());
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static List<Source> getSources() throws IOException {
        return sources;
    }

    public static Source getSourceByName(List<Source> sources, String name) throws IOException {
        for (Source source : sources) {
            if (source.getName().equals(name)) {
                return source;
            }
        }
        return null;
    }
}
