package xyz.zcraft.acgpicdownload;

import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws IOException {
        Set<String> strings = PixivFetchUtil.fetchUser("", "4813223", "127.0.0.1", 7890);
        System.out.println(strings.size());
        String s = PixivFetchUtil.buildQueryString(strings);
        List<PixivArtwork> a = PixivFetchUtil.getUserArtworks(s, "4813223", "127.0.0.1", 7890);
        System.out.println(a);
    }
}
