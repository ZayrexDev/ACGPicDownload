package xyz.zcraft.acgpicdownload;

import java.io.File;
import java.io.IOException;

import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadUtil;

public class Test {
    public static void main(String[] args) throws IOException {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("proxyHost", "127.0.0.1");
        System.getProperties().put("proxyPort", "7890");
        File f = new File("102922241_p0_custom1200.jpg");
        DownloadUtil.download(f, "https://i.pximg.net/c/250x250_80_a2/custom-thumb/img/2022/11/19/18/15/26/102922241_p0_custom1200.jpg",
                "https://www.pixiv.net");
    }
}
