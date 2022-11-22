package xyz.zcraft.acgpicdownload.util.fetchutil;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.HttpStatusException;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.exceptions.SourceNotFoundException;
import xyz.zcraft.acgpicdownload.exceptions.UnsupportedReturnTypeException;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadManager;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadResult;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadUtil;
import xyz.zcraft.acgpicdownload.util.sourceutil.Source;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceFetcher;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceManager;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FetchUtil {
    private final static int PROGRESS_BAR_LENGTH = 20;
    private static final DecimalFormat df = new DecimalFormat("#.##%");

    public static String replaceArgument(String orig, JSONObject args) {
        if (orig == null) {
            return null;
        }

        int l;
        int r;

        while (((l = orig.indexOf("{")) != -1) && (r = orig.indexOf("}") + 1) != 0) {
            String[] a = {orig.substring(l, r)};
            boolean[] have = {false};

            String str = a[0];
            args.forEach((t, o) -> {
                String[] value = {};

                if (args.containsKey(t)) {
                    value = String.valueOf(args.get(t)).split("&");
                }

                if (a[0].contains("$" + t) && value.length != 0) {
                    have[0] = true;
                    StringBuilder sb = new StringBuilder();
                    for (String s : value) {
                        sb.append(str.substring(1, a[0].length() - 1).replaceAll("\\$" + t, s));
                    }
                    a[0] = sb.toString();
                }
            });

            if (!have[0]) {
                orig = orig.substring(0, l) + orig.substring(r);
            } else {
                orig = orig.substring(0, l) + a[0] + orig.substring(r);
            }
        }

        return orig;
    }

    public static ArrayList<Source> getSourcesConfig() throws IOException, JSONException {
        if (SourceManager.getSources() == null) {
            SourceManager.readConfig();
        }
        return SourceManager.getSources();
    }

    public static void listSources(Logger logger) throws IOException, JSONException {
        ArrayList<Source> sources = getSourcesConfig();
        if (sources == null) {
            logger.info("No sources found");
        } else {
            int a = 0;
            int b = 0;
            int c = 0;
            for (Source source : sources) {
                a = Math.max(a, source.getName().length());
                b = Math.max(b, source.getDescription().length());
                c = Math.max(c, source.getUrl().length());
            }
            logger.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", "Name", " | ", "Description", " | ", "URL");
            for (Source source : sources) {
                logger.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", source.getName(), " | ",
                        source.getDescription(), " | ", source.getUrl());
            }
        }
    }

    public static ArrayList<Result> fetchResult(Source s, String proxyHost, int proxyPort)
            throws UnsupportedReturnTypeException, IOException {
        ArrayList<Result> r;
        r = SourceFetcher.fetch(s, proxyHost, proxyPort);
        return r;
    }

    public static void startDownload(ArrayList<Result> r, String outputDir, Logger logger,
                                     boolean saveFullResult, boolean enableConsoleProgressBar, int maxThread) {
        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            logger.err("Can't create directory");
            return;
        }
        ThreadPoolExecutor tpe;
        DownloadResult[] rs = new DownloadResult[r.size()];
        if (maxThread == -1) {
            tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else {
            tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
        }

        for (int i = 0; i < r.size(); i++) {
            Result result = r.get(i);
            DownloadResult dr = new DownloadResult();
            dr.setResult(result);
            tpe.execute(() -> {
                try {
                    new DownloadUtil(1).download(result, outDir, dr, saveFullResult);
                } catch (Exception e) {
                    Main.logError(e);
                    dr.setStatus(DownloadStatus.FAILED);
                    dr.setErrorMessage(e.toString());
                }
            });
            rs[i] = dr;
        }

        startMonitoring(rs, tpe, enableConsoleProgressBar, logger);
    }

    public static void startDownloadWithResults(ArrayList<DownloadResult> r, String outputDir, Logger logger,
                                                boolean saveFullResult, boolean enableConsoleProgressBar, int maxThread, Runnable onUpdate) {
        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            logger.err("Can't create directory");
            return;
        }
        ThreadPoolExecutor tpe;
        if (maxThread == -1) {
            tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else {
            tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
        }

        for (DownloadResult downloadResult : r) {
            tpe.execute(() -> {
                try {
                    new DownloadUtil(1).download(downloadResult.getResult(), outDir, downloadResult, saveFullResult);
                } catch (Exception e) {
                    Main.logError(e);
                    downloadResult.setStatus(DownloadStatus.FAILED);
                    downloadResult.setErrorMessage(e.toString());
                }
            });
        }

        startMonitoring(r.toArray(new DownloadResult[]{}), tpe, enableConsoleProgressBar, logger, onUpdate);
    }

    public static void startMonitoring(DownloadResult[] result, ThreadPoolExecutor tpe,
                                       boolean enableConsoleProgressBar, Logger logger, Runnable onUpdate) {
        DownloadManager manager = new DownloadManager(result);
        Thread t = new Thread(() -> {
            int lastLength = 0;
            while (enableConsoleProgressBar && tpe.getCompletedTaskCount() != result.length) {
                String m = manager.toString();
                if (Main.isDebug()) {
                    m += "  Queue:" + tpe.getQueue().size() + " Active:" + tpe.getActiveCount() + " Pool Size:"
                            + tpe.getPoolSize() + " Done:" + tpe.getCompletedTaskCount();
                }
                logger.printr(m.concat(" ".repeat(Math.max(0, lastLength - m.length()))));
                lastLength = m.length();
                onUpdate.run();

                try {
                    // noinspection BusyWait
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Main.logError(e);
                    e.printStackTrace();
                }
            }
            String m = manager.toString();
            if (Main.isDebug() && tpe != null) {
                m += "  Queue:" + tpe.getQueue().size() + " Active:" + tpe.getActiveCount() + " Pool Size:"
                        + tpe.getPoolSize();
            }
            logger.printr(m.concat(" ".repeat(Math.max(0, lastLength - m.length()))).concat("\n"));
            logger.info("Done");
            onUpdate.run();

            if (tpe != null) {
                tpe.shutdown();
            }

            printResult(result, logger);
        });
        t.setPriority(5);
        t.start();
    }

    public static void startMonitoring(DownloadResult[] result, ThreadPoolExecutor tpe,
                                       boolean enableConsoleProgressBar, Logger logger) {
        DownloadManager manager = new DownloadManager(result);
        Thread t = new Thread(() -> {
            int lastLength = 0;
            while (enableConsoleProgressBar && tpe.getCompletedTaskCount() != result.length) {
                String m = manager.toString();
                if (Main.isDebug()) {
                    m += "  Queue:" + tpe.getQueue().size() + " Active:" + tpe.getActiveCount() + " Pool Size:"
                            + tpe.getPoolSize() + " Done:" + tpe.getCompletedTaskCount();
                }
                logger.printr(m.concat(" ".repeat(Math.max(0, lastLength - m.length()))));
                lastLength = m.length();

                try {
                    // noinspection BusyWait
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Main.logError(e);
                    e.printStackTrace();
                }
            }
            String m = manager.toString();
            if (Main.isDebug() && tpe != null) {
                m += "  Queue:" + tpe.getQueue().size() + " Active:" + tpe.getActiveCount() + " Pool Size:"
                        + tpe.getPoolSize();
            }
            logger.printr(m.concat(" ".repeat(Math.max(0, lastLength - m.length()))).concat("\n"));
            logger.info("Done");

            if (tpe != null) {
                tpe.shutdown();
            }

            printResult(result, logger);
        });
        t.setPriority(5);
        t.start();
    }

    public static void printResult(DownloadResult[] r, Logger logger) {
        HashMap<String, String> mapFailed = new HashMap<>();

        for (DownloadResult downloadResult : r) {
            String fileName = downloadResult.getResult().getFileName();
            String url = downloadResult.getResult().getUrl();
            if (downloadResult.getStatus() != DownloadStatus.COMPLETED) {
                mapFailed.put(fileName, url);
            }
        }

        if (!mapFailed.isEmpty()) {
            logger.info("Failed:");
            mapFailed.forEach((s, s2) -> logger.info(s + " : " + s2));
        }
    }

    public static ArrayList<Result> fetch(Source s, int times, Logger logger, boolean enableConsoleProgressBar,
                                          String proxyHost, int proxyPort) {
        logger.info("Fetching pictures from " + s.getUrl() + " ...");

        ArrayList<Result> r = new ArrayList<>();

        int failed = 0;
        int lastLength = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("Fetching 0/").append(times);
        logger.printr(sb.toString());
        lastLength = printTaskBar(sb.toString(), 0, "", lastLength, logger);

        for (int i = 0; i < times; ) {
            if (times > 1 && enableConsoleProgressBar) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Main.logError(e);
                    e.printStackTrace();
                }
                sb = new StringBuilder();
                sb.append("Fetching ").append(i).append("/").append(times);
                if (failed != 0) {
                    sb.append(" Failed:").append(failed);
                }
                logger.printr(sb.toString());
                lastLength = printTaskBar(sb.toString(), (double) i / (double) times, "", lastLength, logger);
            }
            try {
                r.addAll(fetchResult(s, proxyHost, proxyPort));
            } catch (Exception e) {
                Main.logError(e);
                failed++;
                if (e instanceof HttpStatusException && ((HttpStatusException) e).getStatusCode() == 429) {
                    logger.printr("Error: rate limit exceeded \n");
                } else {
                    logger.printr("Error occurred:" + e.getMessage());
                }
            }
            i++;
        }

        sb = new StringBuilder();
        sb.append("Fetching ").append(times).append("/").append(times);
        if (failed != 0) {
            sb.append(" Failed:").append(failed);
        }
        logger.printr(sb.toString());
        printTaskBar(sb.toString(), 1, "\n", lastLength, logger);

        return r;
    }

    public static int printTaskBar(String prefix, double progress, String suffix, int minLength, Logger logger) {
        StringBuilder sb = new StringBuilder(prefix);
        int a = (int) (PROGRESS_BAR_LENGTH * progress);
        int b = PROGRESS_BAR_LENGTH - a;
        sb.append(" |").append("=".repeat(Math.min(
                        PROGRESS_BAR_LENGTH, a))).append(" ".repeat(Math.max(0, b)))
                .append("|");
        if (progress > 1 || progress < 0) {
            sb.append("...");
        } else {
            sb.append(df.format(progress));
        }

        sb.append(suffix);
        sb.append(" ".repeat(Math.max(0, minLength - sb.length())));
        logger.printr(sb.toString());
        return sb.length();
    }

    public static Source getSourceByName(String sourceName) throws IOException, JSONException, SourceNotFoundException {
        Source s;
        ArrayList<Source> sources = getSourcesConfig();
        if (sources == null) {
            throw new SourceNotFoundException("Could not find any sources to use");
        }
        s = SourceManager.getSourceByName(sources, sourceName);
        return s;
    }

    public static void replaceArgument(Source s, HashMap<String, String> arguments) {
        if (s == null) {
            return;
        }

        JSONObject temp = new JSONObject();
        temp.putAll(s.getDefaultArgs());

        temp.putAll(arguments);

        s.setUrl(replaceArgument(s.getUrl(), temp));
    }
}
