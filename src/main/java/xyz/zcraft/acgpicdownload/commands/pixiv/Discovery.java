package xyz.zcraft.acgpicdownload.commands.pixiv;

import lombok.Getter;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.dl.DownloadUtil;
import xyz.zcraft.acgpicdownload.util.pixiv.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Discovery {
    private final int threads = 5;
    private int mode = 0;
    private int count = 1;

    public void revoke(List<String> argList, String cookie, String proxyHost, int proxyPort, Logger logger) {
        for (int i = 0; i < argList.size(); i++) {
            switch (argList.get(i).toLowerCase()) {
                case "-m", "-mode": {
                    if (argList.size() > i + 1) {
                        i++;
                        if (!List.of(PixivFetchUtil.DISCOVERY_MODES).contains(argList.get(i))) {
                            logger.err("Unknown mode " + argList.get(i));
                            return;
                        }

                        mode = argList.indexOf(argList.get(i));
                    } else {
                        logger.err("Please specify at the mode");
                        return;
                    }
                    break;
                }

                case "-c", "-count": {
                    if (argList.size() > i + 1) {
                        i++;
                        final int c = Integer.parseInt(argList.get(i));
                        if (c < 0 || c > 50) {
                            logger.err("Count must be between 1 and 50.");
                            return;
                        }

                        count = c;
                    } else {
                        logger.err("Please specify a number.");
                        return;
                    }
                    break;
                }
            }
        }

        logger.info("Ready to get discovery: mode=" + mode + ",count=" + count + ",proxy=" + proxyHost + ":" + proxyPort);

        var f = new Fetch(mode, count, cookie, proxyHost, proxyPort);
        f.run();

        System.out.print("\033[?25l");
        System.out.println("Fetching...");
        System.out.print("[=               ]\033[2G");
        int d = 1, i = 2;
        while (!(f.isDone() || f.isError())) {
            if (i + d >= 18) d = -1;
            else if (i + d < 2) d = 1;
            i += d;
            System.out.print("\033[" + (i) + "G=");
            System.out.print("\033[" + (i - d) + "G ");
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("\r\033[32m[      DONE      ]");

        List<PixivArtwork> art = f.getResult();

        if (f.getException() != null) {
            logger.err("Error getting discovery: " + f.getException().getMessage());
            return;
        }

        if (art == null || art.isEmpty()) {
            logger.err("No artworks found!");
            return;
        }

        System.out.println("\033[32mGot " + count + " artworks\033[0m");
        System.out.println();
        System.out.println("\033[1mDownloading...\033[0m");

        AtomicInteger completed = new AtomicInteger(0);
        final ArrayList<PixivDownload> cur = new ArrayList<>(count);
        for (int i1 = 0; i1 < threads; i1++) cur.add(null);
        final ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

        final LinkedList<PixivDownload> err = new LinkedList<>();

        art.forEach(e -> tpe.submit(new DownloadTask(threads, e, cur, err, tpe, completed, cookie, proxyHost, proxyPort)));

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
                    System.out.print("] " + dl.getArtwork().getId());
                    System.out.println();
                }
            }

            if (completed.get() == art.size()) break;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
            }
        }
        System.out.println("Done fetching discovery.");
        System.out.print("\033[?25h");
        tpe.shutdown();
    }

    private record DownloadTask(int threads, PixivArtwork e, ArrayList<PixivDownload> cur,
                                LinkedList<PixivDownload> err, ThreadPoolExecutor tpe,
                                AtomicInteger completed, String cookie, String proxyHost,
                                int proxyPort) implements Runnable {
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
                tpe.submit(new DownloadTask(threads, e, cur, err, tpe, completed, cookie, proxyHost, proxyPort));
                return;
            }

            try {
                new DownloadUtil(1).downloadPixiv(
                        dl,
                        Path.of("downloads").toFile(),
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

    private static class Fetch {
        private final String cookie;
        private final String proxyHost;
        private final int proxyPort;
        private final int mode;
        private final int count;
        @Getter
        private Exception exception;
        @Getter
        private volatile boolean isDone = false;
        @Getter
        private volatile boolean isError = false;
        @Getter
        private List<PixivArtwork> result = null;

        public Fetch(int mode, int count, String cookie, String proxyHost, int proxyPort) {
            this.cookie = cookie;
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
            this.mode = mode;
            this.count = count;
        }

        public void run() {
            new Thread(() -> {
                try {
                    setDone(PixivFetchUtil.getDiscovery(mode, count, cookie, proxyHost, proxyPort));
                } catch (IOException e) {
                    setError(e);
                }
            }).start();
        }

        private void setError(Exception e) {
            isError = true;
            this.exception = e;
        }

        private void setDone(List<PixivArtwork> result) {
            isDone = true;
            this.result = result;
        }
    }
}
