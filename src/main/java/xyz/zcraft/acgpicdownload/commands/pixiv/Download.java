package xyz.zcraft.acgpicdownload.commands.pixiv;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.dl.DownloadUtil;
import xyz.zcraft.acgpicdownload.util.pixiv.ArtworkCondition;
import xyz.zcraft.acgpicdownload.util.pixiv.NamingRule;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivDownload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Download {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Download.class);
    public int threads = 5;

    private String fileName = null;
    private String target = "downloads";

    private List<PixivArtwork> art;

    public static void startDownload(String cookie, String proxyHost, int proxyPort, List<PixivArtwork> art, int threads, String target) {
        AtomicInteger completed = new AtomicInteger(0);
        final ArrayList<PixivDownload> cur = new ArrayList<>(art.size());
        for (int i1 = 0; i1 < threads; i1++) cur.add(null);
        final ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

        final LinkedList<PixivDownload> err = new LinkedList<>();

        art.forEach(e -> tpe.submit(new DownloadTask(threads, e, cur, err, tpe, completed, cookie, proxyHost, proxyPort, target)));

        boolean first = true;
        while (true) {
            if (!first)
                System.out.print("\033[" + (1 + threads) + "F");
            first = false;
            System.out.println(
                    "\033[32mCompleted:" + completed + "/" + art.size()
                            + (err.isEmpty() ? "" : " \033[31mError:" + err.size()) + "\033[0m"
            );
            for (int k = 0; k < threads; k++) {
                System.out.print("\033[K");
                if (cur.get(k) == null) {
                    System.out.println("IDLE");
                } else {
                    PixivDownload dl = cur.get(k);
                    System.out.print("[");
                    int v = (int) (Math.floor(((double) dl.getProgress() / dl.getTotal()) * 16.0));
                    for (int j = 0; j < v; j++) {
                        System.out.print("=");
                    }
                    for (int j = 0; j < 16 - v; j++) {
                        System.out.print(" ");
                    }
                    System.out.print("] " +
                            dl.getArtwork().getId() + " \t" +
                            dl.getProgress() + "/" + dl.getTotal() +
                            (dl.getArtwork().getIllustType() == 2 ? " GIF" : "")
                    );
                    System.out.println();
                }
            }

            if (completed.get() == art.size()) break;

            try {
                //noinspection BusyWait
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        tpe.shutdown();
    }

    public void invoke(List<String> argList, String cookie,
                       String proxyHost, int proxyPort, Logger logger, List<PixivArtwork> previous) {
        for (int i = 1; i < argList.size(); i++) {
            if (!argList.get(i).startsWith("-")) break;
            switch (argList.get(i).toLowerCase()) {
                case "-f", "-file": {
                    if (argList.size() > i + 1) {
                        i++;
                        this.fileName = argList.get(i);
                    } else {
                        logger.err("Please specify file name");
                        return;
                    }

                    break;
                }

                case "-o", "-output": {
                    if (argList.size() > i + 1) {
                        i++;
                        this.target = argList.get(i);
                    } else {
                        logger.err("Please specify a output path");
                        return;
                    }

                    break;
                }

                case "-t", "-threads": {
                    if (argList.size() > i + 1) {
                        i++;
                        try {
                            this.threads = Integer.parseInt(argList.get(i));
                            if (this.threads < 1) {
                                logger.err("Threads must be at least 1");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            logger.err("Invalid number format: " + argList.get(i));
                            return;
                        }
                    } else {
                        logger.err("Please specify a number value");
                        return;
                    }

                    break;
                }
            }
        }

        if (fileName == null && previous == null) {
            logger.err("No artwork source to download!");
            return;
        }

        if (fileName != null && previous != null) {
            logger.warn("Both file and previous result are provided, using file");
        }

        if (fileName != null) {
            try {
                final String s = Files.readString(Path.of(fileName));
                final List<JSONObject> parse = JSONArray.parse(s).toList(JSONObject.class);
                art = new LinkedList<>();
                parse.forEach(e -> {
                    final var t = e.to(PixivArtwork.class);
                    t.setOrigJson(e);
                    art.add(t);
                });
            } catch (IOException e) {
                logger.err("Cannot read file " + fileName + ": " + e.getMessage());
                log.error("Cannot read file " + fileName, e);
                return;
            }
        } else {
            art = previous;
        }

        if (art.isEmpty()) {
            logger.warn("No artwork to download!");
            return;
        }

        logger.info(art.size() + " artworks to download!");

        startDownload(cookie, proxyHost, proxyPort, art, threads, target);

        logger.info("DONE Downloading");
    }

    private record DownloadTask(int threads, PixivArtwork e, ArrayList<PixivDownload> cur,
                                LinkedList<PixivDownload> err, ThreadPoolExecutor tpe,
                                AtomicInteger completed, String cookie, String proxyHost,
                                int proxyPort, String target) implements Runnable {
        @Override
        public void run() {
            int I = -1;
            PixivDownload dl = null;
            synchronized (cur) {
                for (int i = 0; i < threads; i++) {
                    if (cur.get(i) == null) {
                        dl = new PixivDownload(e);
                        cur.set(i, dl);
                        I = i;
                        break;
                    }
                }
            }

            if (dl == null) {
                tpe.submit(new DownloadTask(threads, e, cur, err, tpe, completed, cookie, proxyHost, proxyPort, target));
                return;
            }

            try {
                new DownloadUtil(1).downloadPixiv(
                        dl,
                        Path.of(target).toFile(),
                        cookie,
                        new NamingRule("{$id}{_p$p}", 0, "{$id}"),
                        false,
                        proxyHost,
                        proxyPort,
                        ArtworkCondition.always()
                );

                synchronized (cur) {
                    cur.set(I, null);
                }

                completed.incrementAndGet();
            } catch (IOException ex) {
                dl.setException(ex);
                err.add(dl);
            }
        }
    }
}
