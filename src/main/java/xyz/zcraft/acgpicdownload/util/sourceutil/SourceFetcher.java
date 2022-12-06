package xyz.zcraft.acgpicdownload.util.sourceutil;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import xyz.zcraft.acgpicdownload.exceptions.UnsupportedReturnTypeException;
import xyz.zcraft.acgpicdownload.util.fetchutil.FetchUtil;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SourceFetcher {
    private final static String[] ILLEGAL_STRINGS = {"\\\\", "/", ":", "\\*", "\\?", "\"", "<", ">", "\\|"};

    public static ArrayList<Result> fetch(Source source, String proxyHost, int proxyPort) throws UnsupportedReturnTypeException, IOException {
        Connection conn = Jsoup.connect(source.getUrl().replaceAll("\\|", "%7C")).followRedirects(true).ignoreContentType(true);

        if (proxyHost != null && proxyPort != 0) {
            conn.proxy(proxyHost, proxyPort);
        }

        Connection.Response response = conn.timeout(10000).execute();
        if (SourceManager.isEmpty(source.getReturnType())) {
            if (response.body().startsWith("{") && response.body().endsWith("}")) {
                source.setReturnType("json");
            } else if (Objects.equals(response.url().toString(), source.getUrl())) {
                source.setReturnType("redirect");
            } else {
                throw new UnsupportedReturnTypeException();
            }
        }
        if (Objects.equals("json", source.getReturnType().toLowerCase())) {
            return parseJson(response.body(), source);
        } else if (Objects.equals("redirect", source.getReturnType().toLowerCase())) {
            String s = response.url().toString();
            String t;
            int a = s.lastIndexOf("?");
            int b = s.lastIndexOf("/");
            if (a > b) {
                t = s.substring(b + 1, a);
            } else {
                t = s.substring(b + 1);
            }
            return new ArrayList<>(List.of(new Result(t, s, null)));
        } else {
            return new ArrayList<>(List.of());
        }
    }

    public static ArrayList<Result> parseJson(String jsonString, Source source) throws JSONException {
        Object tmpObj = JSON.parse(jsonString);

        Object data;
        if (tmpObj instanceof JSONObject obj) {

            // Follow the pathToSource
            if (source.getSourceKey() != null && !source.getSourceKey().trim().equals("")) {
                data = followPath(obj, source.getSourceKey());
            } else {
                data = obj;
            }
        } else {
            data = tmpObj;
        }

        // Judge if to parse as array
        ArrayList<JSONObject> pics = new ArrayList<>();
        if (data instanceof JSONArray array) {
            array.forEach(o -> pics.add((JSONObject) o));
        } else if (data instanceof JSONObject) {
            pics.add((JSONObject) data);
        } else {
            throw new JSONException("Could not parse json");
        }

        ArrayList<Result> results = new ArrayList<>();

        // For each of the json objects to parse
        pics.forEach(jsonObject -> {
            List<Result> r = new LinkedList<>();
            Object t = followPath(jsonObject, source.getPicUrl());
            if (t instanceof JSONArray) {
                ((JSONArray) t).forEach(arg0 -> {
                    Result result = new Result();
                    result.setUrl(String.valueOf(arg0));
                    r.add(result);
                });
            } else {
                Result result = new Result();
                result.setUrl(String.valueOf(t));
                result.setJson(jsonObject);
                r.add(result);
            }

            r.forEach(arg0 -> {
                if (source.getNameRule() != null && !source.getNameRule().trim().equals("")) {
                    arg0.setFileName(FetchUtil.replaceArgument(source.getNameRule(), jsonObject));
                } else {
                    int a = arg0.getUrl().lastIndexOf("?");
                    int b = arg0.getUrl().lastIndexOf("/");
                    if (a > b) {
                        arg0.setFileName(arg0.getUrl().substring(b + 1, a));
                    } else {
                        arg0.setFileName(arg0.getUrl().substring(b + 1));
                    }
                }
                for (String l : ILLEGAL_STRINGS) {
                    arg0.setFileName(arg0.getFileName().replaceAll(l, "_"));
                }

                arg0.setFileName(new String(arg0.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                results.add(arg0);
            });
        });

        return results;
    }

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
