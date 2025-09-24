package xyz.zcraft.acgpicdownload.commands.pixiv;

import com.alibaba.fastjson2.JSONArray;
import lombok.Getter;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Discovery {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Discovery.class);

    private int mode = 0;
    private int count = 1;

    private boolean file = false;
    private String fileName;

    public List<PixivArtwork> invoke(List<String> argList, String cookie, String proxyHost, int proxyPort, Logger logger) {
        for (int i = 0; i < argList.size(); i++) {
            if (!argList.get(i).startsWith("-")) break;
            switch (argList.get(i).toLowerCase()) {
                case "-m", "-mode": {
                    if (argList.size() > i + 1) {
                        i++;
                        if (!List.of(PixivFetchUtil.DISCOVERY_MODES).contains(argList.get(i))) {
                            logger.err("Unknown mode " + argList.get(i));
                            return null;
                        }

                        mode = argList.indexOf(argList.get(i));
                    } else {
                        logger.err("Please specify a mode");
                        return null;
                    }
                    break;
                }

                case "-c", "-count": {
                    if (argList.size() > i + 1) {
                        i++;
                        final int c = Integer.parseInt(argList.get(i));
                        if (c < 0 || c > 50) {
                            logger.err("Count must be between 1 and 50.");
                            return null;
                        }

                        count = c;
                    } else {
                        logger.err("Please specify a number.");
                        return null;
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
                        return null;
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
                //noinspection BusyWait
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
            return null;
        }

        if (art == null || art.isEmpty()) {
            logger.err("No artworks found!");
            return new LinkedList<>();
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
                log.error("Error writing file", e);
                logger.err("Error writing file: " + fileName);
            }
        }

        System.out.print("\n\nDONE fetching discovery.");
        System.out.print("\033[?25h");

        return art;
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
