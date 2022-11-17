package xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil;

import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUtil {
    private final int maxRetryCount;
    private int retriedCount = 0;

    public DownloadUtil(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void download(Result r, File toDic, DownloadResult d) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        File f = null;
        try {
            if (!toDic.exists() && !toDic.mkdirs()) {
                if (d != null) {
                    d.setStatus(DownloadStatus.FAILED);
                } else {
                    throw new IOException("Can't create directory");
                }
            }

            URL url = new URL(r.getUrl());
            URLConnection c = url.openConnection();

            if (d != null) {
                d.setTotalSize(c.getContentLengthLong());
                d.setStatus(DownloadStatus.STARTED);
            }

            is = c.getInputStream();

            f = new File(toDic, r.getFileName());
            fos = new FileOutputStream(f);

            byte[] buffer = new byte[10240];
            int byteRead;
            int total = 0;

            while ((byteRead = is.read(buffer)) != -1) {
                total += byteRead;
                fos.write(buffer, 0, byteRead);
                if (d != null) {
                    d.setSizeDownloaded(total);
                }
            }

            is.close();
            fos.close();

            if (d != null) {
                d.setStatus(DownloadStatus.COMPLETED);
            }
        } catch (IOException e) {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (f != null) {
                f.delete();
            }
            retriedCount++;
            if (retriedCount > maxRetryCount) {
                if (d != null) {
                    d.setStatus(DownloadStatus.FAILED);
                }
                throw e;
            }
        }
    }
}
