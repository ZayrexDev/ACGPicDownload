package xyz.zcraft.acgpicdownload.util.downloadutil;

import com.alibaba.fastjson2.JSONWriter.Feature;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class DownloadUtil {
    private final int maxRetryCount;
    private int retriedCount = 0;

    public DownloadUtil(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public static void download(File file, String link) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(link);

            is = url.openStream();

            fos = new FileOutputStream(file);

            byte[] buffer = new byte[10240];
            int byteRead;

            while ((byteRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteRead);
            }

            is.close();
            fos.close();
        } catch (IOException e) {
            Main.logError(e);
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (file.exists()){
                file.delete();
            }
        }
    }

    public void download(Result r, File toDic, DownloadResult d, boolean saveFullResult) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream jsonos = null;
        File f = null;
        File jsonf = null;
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

            if (saveFullResult && r.getJson() != null) {
                jsonf = new File(toDic, r.getFileName().substring(0, r.getFileName().lastIndexOf(".") + 1).concat("json"));
                jsonos = new BufferedOutputStream(new FileOutputStream(jsonf));

                String str = r.getJson().toJSONString(Feature.PrettyFormat);
                jsonos.write(str.getBytes(StandardCharsets.UTF_8));
                jsonos.flush();
                jsonos.close();
            }

            if (d != null) {
                d.setStatus(DownloadStatus.COMPLETED);
            }
        } catch (IOException e) {
            Main.logError(e);
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (f != null) {
                f.delete();
            }
            if (jsonos != null) {
                jsonos.close();
            }
            if (jsonf != null) {
                jsonf.delete();
            }
            retriedCount++;
            if (retriedCount > maxRetryCount) {
                if (d != null) {
                    d.setStatus(DownloadStatus.FAILED);
                }
                throw e;
            } else {
                download(r, toDic, d, saveFullResult);
            }
        }
    }
}
