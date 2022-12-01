package xyz.zcraft.acgpicdownload;

import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Test {
    private static final String cookie = "";

    public static void main(String[] args) throws IOException {
        // Set<String> strings = PixivFetchUtil.fetchUser("4813223", "127.0.0.1", 7890);
        // System.out.println(strings.size());
        // System.out.println();
        // List<String> queryStrings = PixivFetchUtil.buildQueryString(strings);
        // List<PixivArtwork> a = new LinkedList<>();
        // for (String s : queryStrings) {
        //     a.addAll(PixivFetchUtil.getUserArtworks(s, "4813223", "127.0.0.1", 7890));
        // }

        for (PixivArtwork aw : PixivFetchUtil.getDiscovery(cookie, "127.0.0.1", 7890)) {
            System.out.println(aw.getTitle() + " -> " + aw.getId() + " -> "  + PixivFetchUtil.getArtworkPageUrl(aw));
        }
    }
}
