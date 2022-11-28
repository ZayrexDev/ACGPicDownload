package xyz.zcraft.acgpicdownload.util.pixivutils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PixivFetchUtil {
    private static final String TOP = "https://www.pixiv.net/ajax/top/illust?mode=all&lang=zh";
    private static final String RELATED = "https://www.pixiv.net/ajax/illust/%s/recommend/init?limit=%d";
    private static final String ARTWORK = "https://www.pixiv.net/artworks/";

    public static List<PixivArtwork> fetchMenu(String cookieString, String proxyHost, int proxyPort) throws IOException {
        HashMap<String, String> cookie = parseCookie(cookieString);
        Connection c = Jsoup.connect(TOP)
                .ignoreContentType(true)
                .method(Method.GET)
                .cookies(cookie)
                .timeout(10 * 1000);

        if (proxyHost != null && proxyPort != 0) {
            c.proxy(proxyHost, proxyPort);
        }

        return parseArtworks(c.get().body().ownText());
    }

    public static LinkedList<PixivArtwork> parseArtworks(String jsonString) {
        LinkedList<PixivArtwork> artworks = new LinkedList<>();
        JSONArray illust = JSONObject.parse(jsonString).getJSONObject("body").getJSONObject("thumbnails").getJSONArray("illust");

        for (int i = 0; i < illust.size(); i++) {
            artworks.add(illust.getObject(i, PixivArtwork.class));
        }

        classifyArtwork(artworks, JSONObject.parse(jsonString).getJSONObject("body").getJSONObject("page"));

        return artworks;
    }

    public static String getImageUrl(PixivArtwork artwork, String cookieString, String proxyHost, int proxyPort) throws IOException {
        HashMap<String, String> cookie = parseCookie(cookieString);
        Connection c = Jsoup.connect(getArtworkPageUrl(artwork))
                .ignoreContentType(true)
                .method(Method.GET)
                .cookies(cookie)
                .timeout(10 * 1000);
        if (proxyHost != null && proxyPort != 0) {
            c.proxy(proxyHost, proxyPort);
        }

        return JSONObject.parseObject(c.get().head().getElementById("meta-preload-data").attr("content")).getJSONObject("illust").getJSONObject(artwork.getId()).getJSONObject("urls").getString("original");
    }

    public static List<PixivArtwork> getRelated(PixivArtwork artwork, int limit, String cookieString, String proxyHost, int proxyPort)
            throws IOException {
        HashMap<String, String> cookie = parseCookie(cookieString);
        Connection c = Jsoup.connect(String.format(RELATED, artwork.getId(), limit))
                .ignoreContentType(true)
                .method(Method.GET)
                .cookies(cookie)
                .timeout(10 * 1000);
        if (proxyHost != null && proxyPort != 0) {
            c.proxy(proxyHost, proxyPort);
        }

        String jsonString = c.get().body().ownText();

        LinkedList<PixivArtwork> artworks = new LinkedList<>();

        JSONArray illusts = JSONObject.parse(jsonString).getJSONObject("body").getJSONArray("illusts");

        for (int i = 0; i < illusts.size(); i++) {
            artworks.add(illusts.getObject(i, PixivArtwork.class));
        }

        return artworks;
    }

    public static String getArtworkPageUrl(PixivArtwork artwork) {
        return ARTWORK + artwork.getId();
    }

    public static HashMap<String, String> parseCookie(String cookieString) {
        String[] t = cookieString.split(";");
        HashMap<String, String> cookieMap = new HashMap<>();
        for (String t2 : t) {
            String[] t3 = t2.split("=");
            if (t3.length >= 2) {
                cookieMap.put(t3[0], t3[1]);
            }
        }
        return cookieMap;
    }

    public static void classifyArtwork(List<PixivArtwork> orig, JSONObject pageJson) {
        LinkedList<String> recommendIDs = new LinkedList<>();
        LinkedList<String> followIDs = new LinkedList<>();

        for (Object o : pageJson.getJSONArray("follow")) {
            followIDs.add(o.toString());
        }

        for (Object o : pageJson.getJSONObject("recommend").getJSONArray("ids")) {
            recommendIDs.add(o.toString());
        }

        JSONArray recommendByTag = pageJson.getJSONArray("recommendByTag");
        for (int i = 0; i < recommendByTag.size(); i++) {
            for (Object ids : recommendByTag.getJSONObject(i).getJSONArray("ids")) {
                recommendIDs.add(ids.toString());
            }
        }

        JSONArray recommendUser = pageJson.getJSONArray("recommendUser");
        for (int i = 0; i < recommendUser.size(); i++) {
            for (Object ids : recommendUser.getJSONObject(i).getJSONArray("illustIds")) {
                recommendIDs.add(ids.toString());
            }
        }

        for (PixivArtwork artwork : orig) {
            String id = artwork.getId();
            if (followIDs.contains(id)) {
                artwork.setFrom(From.Follow);
            } else if (recommendIDs.contains(id)) {
                artwork.setFrom(From.Recommend);
            } else {
                artwork.setFrom(From.Other);
            }
        }
    }

    public static List<PixivArtwork> selectArtworks(List<PixivArtwork> orig, int limit, boolean follow, boolean recommend, boolean other) {
        LinkedList<PixivArtwork> art = new LinkedList<>();
        for (PixivArtwork artwork : orig) {
            if (limit < art.size()) {
                From from = artwork.getFrom();
                if (from == null && other) art.add(artwork);
                if (from == From.Follow && follow) art.add(artwork);
                if (from == From.Recommend && recommend) art.add(artwork);
            } else {
                break;
            }
        }

        return art;
    }
}
