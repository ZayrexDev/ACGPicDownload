package xyz.zcraft.acgpicdownload.commands.pixiv;

import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class Complete {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Complete.class);
    private static final Logger out = new Logger("Complete");

    private int threads = 2;
    private int retries = 5;

    public List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) throws Exception {
        for (int i = 1; i < argList.size(); i++) {
            switch (argList.get(i).toLowerCase()) {
                case "-t", "-thread" -> {
                    if (argList.size() > i + 1) {
                        i++;
                        this.threads = Integer.parseInt(argList.get(i));
                    } else {
                        out.err("Please specify a thread count");
                        throw new IllegalArgumentException("Please specify a thread count");
                    }

                }

                case "-r", "-retry" -> {
                    if (argList.size() > i + 1) {
                        i++;
                        this.retries = Integer.parseInt(argList.get(i));
                    } else {
                        out.err("Please specify time of retries");
                        throw new IllegalArgumentException("Please specify time of retries");
                    }

                }
            }
        }

        final List<PixivArtwork> result = new LinkedList<>();

        previous.stream().filter(p -> p.getLikeCount() != 0 || p.getBookmarkData() != null).forEach(result::add);
        final List<PixivArtwork> list = previous.stream().filter(p -> p.getLikeCount() == 0 && p.getBookmarkData() == null).toList();
        final List<PixivArtwork> failedList = new LinkedList<>();

        out.info("Got " + list.size() + " artworks to complete data.");

        AtomicInteger completed = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        try (ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads)) {
            list.forEach(t -> tpe.execute(() -> {
                int tries = 1;
                while (tries <= retries) {
                    try {
                        result.add(PixivFetchUtil.getArtwork(t.getId(), profile.cookie(), profile.proxyHost(), profile.proxyPort()));
                        completed.getAndIncrement();
                        return;
                    } catch (IOException e) {
                        tries++;
                    }
                }

                failedList.add(t);
                failed.getAndIncrement();
            }));

            while (completed.get() + failed.get() < list.size()) {
                System.out.print("\033[0K\033[32mCompleted: " + completed + "/" + list.size() +
                        (failed.get() == 0 ? "" : " \033[31mFailed: " + failed + "\033[32m") + "\033[0m\n");
                System.out.print("\033[0K[");
                int v = (int) (Math.floor(((double) (completed.get() + failed.get()) / list.size()) * 16.0));
                for (int j = 0; j < v; j++) System.out.print("=");
                for (int k = 0; k < (16 - v); k++) System.out.print(" ");
                System.out.print("]\n");
                Thread.sleep(1000);
                System.out.print("\033[2F");
            }
        }

        final String failedIds = failedList.stream()
                .flatMap((Function<PixivArtwork, Stream<?>>) pixivArtwork -> Stream.of(pixivArtwork.getId()))
                .toList().toString();

        if (failed.get() > 0) out.err("Failed to complete " + failed + " artworks: " + failedIds);
        else out.info("All artworks completed.");

        return result;
    }
}
