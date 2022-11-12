package Util.SourceUtil;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import Util.Result;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        Object data = followPath(obj, source.getSourceKey());

        // Judge if to parse as array
        ArrayList<JSONObject> pics = new ArrayList<>();
        if (source.isAsArray()) {
            JSONArray array = (JSONArray) data;
            array.forEach(new Consumer<Object>() {
                @Override
                public void accept(Object o) {
                    pics.add((JSONObject) o);
                }
            });
        } else {
            pics.add((JSONObject) data);
        }

        ArrayList<Result> results = new ArrayList<>();

        // For each of the json objects to parse
        pics.forEach(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) {
                Result r = new Result();
                r.setUrl(String.valueOf(followPath(jsonObject, source.getPicUrl())));

                if (source.getNameRule() != null && source.getNameRule().trim() != "") {
                    r.setFileName(source.getNameRule());
                    jsonObject.forEach(new BiConsumer<String, Object>() {
                        @Override
                        public void accept(String t, Object u) {
                            r.setFileName(r.getFileName().replaceAll(
                                    "\\$\\{" + t + "}",
                                    String.valueOf(u)));
                        }
                    });
                } else {
                    r.setFileName(r.getUrl().substring(r.getUrl().lastIndexOf("/") + 1));
                }
                results.add(r);
            }
        });

        return results.toArray(new Result[] {});
    }

    // Follow the given path.
    private static Object followPath(JSONObject obj, String path) {
        JSONObject result = obj.clone();

        String[] pathToSource = path.split("/");
        for (int i = 0; i < pathToSource.length - 1; i++) {
            result = (JSONObject) obj.get(pathToSource[i]);
        }

        return result.get(pathToSource[pathToSource.length - 1]);
    }
}
