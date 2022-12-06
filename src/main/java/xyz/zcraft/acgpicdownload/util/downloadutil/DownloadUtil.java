package xyz.zcraft.acgpicdownload.util.downloadutil;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;
import xyz.zcraft.acgpicdownload.util.pixivutils.GifData;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownload;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownloadUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.zip.ZipInputStream;

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

            if (referer != null) {
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
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public void saveGifToFile(File file, GifData gifData) throws IOException {
        URL url = new URL(gifData.getSrc());
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Referer", PixivDownloadUtil.REFERER);

        ZipInputStream zis = new ZipInputStream(urlConnection.getInputStream());
        file.createNewFile();

        AnimatedGifEncoder age = new AnimatedGifEncoder();
        age.setDelay(JSONObject.parseObject(gifData.getOrigFrame().get(0).toString()).getInteger("delay"));
        age.setRepeat(0);
        age.start(new FileOutputStream(file));

        while (zis.getNextEntry() != null) {
            age.addFrame(ImageIO.read(zis));
        }

        age.finish();
    }

    public void download(Result r, File toDic, DownloadResult d, boolean saveFullResult, String referer)
            throws IOException {
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
                jsonf = new File(toDic,
                        r.getFileName().substring(0, r.getFileName().lastIndexOf(".") + 1).concat("json"));
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

    public void downloadPixiv(PixivDownload a, File toDic, String cookieString, String proxyHost, Integer proxyPort)
            throws IOException {
        switch (a.getArtwork().getIllustType()) {
            case 2 -> {
                downloadPixivGif(a, toDic, proxyHost, proxyPort);
            }

            default -> {
                downloadPixivIllusion(a, toDic, cookieString, proxyHost, proxyPort);
            }
        }
    }

    private void downloadPixivGif(PixivDownload a, File toDic, String proxyHost, Integer proxyPort) throws IOException {
        try {
            GifData gifData = PixivFetchUtil.getGifData(a.getArtwork(), proxyHost, proxyPort);
            URL url = new URL(gifData.getSrc());
            URLConnection c = url.openConnection();
            c.setRequestProperty("Referer", PixivDownloadUtil.REFERER);
            ZipInputStream zis = new ZipInputStream(c.getInputStream());

            AnimatedGifEncoder age = new AnimatedGifEncoder();
            age.setRepeat(0);
            age.setDelay(gifData.getOrigFrame().getJSONObject(0).getInteger("delay"));
            File f = new File(toDic, a.getArtwork().getId()+".gif");

            a.setStatus(DownloadStatus.STARTED);
            f.createNewFile();
            age.start(new FileOutputStream(f));
            while (zis.getNextEntry() != null) {
                age.addFrame(ImageIO.read(zis));
            }

            age.finish();

            a.setStatus(DownloadStatus.COMPLETED);
        } catch (Exception e) {
            Main.logError(e);
            retriedCount++;
            if (retriedCount > maxRetryCount) {
                if (a != null) {
                    a.setStatus(DownloadStatus.FAILED);
                }
                throw e;
            } else {
                downloadPixivGif(a, toDic, proxyHost, proxyPort);
            }
        }
    }

    private void downloadPixivIllusion(PixivDownload a, File toDic, String cookieString, String proxyHost,
            Integer proxyPort)
            throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        File f = null;
        try {
            LinkedList<String> pages = PixivFetchUtil.getFullPages(a.getArtwork(), proxyHost, proxyPort);

            if (!toDic.exists() && !toDic.mkdirs()) {
                a.setStatus(DownloadStatus.FAILED);
            }

            for(String s : pages){
                URL url = new URL(s);
                URLConnection c = url.openConnection();

                c.setRequestProperty("Referer", PixivDownloadUtil.REFERER);

                if (a != null) {
                    a.setTotalSize(c.getContentLengthLong());
                    a.setStatus(DownloadStatus.STARTED);
                }

                is = c.getInputStream();

                // f = new File(toDic,
                // a.getArtwork().getId().concat("_p").concat(String.valueOf(a.getArtwork().getPageCount())));
                f = new File(toDic, s.substring(s.lastIndexOf("/") + 1));
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
            }

            if (a != null) {
                a.setStatus(DownloadStatus.COMPLETED);
            }
        } catch (IOException e) {
            Main.logError(e);
            if (is != null)
                is.close();
            if (fos != null)
                fos.close();
            if (f != null)
                f.delete();
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
