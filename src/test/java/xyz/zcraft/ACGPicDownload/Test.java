package xyz.zcraft.ACGPicDownload;

import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.util.LinkedList;

public class Test {
    public static void main(String[] args) throws IOException {
        LinkedList<String> ranking = PixivFetchUtil.getRankingIDs("monthly", "manga", "", "127.0.0.1", 7890);
        for (String id : ranking) {
//            System.out.println(pixivArtwork);
        }
    }
}
