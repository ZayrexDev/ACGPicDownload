import com.alibaba.fastjson2.JSONObject;
import xyz.zcraft.acgpicdownload.util.pixivutils.NamingRule;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;

public class Test {
    public static void main(String[] args) {
        NamingRule namingRule = new NamingRule("{$id}{_$p}", 0, "te{$idst}");
        JSONObject object = new JSONObject();
        object.put("id", "114514");
        PixivArtwork a = new PixivArtwork();
        a.setOrigJson(object);
//        System.out.println(namingRule.name(a));
        String s = "https://www.pixiv.net/artworks/103365879.asdaw";
        System.out.println(namingRule.nameFolder(a));
        System.out.println(namingRule.name(a, 4) + s.substring(s.lastIndexOf(".")));
    }
}
