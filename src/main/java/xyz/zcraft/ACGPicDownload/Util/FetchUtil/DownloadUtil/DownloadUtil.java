package xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil;

import xyz.zcraft.ACGPicDownload.Main;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUtil {
    public void download(Result r, File toDic, DownloadResult d) throws IOException {
        if (Main.debug) {
            Main.addToPool(hashCode(), "created");
        }

        if (!toDic.exists() && !toDic.mkdirs()) {
            if (d != null) {
                d.setStatus(DownloadStatus.FAILED);
            } else {
                throw new IOException("Can't create directory");
            }
        }

        if (Main.debug) {
            Main.addToPool(hashCode(), "created dir");
        }

        URL url = new URL(r.getUrl());
        URLConnection c = url.openConnection();

        if (Main.debug) {
            Main.addToPool(hashCode(), "opened connection");
        }

        if (d != null) {
            d.setTotalSize(c.getContentLengthLong());
            d.setStatus(DownloadStatus.STARTED);
        }

        InputStream is = c.getInputStream();

        if (Main.debug) {
            Main.addToPool(hashCode(), "opened stream");
        }

        FileOutputStream fos = new FileOutputStream(new File(toDic, r.getFileName()));

        if (Main.debug) {
            Main.addToPool(hashCode(), "fos");
        }

        byte[] buffer = new byte[10240];
        int byteRead;
        int total = 0;

        if (Main.debug) {
            Main.addToPool(hashCode(), "buffer");
        }

        while ((byteRead = is.read(buffer)) != -1) {
            if (Main.debug) {
                Main.addToPool(hashCode(), total + "/" + c.getContentLengthLong());
            }
            total += byteRead;
            fos.write(buffer, 0, byteRead);
            if (d != null) {
                d.setSizeDownloaded(total);
            }
        }

        is.close();
        fos.close();

        if (Main.debug) {
            Main.removeFromPool(hashCode());
        }

        if (d != null) {
            d.setStatus(DownloadStatus.COMPLETED);
        }
    }

    public void download(Result r, File toDic) throws IOException {
        download(r, toDic, null);
    }
}
