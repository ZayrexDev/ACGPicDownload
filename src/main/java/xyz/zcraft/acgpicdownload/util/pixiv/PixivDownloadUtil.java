package xyz.zcraft.acgpicdownload.util.pixiv;

import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.dl.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.dl.DownloadUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PixivDownloadUtil {
    public static final String REFERER = "https://www.pixiv.net/";

    public synchronized static void startDownload(List<PixivDownload> artworksDownloads, String outputDir,
                                                  Logger logger, int maxThread, String cookieString,
                                                  NamingRule namingRule, boolean fullResult,
                                                  String proxyHost, Integer proxyPort,
                                                  ArtworkCondition condition) {
        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            logger.err(ResourceBundleUtil.getString("gui.fetch.err.cannotCreatDir"));
            return;
        }

        ThreadPoolExecutor tpe;

        if (maxThread == -1) tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        else tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);

        for (var a : artworksDownloads) {
            if (a.getStatus().equals(DownloadStatus.CREATED) || a.getStatus().equals(DownloadStatus.FAILED)) {
                a.setStatus(DownloadStatus.INITIALIZE);
                tpe.execute(() -> {
                    try {
                        new DownloadUtil(1).downloadPixiv(
                                a, outDir, cookieString,
                                namingRule, fullResult,
                                proxyHost, proxyPort, condition
                        );
                    } catch (Exception e) {
                        Main.logError(e);
                        a.setStatus(DownloadStatus.FAILED);
                    }
                });
            }
        }
    }
}
