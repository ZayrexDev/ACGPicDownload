package xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import xyz.zcraft.ACGPicDownload.Commands.Fetch;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SourceFetcher {
    public static List<Result> fetch(Source source) throws Exception {
        Connection.Response response = Jsoup.connect(source.getUrl()).followRedirects(true).ignoreContentType(true).timeout(10000).execute();

        if (SourceManager.isEmpty(source.getReturnType())) {
            if (response.body().startsWith("{") && response.body().endsWith("}")) {
                source.setReturnType("json");
            } else if (Objects.equals(response.url().toString(), source.getUrl())) {
                source.setReturnType("redirect");
            } else {
                throw new Exception("Can't judge return type");
            }
        }
        if (Objects.equals("json", source.getReturnType().toLowerCase())) {
            return parseJson(response.body(), source);
        } else if (Objects.equals("redirect", source.getReturnType().toLowerCase())) {
            String s = response.url().toString();
            return List.of(new Result(s.substring(s.lastIndexOf("/") + 1), s, null));
        } else {
            return List.of();
        }
    }

    private static List<Result> parseJson(String jsonString, Source source) {
        JSONObject obj = JSONObject.parseObject(jsonString);

        // Follow the pathToSource
        Object data;
        if (source.getSourceKey() != null && !source.getSourceKey().trim().equals("")) {
            data = followPath(obj, source.getSourceKey());
        } else {
            data = obj;
        }


        // Judge if to parse as array
        ArrayList<JSONObject> pics = new ArrayList<>();
        if (data instanceof JSONArray array) {
            array.forEach(o -> pics.add((JSONObject) o));
        } else {
            pics.add((JSONObject) data);
        }

        ArrayList<Result> results = new ArrayList<>();

        // For each of the json objects to parse
        pics.forEach(jsonObject -> {
            Result r = new Result();
            r.setJson(jsonObject);
            r.setUrl(String.valueOf(followPath(jsonObject, source.getPicUrl())));

            if (source.getNameRule() != null && !source.getNameRule().trim().equals("")) {
                r.setFileName(Fetch.replaceArgument(source.getNameRule(), jsonObject));
                for(String l : ILLEGAL_STRINGS){
                    r.setFileName(r.getFileName().replaceAll(l, "_"));
                }
            } else {
                r.setFileName(r.getUrl().substring(r.getUrl().lastIndexOf("/") + 1));
            }

            r.setFileName(new String(r.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            results.add(r);
        });

        return results;
    }

    private final static String[] ILLEGAL_STRINGS = {"\\\\", "/", ":", "\\*", "\\?", "\"", "<", ">", "\\|"};

    // Follow the given path.
    private static Object followPath(JSONObject obj, String path) {
        if (path == null || path.trim().equals("")) {
            return obj;
        }

        Object result = obj.clone();

        String[] pathToSource = path.split("/");
        for (String s : pathToSource) {
            if (result instanceof JSONObject) {
                result = ((JSONObject) result).get(s);
            } else if (result instanceof JSONArray) {
                if (s.endsWith("]") && s.lastIndexOf("[") != -1) {
                    String prob = s.substring(s.lastIndexOf("[") + 1, s.length() - 1);
                    try {
                        int index = Integer.parseInt(prob);
                        result = ((JSONArray) result).get(index);
                    } catch (NumberFormatException e) {
                        result = ((JSONArray) result).get(0);
                    }
                }
            } else {
                throw new JSONException("Could not parse path " + path);
            }
        }

        return result;
    }
}
