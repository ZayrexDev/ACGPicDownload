package xyz.zcraft.acgpicdownload.util.pixivutils;

import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadResult;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PixivDownloadUtil {
    public static final String REFERER = "https://www.pixiv.net";

    public static void startDownload(List<PixivDownload> artworksDownloads, String outputDir,
                                     Logger logger, int maxThread, Runnable onUpdate,
                                     String cookieString, String proxyHost, int proxyPort) {
        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            logger.err(ResourceBundleUtil.getString("cli.fetch.err.cannotCreatDir"));
            return;
        }

        ThreadPoolExecutor tpe;

        if (maxThread == -1) {
            tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else {
            tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
        }

        for (var a : artworksDownloads) {
            if (a.getStatus().equals(DownloadStatus.CREATED) || a.getStatus().equals(DownloadStatus.FAILED)) {
                DownloadResult r = new DownloadResult();
                tpe.execute(() -> {
                    try {
                        new DownloadUtil(1).downloadPixiv(a, outDir, r, cookieString, proxyHost, proxyPort);
                    } catch (Exception e) {
                        Main.logError(e);
                        r.setStatus(DownloadStatus.FAILED);
                        r.setErrorMessage(e.toString());
                    }
                });
            }
        }
    }
}
