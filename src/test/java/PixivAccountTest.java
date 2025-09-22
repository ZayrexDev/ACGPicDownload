import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Objects;

import static xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil.parseCookie;

public class PixivAccountTest {
    public static void main(String[] args) {
        String cookieString = "PHPSESSID=88458885_oPHKLxNYucl77CsueCnlimJbk7jFCnxS;";
        HashMap<String, String> cookie = parseCookie(cookieString);
        Connection c = Jsoup.connect("https://www.pixiv.net")
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .cookies(cookie)
                .timeout(10 * 1000);

        c.proxy("127.0.0.1", 7890);

        try {
            String text = Objects.requireNonNull(c.get().getElementById("meta-global-data")).attr("content");
            final JSONObject jsonObject = JSONObject.parseObject(text);
            System.out.println(jsonObject.getString("token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
