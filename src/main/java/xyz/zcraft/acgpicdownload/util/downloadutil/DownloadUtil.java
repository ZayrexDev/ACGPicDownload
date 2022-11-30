package xyz.zcraft.acgpicdownload.util.downloadutil;

import com.alibaba.fastjson2.JSONWriter.Feature;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownload;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownloadUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class DownloadUtil {
    private final int maxRetryCount;
    private int retriedCount = 0;

    public DownloadUtil(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public static void download(File file, String link, String referer) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(link);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();

            if(referer != null){
                c.setRequestProperty("Referer", referer);
            }

            is = c.getInputStream();

            file.createNewFile();
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[10240];
            int byteRead;

            while ((byteRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteRead);
            }

            is.close();
            fos.close();
        }finally{
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public void download(Result r, File toDic, DownloadResult d, boolean saveFullResult, String referer) throws IOException {
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

            if (referer != null) {
                c.setRequestProperty("referer", referer);
            }

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
                download(r, toDic, d, saveFullResult, referer);
            }
        }
    }

    public void downloadPixiv(PixivDownload a, File toDic, String cookieString, String proxyHost, int proxyPort) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        File f = null;
        try {
            if (a.getArtwork().getImageUrl() == null || a.getArtwork().getImageUrl().isEmpty()) {
                a.getArtwork().setImageUrl(PixivFetchUtil.getImageUrl(a.getArtwork(), cookieString, proxyHost, proxyPort));
            }

            if (!toDic.exists() && !toDic.mkdirs()) {
                a.setStatus(DownloadStatus.FAILED);
            }

            URL url = new URL(a.getArtwork().getImageUrl());
            URLConnection c = url.openConnection();

            c.setRequestProperty("Referer", PixivDownloadUtil.REFERER);

            if (a != null) {
                a.setTotalSize(c.getContentLengthLong());
                a.setStatus(DownloadStatus.STARTED);
            }

            is = c.getInputStream();

            String s = a.getArtwork().getImageUrl();

            // f = new File(toDic, a.getArtwork().getId().concat("_p").concat(String.valueOf(a.getArtwork().getPageCount())));
            f = new File(toDic,s.substring(s.lastIndexOf("/") + 1));
            fos = new FileOutputStream(f);

            byte[] buffer = new byte[10240];
            int byteRead;
            int total = 0;

            while ((byteRead = is.read(buffer)) != -1) {
                total += byteRead;
                fos.write(buffer, 0, byteRead);
                if (a != null) {
                    a.setSizeDownloaded(total);
                }
            }

            is.close();
            fos.close();

            if (a != null) {
                a.setStatus(DownloadStatus.COMPLETED);
            }
        } catch (IOException e) {
            Main.logError(e);
            if (is != null) is.close();
            if (fos != null) fos.close();
            if (f != null) f.delete();
            retriedCount++;
            if (retriedCount > maxRetryCount) {
                if (a != null) {
                    a.setStatus(DownloadStatus.FAILED);
                }
                throw e;
            } else {
                downloadPixiv(a, toDic, cookieString, proxyHost, proxyPort);
            }
        }
    }
}
