package xyz.zcraft.acgpicdownload.util.dl;

import com.alibaba.fastjson2.JSONWriter.Feature;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.fetch.Result;
import xyz.zcraft.acgpicdownload.util.pixiv.*;

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

            //noinspection ResultOfMethodCallIgnored
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
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
            if (jsonos != null) {
                jsonos.close();
            }
            if (jsonf != null) {
                //noinspection ResultOfMethodCallIgnored
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

    public void downloadPixiv(@NotNull PixivDownload a, File toDic,@Nullable String cookieString,@NotNull NamingRule namingRule, boolean fullResult,@Nullable String proxyHost,@Nullable Integer proxyPort, @NotNull ArtworkCondition condition)
            throws IOException {
        if (a.getArtwork().getIllustType() == 2) {
            downloadPixivGif(a, toDic, cookieString, namingRule, fullResult, proxyHost, proxyPort, condition);
        } else {
            downloadPixivIllusion(a, toDic, cookieString, namingRule, fullResult, proxyHost, proxyPort, condition);
        }
    }

    private void downloadPixivGif(@NotNull PixivDownload a, File toDic, String cookieString, NamingRule namingRule,
                                  boolean fullResult, String proxyHost, Integer proxyPort, @NotNull ArtworkCondition condition) throws IOException {
        try {
            GifData gifData = PixivFetchUtil.getGifData(a.getArtwork(), cookieString, proxyHost, proxyPort);
            if (!condition.isAlways()) {
                PixivFetchUtil.getFullData(a.getArtwork(), cookieString, proxyHost, proxyPort);
                if (!condition.test(a.getArtwork())) {
                    a.setStatus(DownloadStatus.FILTERED);
                    return;
                }
            }
            URL url = new URL(gifData.getSrc());
            URLConnection c = url.openConnection();
            c.setRequestProperty("Referer", PixivDownloadUtil.REFERER);
            final InputStream inputStream = c.getInputStream();
            ZipInputStream zis = new ZipInputStream(inputStream);

            AnimatedGifEncoder age = new AnimatedGifEncoder();
            age.setRepeat(0);
            age.setDelay(gifData.getOrigFrame().getJSONObject(0).getInteger("delay"));
            File f = new File(toDic, namingRule.name(a.getArtwork()) + ".gif");

            a.setStatus(DownloadStatus.STARTED);
            a.setTotal(gifData.getOrigFrame().size());
            a.setProgress(0);
            //noinspection ResultOfMethodCallIgnored
            f.createNewFile();
            age.start(new FileOutputStream(f));
            while (zis.getNextEntry() != null) {
                age.addFrame(ImageIO.read(zis));
                a.setProgress(a.getProgress() + 1);
            }

            age.finish();

            if (fullResult && a.getArtwork().getOrigJson() != null) {
                File jsonf = new File(toDic, namingRule.name(a.getArtwork()).concat(".json"));
                BufferedOutputStream jsonos = new BufferedOutputStream(new FileOutputStream(jsonf));

                String str = a.getArtwork().getOrigJson().toJSONString(Feature.PrettyFormat);
                jsonos.write(str.getBytes(StandardCharsets.UTF_8));
                jsonos.flush();
                jsonos.close();
            }

            a.setStatus(DownloadStatus.COMPLETED);
        } catch (Exception e) {
            Main.logError(e);
            retriedCount++;
            if (retriedCount > maxRetryCount) {
                a.setStatus(DownloadStatus.FAILED);
                throw e;
            } else {
                downloadPixivGif(a, toDic, cookieString, namingRule, fullResult, proxyHost, proxyPort, condition);
            }
        }
    }

    private void downloadPixivIllusion(@NotNull PixivDownload a, File toDic, String cookieString, NamingRule namingRule,
                                       boolean fullResult, String proxyHost, Integer proxyPort,@NotNull ArtworkCondition condition)
            throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        File f = null;
        try {
            LinkedList<String> pages = PixivFetchUtil.getFullPages(a.getArtwork(), cookieString, proxyHost, proxyPort);

            if (!condition.isAlways()){
                PixivFetchUtil.getFullData(a.getArtwork(), cookieString, proxyHost, proxyPort);
                if (!condition.test(a.getArtwork())) {
                    a.setStatus(DownloadStatus.FILTERED);
                    return;
                }
            }

            if (pages.size() > 1 && namingRule.multiP() == 0) {
                toDic = new File(toDic, namingRule.nameFolder(a.getArtwork()));
            }

            if (!toDic.exists() && !toDic.mkdirs()) {
                a.setStatus(DownloadStatus.FAILED);
            }

            a.setStatus(DownloadStatus.STARTED);
            a.setTotal(pages.size());

            for (int i = 0, pagesSize = pages.size(); i < pagesSize; i++) {
                a.setProgress(i);
                String s = pages.get(i);
                URL url = new URL(s);
                URLConnection c = url.openConnection();

                c.setRequestProperty("Referer", PixivDownloadUtil.REFERER);

                is = c.getInputStream();

                if (pagesSize == 1) {
                    f = new File(toDic, namingRule.name(a.getArtwork()) + s.substring(s.lastIndexOf(".")));
                } else {
                    f = new File(toDic, namingRule.name(a.getArtwork(), (i + 1)) + s.substring(s.lastIndexOf(".")));
                }
                fos = new FileOutputStream(f);

                byte[] buffer = new byte[10240];
                int byteRead;

                while ((byteRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteRead);
                }

                is.close();
                fos.close();
            }

            if (fullResult && a.getArtwork().getOrigJson() != null) {
                File jsonf = new File(toDic, namingRule.name(a.getArtwork()).concat(".json"));
                BufferedOutputStream jsonos = new BufferedOutputStream(new FileOutputStream(jsonf));

                String str = a.getArtwork().getOrigJson().toJSONString(Feature.PrettyFormat);
                jsonos.write(str.getBytes(StandardCharsets.UTF_8));
                jsonos.flush();
                jsonos.close();
            }

            a.setStatus(DownloadStatus.COMPLETED);
        } catch (IOException e) {
            Main.logError(e);
            if (is != null)
                is.close();
            if (fos != null)
                fos.close();
            if (f != null)
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            retriedCount++;
            if (retriedCount > maxRetryCount) {
                a.setStatus(DownloadStatus.FAILED);
                throw e;
            } else {
                downloadPixiv(a, toDic, cookieString, namingRule, fullResult, proxyHost, proxyPort, condition);
            }
        }
    }
}
