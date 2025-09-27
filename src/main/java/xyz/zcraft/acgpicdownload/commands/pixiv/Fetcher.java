package xyz.zcraft.acgpicdownload.commands.pixiv;

import lombok.Getter;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Fetcher {
    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Fetcher.class);
    private static final Logger out = new Logger("Fetcher");

    private ArtProvider ap;

    public List<PixivArtwork> invoke(List<String> argList, String cookie, String proxyHost, int proxyPort, Mode mode) {
        switch (mode) {
            case Discovery -> {
                int discMode = 0;
                int count = 1;

                for (int i = 1; i < argList.size(); i++) {
                    if (!argList.get(i).startsWith("-")) break;
                    switch (argList.get(i).toLowerCase()) {
                        case "-m", "-mode": {
                            if (argList.size() > i + 1) {
                                i++;
                                if (!List.of(PixivFetchUtil.DISCOVERY_MODES).contains(argList.get(i))) {
                                    out.err("Unknown mode " + argList.get(i));
                                    throw new IllegalArgumentException("Unknown mode " + argList.get(i));
                                }

                                discMode = argList.indexOf(argList.get(i));
                            } else {
                                out.err("Please specify a mode");
                                throw new IllegalArgumentException("Please specify a mode");
                            }
                            break;
                        }

                        case "-c", "-count": {
                            if (argList.size() > i + 1) {
                                i++;
                                final int c = Integer.parseInt(argList.get(i));
                                if (c < 0 || c > 100) {
                                    out.err("Count must be between 1 and 100.");
                                    throw new IllegalArgumentException("Count must be between 1 and 100");
                                }

                                count = c;
                            } else {
                                out.err("Please specify a number.");
                                throw new IllegalArgumentException("Please specify a number.");
                            }
                            break;
                        }
                    }
                }

                out.info("Ready to get discovery: mode=" + discMode + ",count=" + count +
                        (proxyHost != null ? ",proxy=" + proxyHost + ":" + proxyPort : ""));

                int finalCount = count;
                int finalDiscMode = discMode;
                ap = () -> PixivFetchUtil.getDiscovery(finalDiscMode, finalCount, cookie, proxyHost, proxyPort);
            }
            case User -> {
                // TODO
            }
            case Ranking -> {
                // TODO
            }
            case Search -> {
                // TODO
            }
        }

        var f = new Fetch(ap);

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
            out.err("Error getting " + mode + ": " + f.getException().getMessage());
            throw new RuntimeException(f.getException());
        }

        if (art == null || art.isEmpty()) {
            out.warn("No artworks found!");
            return new LinkedList<>();
        }

        System.out.println("\033[32mGot " + art.size() + " artworks\033[0m");
        System.out.println();

        System.out.print("\nDONE fetching " + mode + ". " + art.size() + " artworks found.\n");
        System.out.print("\033[?25h");

        return art;
    }

    public enum Mode {
        Discovery, User, Ranking, Search;

        @Override
        public String toString() {
            return switch (this) {
                case Discovery -> "discovery";
                case User -> "user artworks";
                case Ranking -> "ranking";
                case Search -> "search";
            };
        }
    }

    public interface ArtProvider {
        List<PixivArtwork> fetch() throws IOException;
    }

    private static class Fetch {
        private final ArtProvider ap;
        @Getter
        private Exception exception;
        @Getter
        private volatile boolean isDone = false;
        @Getter
        private volatile boolean isError = false;
        @Getter
        private List<PixivArtwork> result = null;

        public Fetch(ArtProvider ap) {
            this.ap = ap;
        }

        public void run() {
            new Thread(() -> {
                try {
                    setDone(ap.fetch());
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
