package xyz.zcraft.acgpicdownload.commands.pixiv.sub;


import lombok.Getter;
import xyz.zcraft.acgpicdownload.commands.pixiv.Profile;
import xyz.zcraft.acgpicdownload.commands.pixiv.SubCommand;
import xyz.zcraft.acgpicdownload.util.pixiv.From;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Fetch extends SubCommand {
    private ArtProvider ap;
    private boolean append = false;
    private static final int RANKING_MAX_RETRY_ATTEMPTS = 5;
    private static final int RANKING_RETRY_DELAY_MS = 2000;
    private static final int RELATED_ARTWORKS_LIMIT = 18;
    private static final String[][] RANKING_MAJORS = {
            {"daily", "daily_r18"}, {"weekly", "weekly_r18"},
            {"monthly"}, {"rookie"}, {"original"},
            {"daily_ai", "daily_r18_ai"}, {"male", "male_r18"}, {"female", "female_r18"}
    };
    private static final String[] SEARCH_SUFFIX = {
            "",
            "30000users入り",
            "20000users入り",
            "10000users入り",
            "5000users入り",
            "1000users入り",
            "500users入り",
            "300users入り",
            "100users入り",
            "50users入り"
    };

    public List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) {
        if (argList.size() < 2) {
            out.err("Please specify a fetch mode: -discovery, -user, -ranking, -search");
            throw new IllegalArgumentException("Please specify a fetch mode: -discovery, -user, -ranking, -search");
        }
        Mode mode = switch (argList.get(1).toLowerCase()) {
            case "-discovery" -> Mode.Discovery;
            case "-user" -> Mode.User;
            case "-ranking" -> Mode.Ranking;
            case "-search" -> Mode.Search;
            default -> {
                out.err("Unknown fetch mode: " + argList.getFirst());
                throw new IllegalArgumentException("Unknown fetch mode: " + argList.getFirst());
            }
        };
        switch (mode) {
            case Discovery -> {
                int discMode = 0;
                int count = 1;

                for (int i = 2; i < argList.size(); i++) {
                    if (!argList.get(i).startsWith("-")) break;
                    switch (argList.get(i).toLowerCase()) {
                        case "-m", "-mode" -> {
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
                        }
                        case "-c", "-count" -> {
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
                        }
                        case "-a", "-append" -> append = true;
                    }
                }

                out.info("Ready to get discovery: mode=" + discMode + ",count=" + count +
                        (profile.proxyHost() != null ? ",proxy=" + profile.proxyHost() + ":" + profile.proxyPort() : ""));

                int finalCount = count;
                int finalDiscMode = discMode;
                ap = () -> PixivFetchUtil.getDiscovery(finalDiscMode, finalCount, profile.cookie(), profile.proxyHost(), profile.proxyPort());
            }
            case User -> {
                String uid = null;
                int relatedDepth = 0;

                for (int i = 2; i < argList.size(); i++) {
                    if (!argList.get(i).startsWith("-")) {
                        if (uid == null) uid = argList.get(i);
                        continue;
                    }
                    switch (argList.get(i).toLowerCase()) {
                        case "-u", "-uid", "-user" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                uid = argList.get(i);
                            } else {
                                out.err("Please specify user uid");
                                throw new IllegalArgumentException("Please specify user uid");
                            }
                        }
                        case "-r", "-related" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                relatedDepth = Integer.parseInt(argList.get(i));
                                if (relatedDepth < 0) {
                                    out.err("Related depth must be >= 0");
                                    throw new IllegalArgumentException("Related depth must be >= 0");
                                }
                            } else {
                                out.err("Please specify related depth");
                                throw new IllegalArgumentException("Please specify related depth");
                            }
                        }
                        case "-a", "-append" -> append = true;
                    }
                }

                if (uid == null || uid.trim().isEmpty()) {
                    out.err("Please specify user uid");
                    throw new IllegalArgumentException("Please specify user uid");
                }
                uid = uid.trim();
                if (uid.startsWith("https://www.pixiv.net/users/")) {
                    uid = uid.substring(uid.lastIndexOf("/") + 1);
                }
                int splitIdx = uid.indexOf('?');
                if (splitIdx >= 0) uid = uid.substring(0, splitIdx);
                splitIdx = uid.indexOf('#');
                if (splitIdx >= 0) uid = uid.substring(0, splitIdx);
                final String finalUid = uid;
                final int finalRelatedDepth = relatedDepth;

                out.info("Ready to get user artworks: uid=" + finalUid + ",relatedDepth=" + finalRelatedDepth +
                        (profile.proxyHost() != null ? ",proxy=" + profile.proxyHost() + ":" + profile.proxyPort() : ""));

                ap = () -> {
                    Set<String> artIDs = PixivFetchUtil.fetchUser(finalUid, profile.proxyHost(), profile.proxyPort());
                    List<String> queryString = PixivFetchUtil.buildQueryString(artIDs);
                    LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();
                    for (String s : queryString) {
                        pixivArtworks.addAll(PixivFetchUtil.getUserArtworks(s, finalUid, profile.proxyHost(), profile.proxyPort()));
                    }
                    fetchRelatedArtworks(pixivArtworks, finalRelatedDepth, profile);
                    return pixivArtworks;
                };
            }
            case Ranking -> {
                String major = "daily";
                String minor = "";
                boolean r18 = false;

                for (int i = 2; i < argList.size(); i++) {
                    if (!argList.get(i).startsWith("-")) continue;
                    switch (argList.get(i).toLowerCase()) {
                        case "-major" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                major = argList.get(i).toLowerCase();
                            } else {
                                out.err("Please specify ranking major");
                                throw new IllegalArgumentException("Please specify ranking major");
                            }
                        }
                        case "-minor" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                minor = argList.get(i).toLowerCase();
                            } else {
                                out.err("Please specify ranking minor");
                                throw new IllegalArgumentException("Please specify ranking minor");
                            }
                        }
                        case "-r18" -> r18 = true;
                        case "-a", "-append" -> append = true;
                    }
                }

                int majorIdx = switch (major) {
                    case "daily" -> 0;
                    case "weekly" -> 1;
                    case "monthly" -> 2;
                    case "rookie" -> 3;
                    case "original" -> 4;
                    case "daily_ai" -> 5;
                    case "male" -> 6;
                    case "female" -> 7;
                    default -> -1;
                };
                if (majorIdx < 0) {
                    out.err("Unknown ranking major: " + major);
                    throw new IllegalArgumentException("Unknown ranking major: " + major);
                }
                if (r18 && RANKING_MAJORS[majorIdx].length == 1) {
                    out.err("Ranking major does not support r18: " + major + " (supported: daily, weekly, daily_ai, male, female)");
                    throw new IllegalArgumentException("Ranking major does not support r18: " + major + " (supported: daily, weekly, daily_ai, male, female)");
                }

                Set<String> allowedMinor = new HashSet<>(List.of(""));
                if (majorIdx == 0 || majorIdx == 1 || majorIdx == 2 || majorIdx == 3) {
                    allowedMinor.add("illust");
                    allowedMinor.add("manga");
                }
                if (majorIdx == 0 || majorIdx == 1) {
                    allowedMinor.add("ugoira");
                }
                if (!allowedMinor.contains(minor)) {
                    out.err("Invalid ranking minor " + minor + " for major " + major);
                    throw new IllegalArgumentException("Invalid ranking minor " + minor + " for major " + major);
                }

                final String finalMajor = RANKING_MAJORS[majorIdx][r18 ? 1 : 0];
                final String finalMinor = minor;
                final String rankingInfoPrefix = finalMajor + (finalMinor.isEmpty() ? "" : "-" + finalMinor);
                out.info("Ready to get ranking: major=" + finalMajor + ",minor=" + (finalMinor.isEmpty() ? "all" : finalMinor) +
                        (profile.proxyHost() != null ? ",proxy=" + profile.proxyHost() + ":" + profile.proxyPort() : ""));

                ap = () -> {
                    LinkedList<String> ids = PixivFetchUtil.getRankingIDs(finalMajor, finalMinor, profile.cookie(), profile.proxyHost(), profile.proxyPort());
                    LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();
                    int failed = 0;
                    for (int idx = 0; idx < ids.size(); idx++) {
                        boolean success = false;
                        for (int attempt = 1; attempt <= RANKING_MAX_RETRY_ATTEMPTS; attempt++) {
                            try {
                                PixivArtwork a = PixivFetchUtil.getArtwork(ids.get(idx), profile.cookie(), profile.proxyHost(), profile.proxyPort());
                                a.setFrom(From.Ranking);
                                a.setRanking(rankingInfoPrefix + "#" + (idx + 1));
                                pixivArtworks.add(a);
                                success = true;
                                break;
                            } catch (Exception e) {
                                if (attempt == RANKING_MAX_RETRY_ATTEMPTS) break;
                                try {
                                    Thread.sleep(RANKING_RETRY_DELAY_MS);
                                } catch (InterruptedException interruptedException) {
                                    out.warn("Ranking fetch interrupted during retry delay for artwork id=" + ids.get(idx));
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        }
                        if (!success) {
                            failed++;
                            out.warn("Failed to fetch ranking artwork id=" + ids.get(idx));
                        }
                    }
                    if (failed > 0) {
                        out.warn("Failed to fetch " + failed + " ranking artworks.");
                    }
                    return pixivArtworks;
                };
            }
            case Search -> {
                String keyword = null;
                String type = "top";
                int searchMode = 0;
                int page = 1;
                int relatedDepth = 0;
                String suffix = "";

                for (int i = 2; i < argList.size(); i++) {
                    if (!argList.get(i).startsWith("-")) {
                        if (keyword == null) keyword = argList.get(i);
                        continue;
                    }
                    switch (argList.get(i).toLowerCase()) {
                        case "-k", "-kw", "-keyword" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                keyword = argList.get(i);
                            } else {
                                out.err("Please specify search keyword");
                                throw new IllegalArgumentException("Please specify search keyword");
                            }
                        }
                        case "-t", "-type" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                type = argList.get(i).toLowerCase();
                            } else {
                                out.err("Please specify search type: top|illust|manga");
                                throw new IllegalArgumentException("Please specify search type: top|illust|manga");
                            }
                        }
                        case "-m", "-mode" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                String modeValue = argList.get(i).toLowerCase();
                                searchMode = switch (modeValue) {
                                    case "all" -> 0;
                                    case "safe" -> 1;
                                    case "r18", "adult" -> 2;
                                    default -> -1;
                                };
                                if (searchMode < 0) {
                                    out.err("Unknown search mode: " + modeValue);
                                    throw new IllegalArgumentException("Unknown search mode: " + modeValue);
                                }
                            } else {
                                out.err("Please specify search mode: all|safe|r18");
                                throw new IllegalArgumentException("Please specify search mode: all|safe|r18");
                            }
                        }
                        case "-p", "-page" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                page = Integer.parseInt(argList.get(i));
                                if (page < 1) {
                                    out.err("Page must be >= 1");
                                    throw new IllegalArgumentException("Page must be >= 1");
                                }
                            } else {
                                out.err("Please specify page number");
                                throw new IllegalArgumentException("Please specify page number");
                            }
                        }
                        case "-s", "-suffix" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                String suffixArg = argList.get(i);
                                try {
                                    int suffixIdx = Integer.parseInt(suffixArg);
                                    if (suffixIdx < 0 || suffixIdx >= SEARCH_SUFFIX.length) {
                                        out.err("Suffix index must be between 0 and " + (SEARCH_SUFFIX.length - 1));
                                        throw new IllegalArgumentException("Suffix index must be between 0 and " + (SEARCH_SUFFIX.length - 1));
                                    }
                                    suffix = SEARCH_SUFFIX[suffixIdx];
                                } catch (NumberFormatException ignored) {
                                    suffix = suffixArg;
                                    out.info("Using custom search suffix: " + suffix);
                                }
                            } else {
                                out.err("Please specify suffix or suffix index");
                                throw new IllegalArgumentException("Please specify suffix or suffix index");
                            }
                        }
                        case "-r", "-related" -> {
                            if (argList.size() > i + 1) {
                                i++;
                                relatedDepth = Integer.parseInt(argList.get(i));
                                if (relatedDepth < 0) {
                                    out.err("Related depth must be >= 0");
                                    throw new IllegalArgumentException("Related depth must be >= 0");
                                }
                            } else {
                                out.err("Please specify related depth");
                                throw new IllegalArgumentException("Please specify related depth");
                            }
                        }
                        case "-a", "-append" -> append = true;
                    }
                }

                if (keyword == null || keyword.trim().isEmpty()) {
                    out.err("Please specify search keyword");
                    throw new IllegalArgumentException("Please specify search keyword");
                }
                if (!List.of("top", "illust", "manga").contains(type)) {
                    out.err("Unknown search type: " + type);
                    throw new IllegalArgumentException("Unknown search type: " + type);
                }

                final String finalKeyword = keyword + suffix;
                final String finalType = type;
                final int finalSearchMode = searchMode;
                final int finalPage = page;
                final int finalRelatedDepth = relatedDepth;

                out.info("Ready to search: keyword=" + finalKeyword + ",type=" + finalType +
                        (finalType.equals("top") ? "" : ",mode=" + PixivFetchUtil.DISCOVERY_MODES[finalSearchMode] + ",page=" + finalPage) +
                        ",relatedDepth=" + finalRelatedDepth +
                        (profile.proxyHost() != null ? ",proxy=" + profile.proxyHost() + ":" + profile.proxyPort() : ""));

                ap = () -> {
                    LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();
                    switch (finalType) {
                        case "top" -> pixivArtworks.addAll(PixivFetchUtil.searchTopArtworks(finalKeyword, profile.cookie(), profile.proxyHost(), profile.proxyPort()));
                        case "illust" -> pixivArtworks.addAll(PixivFetchUtil.searchIllustArtworks(finalKeyword, finalSearchMode, finalPage, profile.cookie(), profile.proxyHost(), profile.proxyPort()));
                        case "manga" -> pixivArtworks.addAll(PixivFetchUtil.searchMangaArtworks(finalKeyword, finalSearchMode, finalPage, profile.cookie(), profile.proxyHost(), profile.proxyPort()));
                    }
                    fetchRelatedArtworks(pixivArtworks, finalRelatedDepth, profile);
                    return pixivArtworks;
                };
            }
        }

        var f = new Fetcher(ap);

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

        if (append && previous != null) {
            out.info("Appending to previous " + previous.size() + " artworks, total " + (previous.size() + art.size()) + " artworks.");
            LinkedList<PixivArtwork> combined = new LinkedList<>(previous);
            combined.addAll(art);
            return combined;
        } else {
            return art;
        }
    }

    private static void fetchRelatedArtworks(List<PixivArtwork> artworks, int depth, Profile profile) throws IOException {
        if (depth <= 0) return;
        List<PixivArtwork> currentLayer = new LinkedList<>(artworks);
        for (int i = 0; i < depth; i++) {
            List<PixivArtwork> nextLayer = new LinkedList<>();
            for (PixivArtwork artwork : currentLayer) {
                try {
                    nextLayer.addAll(PixivFetchUtil.getRelated(artwork, RELATED_ARTWORKS_LIMIT, profile.cookie(), profile.proxyHost(), profile.proxyPort()));
                } catch (IOException e) {
                    out.warn(String.format("Failed to fetch related artworks for id=%s: %s", artwork.getId(), e.getMessage()));
                }
            }
            artworks.addAll(nextLayer);
            currentLayer = nextLayer;
        }
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

    private static class Fetcher {
        private final ArtProvider ap;
        @Getter
        private Exception exception;
        @Getter
        private volatile boolean isDone = false;
        @Getter
        private volatile boolean isError = false;
        @Getter
        private List<PixivArtwork> result = null;

        public Fetcher(ArtProvider ap) {
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
