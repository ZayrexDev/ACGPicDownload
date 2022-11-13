package xyz.zcraft.ACGPicDownload.Util.SourceUtil;

import com.alibaba.fastjson2.JSONException;
import xyz.zcraft.ACGPicDownload.Util.Result;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

public class SourceFetcher {
    public static Result[] fetch(Source source) throws IOException {
        // Get the json from source
        String s = Jsoup.connect(source.getUrl())
                .followRedirects(true)
                .ignoreContentType(true)
                .get()
                .body()
                .ownText();

        JSONObject obj = JSONObject.parseObject(s);

        // Follow the pathToSource
        Object data;
        if(source.getSourceKey() != null && !source.getSourceKey().trim().equals("")) {
            data = followPath(obj, source.getSourceKey());
        }else{
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
            r.setUrl(String.valueOf(followPath(jsonObject, source.getPicUrl())));

            if (source.getNameRule() != null && !source.getNameRule().trim().equals("")) {
                r.setFileName(source.getNameRule());
                jsonObject.forEach((t, u) -> r.setFileName(r.getFileName().replaceAll(
                        "\\$\\{" + t + "}",
                        String.valueOf(u))));
            } else {
                r.setFileName(r.getUrl().substring(r.getUrl().lastIndexOf("/") + 1));
            }
            results.add(r);
        });

        return results.toArray(new Result[] {});
    }

    // Follow the given path.
    private static Object followPath(JSONObject obj, String path) {
        if (path == null || path.trim().equals("")){
            return obj;
        }

        Object result = obj.clone();

        String[] pathToSource = path.split("/");
        for (int i = 0; i < pathToSource.length; i++) {
            if (result instanceof JSONObject){
                result = ((JSONObject) result).get(pathToSource[i]);
            }else if(result instanceof JSONArray){
                if(pathToSource[i].endsWith("]") && pathToSource[i].lastIndexOf("[") != -1){
                    String prob = pathToSource[i].substring(pathToSource[i].lastIndexOf("[") + 1,pathToSource[i].length() - 1);
                    try{
                        int index = Integer.parseInt(prob);
                        result = ((JSONArray) result).get(index);
                    }catch (NumberFormatException e){
                        result = ((JSONArray) result).get(0);
                    }
                }
            }else{
                throw new JSONException("Could not parse path " + path);
            }
        }

        return result;
    }
}
