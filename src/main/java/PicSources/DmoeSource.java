package PicSources;

import org.jsoup.Jsoup;

public class DmoeSource {
    private static String url =  "https://www.dmoe.cc/random.php";

    public String fetch() throws Exception {
        return Jsoup.connect(url)
                .followRedirects(true)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.26")
                .execute()
                .url()
                .toString();
    }
}
