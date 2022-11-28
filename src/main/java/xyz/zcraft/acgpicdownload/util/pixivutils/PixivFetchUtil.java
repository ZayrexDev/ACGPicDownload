package xyz.zcraft.acgpicdownload.util.pixivutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class PixivFetchUtil {
    private static final String TOP = "https://www.pixiv.net/ajax/top/illust?mode=all&lang=zh";
    private static final String RELEATED = "https://www.pixiv.net/ajax/illust/%s/recommend/init?limit=%d";
    private static final String ARTWORK = "https://www.pixiv.net/artworks/";

    public static List<PixivArtwork> fetchMenu(String cookieString, String proxyHost, int proxyPort) throws IOException{
        HashMap<String,String> cookie = CookieUtil.parseCookie(cookieString);
        Connection c = Jsoup.connect(TOP)
                            .ignoreContentType(true)
                            .method(Method.GET)
                            .cookies(cookie)
                            .timeout(10*1000);

        if(proxyHost!=null&&proxyPort!=0){
            c.proxy(proxyHost, proxyPort);
        }

        String jsonString = c.get().body().ownText();

        LinkedList<PixivArtwork> artworks = new LinkedList<>();

        JSONArray illust = JSONObject.parse(jsonString).getJSONObject("body").getJSONObject("thumbnails").getJSONArray("illust");

        for(int i=0;i<illust.size();i++){
            artworks.add(illust.getObject(i, PixivArtwork.class));
        }

        return artworks;
    }

    public static String getLink(PixivArtwork artwork, String cookieString, String proxyHost, int proxyPort) throws IOException{
        HashMap<String, String> cookie = CookieUtil.parseCookie(cookieString);
        Connection c = Jsoup.connect(getArtworkPage(artwork))
                .ignoreContentType(true)
                .method(Method.GET)
                .cookies(cookie)
                .timeout(10 * 1000);
        if (proxyHost != null && proxyPort != 0) {
            c.proxy(proxyHost, proxyPort);
        }

        return JSONObject.parseObject(c.get().head().getElementById("meta-preload-data").attr("content")).getJSONObject("illust").getJSONObject(artwork.getId()).getJSONObject("urls").getString("original");
    }

    public static List<PixivArtwork> getReleated(PixivArtwork artwork, int limit, String cookieString, String proxyHost, int proxyPort)
            throws IOException {
        HashMap<String, String> cookie = CookieUtil.parseCookie(cookieString);
        Connection c = Jsoup.connect(String.format(RELEATED, artwork.getId(),limit))
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

    public static String getArtworkPage(PixivArtwork artwork){
        return ARTWORK + artwork.getId();
    }
}
