package xyz.zcraft.acgpicdownload.util.sourceutil;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.exceptions.SourceConfigException;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadUtil;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.IntegerArgument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.IntegerLimit;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.LimitedIntegerArgument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.LimitedStringArgument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.StringArgument;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SourceManager {
    private static final ArrayList<String> returnTypes = new ArrayList<>(Arrays.asList("json", "redirect"));
    private static ArrayList<Source> sources;

    private static final String GITHUB_LINK = "https://raw.githubusercontent.com/zxzxy/ACGPicDownload/master/src/main/resources/xyz/zcraft/acgpicdownload/util/sourceutil/sources.json";

    public static void readConfig() throws IOException, JSONException {
        sources = parse(readStringFromConfig());
    }

    public static void updateFromGithub() throws IOException{
        File f = new File("sources.json");
        if(f.exists()) f.delete();
        DownloadUtil.download(f, GITHUB_LINK, null);
    }

    private static String readStringFromConfig() throws IOException {
        StringBuilder sb = new StringBuilder();
        File f = new File("sources.json");
        if (!f.exists()) {
            if (!f.createNewFile()) {
                throw new IOException("Could not create " + f);
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(SourceManager.class.getResource("sources.json")).openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                bw.write(line);
                bw.newLine();
            }

            bw.close();
            reader.close();
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();
        }

        return sb.toString();
    }

    private static ArrayList<Source> parse(String configString) throws JSONException {
        JSONArray config = JSON.parseArray(configString);
        if (config == null) {
            return new ArrayList<>();
        }
        ArrayList<Source> sources = new ArrayList<>();
        config.forEach(o -> {
            Source s = JSONObject.parseObject(String.valueOf(o), Source.class);
            try {
                verifySource(s);
                s.setArguments(parseArguments(s.getDefaultArgs()));
                sources.add(s);
            } catch (SourceConfigException|JSONException e) {
                System.err.println("Failed to parse source " + s.getName() + " : " + e);
                Main.logError(e);
            }
        });
        return sources;
    }

    private static void verifySource(Source source) throws SourceConfigException {
        if (isEmpty(source.getName())) {
            throw new SourceConfigException("Source name must not be empty");
        }
        if (isEmpty(source.getUrl())) {
            throw new SourceConfigException("Source url must not be empty");
        }
        if (!isEmpty(source.getReturnType()) && !returnTypes.contains(source.getReturnType())) {
            throw new SourceConfigException("Unknown return type:" + source.getReturnType());
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static ArrayList<Source> getSources() {
        return sources;
    }

    public static Source getSourceByName(List<Source> sources, String name) {
        for (Source source : sources) {
            if (source.getName().equals(name)) {
                return source;
            }
        }
        return null;
    }

    public static ArrayList<Argument<?>> parseArguments(JSONObject args) throws SourceConfigException, JSONException{
        ArrayList<Argument<?>> t = new ArrayList<>();
        if(args == null){
            return t;
        }

        for (String key : args.keySet()) {
            t.add(parseArgument(key, args.getJSONObject(key)));
        }

        return t;
    }

    public static Argument<?> parseArgument(String name, JSONObject arg) throws SourceConfigException {
        Argument<?> t = null;

        if (arg.containsKey("type")) {
            if (arg.get("type").equals("int")) {
                if(arg.containsKey("min") || arg.containsKey("max") || arg.containsKey("step")){
                    IntegerLimit l = new IntegerLimit(arg.getInteger("min"),arg.getInteger("max"),arg.getInteger("step"));
                    LimitedIntegerArgument lia = new LimitedIntegerArgument(name,l);
                    if(arg.containsKey("value")){
                        lia.set(arg.getInteger("value"));
                    }
                    t = lia;
                }else{
                    t = new IntegerArgument(name);
                }
            }else if(arg.get("type").equals("string")){
                if (arg.containsKey("from")) {
                    Object obj = arg.get("from");
                    if(obj instanceof JSONArray){
                        HashSet<String> tmp = new HashSet<>();
                        JSONArray arr = (JSONArray) obj;
                        arr.forEach(new Consumer<Object>() {
                            @Override
                            public void accept(Object arg0) {
                                tmp.add(String.valueOf(arg0));
                            }
                        });
                        LimitedStringArgument lsa = new LimitedStringArgument(name, tmp);
                        if(arg.containsKey("value")){
                            lsa.set(arg.getString("value"));
                        }
                        t = lsa;
                    }
                } else {
                    t = new StringArgument(name);
                }
            }
        }

        if(t == null){
            throw new SourceConfigException();
        }else{
            return t;
        }
    }
}
