import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class PixivBookmarkTest {
    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();
        json.put("illust_id", "104278312");
        json.put("restrict", 0);
        json.put("comment", "");
        json.put("tags", new JSONArray());

        System.out.println(json.toJSONString());

        final String response = Jsoup.connect("https://www.pixiv.net/ajax/illusts/bookmarks/add")
                .proxy("127.0.0.1", 7890)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.69")
                .header("Cookie", "first_visit_datetime_pc=2023-01-17+20:57:49; p_ab_id=9; p_ab_id_2=3; p_ab_d_id=548119463; yuid_b=JUAnEEQ; privacy_policy_agreement=5; _ga=GA1.2.1769619120.1670332183; PHPSESSID=88458885_Ad022PSkkxj7xDaLeiJwS7LzPOyizsJv; device_token=f602092c8dd3ef28d19c163b5e115880; _ga_MZ1NL4PHH0=GS1.1.1673956724.1.1.1673956877.0.0.0; c_type=23; privacy_policy_notification=0; a_type=0; b_type=1; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; login_ever=yes; __cf_bm=q9Y6WtPU.i0hji5yXBtyVXsawJ2QU9qDDFfTkOt4cjI-1674913317-0-ATYCeZT0M3+Lgjh/q4oZo2HJsIDttHtt4ANl7TUFPnyyetZZVcFc8kqjuMUH8CY8RSeoOka/VMmO/51EDXtaGN8XIQGS17YG36f1jF1cEcFNptRSlniAvN6ZhQzkOPyOO4x3j10PVAyRmQt+yak5pitsJfP8nuyFMy36LDnQAvyIA3U/l7dUQRs+tGIUlDO4ciyhxfxq/Tu0vZDYRTFNWXk=; tag_view_ranking=-98s6o2-Rp~r_Jjn6Ua2V~jTSl_ciRq2~EZQqoW9r8g~2R7RYffVfj~SapL8yQw4Y~y8GNntYHsi~i83OPEGrYw~MhBZqc0gyc~Oe_3HJFeBA~O64_t9evHE~O2wfZxfonb~mYDe1eOQZ4~yL80PVO9Um~A7hSoqw-5Z~VTGlg0gWIi~cryvQ5p2Tx~Ged1jLxcdL~01ilCGA69_~SAyihQLaXc~Qq_PJ4rEpq~Ysy4PAF_ss~PwDMGzD6xn~qWFESUmfEs~Nj9Bt-JWdN~_IharlAfPe~jk9IzfjZ6n~n39RQWfHku~0CaTbfGZYk~ePN3h1AXKX")
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("x-csrf-token", "755a7ccfb5e2770de6c6ddf5f9e1a0f6")
                .requestBody(json.toJSONString())
                .method(Connection.Method.POST)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute()
                .body();
        System.out.println("response = " + response);
    }
}
