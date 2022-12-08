package xyz.zcraft.acgpicdownload;

import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        List<PixivArtwork> pixivArtworks = PixivFetchUtil.searchIllustArtworks("GenshinImpact", 0, 1, "", "127.0.0.1", 7890);
        for (PixivArtwork pixivArtwork : pixivArtworks) {
            System.out.println(pixivArtwork.getId());
        }
    }
}
