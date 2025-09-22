import xyz.zcraft.acgpicdownload.util.pixiv.GifData;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;

public class GifTest {
    private static final String cookieString = "PHPSESSID=53586815_etMGmfiftP72YvjcUPE3qRGwd8xHHAj2";

    public static void main(String[] args) throws IOException {
        final PixivArtwork pixivArtwork = new PixivArtwork();
        pixivArtwork.setId("91945977");
        GifData gifData = PixivFetchUtil.getGifData(pixivArtwork, cookieString, "127.0.0.1", 7890);
        System.out.println(gifData.getSrc());
    }
}
