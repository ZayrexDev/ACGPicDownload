package xyz.zcraft.acgpicdownload.commands.pixiv;

import com.alibaba.fastjson2.JSONArray;
import lombok.Getter;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Discovery {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Discovery.class);
    private final int threads = 5;

    private int mode = 0;
    private int count = 1;

    private boolean file = false;
    private String fileName;

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
                        logger.err("Please specify a mode");
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

                case "-f", "-file": {
                    if (argList.size() > i + 1) {
                        i++;
                        fileName = argList.get(i);
                        file = true;
                    } else {
                        logger.err("Please specify a mode");
                        return;
                    }

                    break;
                }
            }
        }

        logger.info("Ready to get discovery: mode=" + mode + ",count=" + count + ",proxy=" + proxyHost + ":" + proxyPort);

        if (file && fileName != null) {
            logger.info("Saving to file: " + fileName);
        }

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
        if (f.isError())
            System.out.println("\r\033[31m[      ERROR     ]\033[0m");
        else
            System.out.println("\r\033[32m[      DONE      ]\033[0m");
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

        if (file && fileName != null) {
            try {
                Files.writeString(Path.of(fileName),
                        JSONArray.toJSONString(
                                art.stream().flatMap(
                                        (Function<PixivArtwork, Stream<?>>) e -> Stream.of(e.getOrigJson())
                                ).toList()
                        )
                );
                logger.info("File written to " + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                logger.err("Error writing file: " + fileName);
            }
        } else {
            System.out.println("\033[1mDownloading...\033[0m");

            Download.startDownload(cookie, proxyHost, proxyPort, art, threads, "downloads");
        }

        System.out.print("\n\nDONE fetching discovery.");
        System.out.print("\033[?25h");

    }

//    private void startDownload(String cookie, String proxyHost, int proxyPort, List<PixivArtwork> art) {
//        AtomicInteger completed = new AtomicInteger(0);
//        final ArrayList<PixivDownload> cur = new ArrayList<>(count);
//        for (int i1 = 0; i1 < threads; i1++) cur.add(null);
//        final ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
//
//        final LinkedList<PixivDownload> err = new LinkedList<>();
//
//        art.forEach(e -> tpe.submit(new DownloadTask(threads, e, cur, err, tpe, completed, cookie, proxyHost, proxyPort)));
//
//        boolean first = true;
//        while (true) {
//            if (!first)
//                System.out.print("\033[" + (1 + threads) + "F");
//            first = false;
//            System.out.println(
//                    "\033[32mCompleted:" + completed + "/" + art.size()
//                            + (err.isEmpty() ? "" : " \033[31mError:" + err.size()) + "\033[0m"
//            );
//            for (int k = 0; k < threads; k++) {
//                System.out.print("\033[K");
//                if (cur.get(k) == null) {
//                    System.out.println("IDLE");
//                } else {
//                    PixivDownload dl = cur.get(k);
//                    System.out.print("[");
//                    int v = (int) (Math.floor(((double) dl.getProgress() / dl.getTotal()) * 16.0));
//                    for (int j = 0; j < v; j++) {
//                        System.out.print("=");
//                    }
//                    for (int j = 0; j < 16 - v; j++) {
//                        System.out.print(" ");
//                    }
//                    System.out.print("] " +
//                            dl.getArtwork().getId() + " \t" +
//                            dl.getProgress() + "/" + dl.getTotal() +
//                            (dl.getArtwork().getIllustType() == 2 ? " GIF" : "")
//                    );
//                    System.out.println();
//                }
//            }
//
//            if (completed.get() == art.size()) break;
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {

    /// /                throw new RuntimeException(e);
//            }
//        }
//        tpe.shutdown();
//    }
//
//    private record DownloadTask(int threads, PixivArtwork e, ArrayList<PixivDownload> cur,
//                                LinkedList<PixivDownload> err, ThreadPoolExecutor tpe,
//                                AtomicInteger completed, String cookie, String proxyHost,
//                                int proxyPort) implements Runnable {
//        @Override
//        public void run() {
//            int I = -1;
//            PixivDownload dl = null;
//            synchronized (cur) {
//                for (int i = 0; i < threads; i++) {
//                    if (cur.get(i) == null) {
//                        dl = new PixivDownload(e);
//                        cur.set(i, dl);
//                        I = i;
//                        break;
//                    }
//                }
//            }
//
//            if (dl == null) {
//                tpe.submit(new DownloadTask(threads, e, cur, err, tpe, completed, cookie, proxyHost, proxyPort));
//                return;
//            }
//
//            try {
//                new DownloadUtil(1).downloadPixiv(
//                        dl,
//                        Path.of("downloads").toFile(),
//                        cookie,
//                        new NamingRule("{$id}{_p$p}", 0, "{$id}"),
//                        false,
//                        proxyHost,
//                        proxyPort,
//                        ArtworkCondition.always()
//                );
//
//                synchronized (cur) {
//                    cur.set(I, null);
//                }
//
//                completed.incrementAndGet();
//            } catch (IOException ex) {
//                dl.setException(ex);
//                err.add(dl);
//            }
//        }
//    }

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
