package xyz.zcraft.acgpicdownload.commands.pixiv;

import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.util.LinkedList;
import java.util.List;

public class Filter {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Filter.class);
    private static final Logger out = new Logger("Filter");

    private int bookmark = -1;
    private int view = -1;
    private int like = -1;
    private boolean ignoreIncomplete = false;

    public List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) throws Exception {
        for (int i = 1; i < argList.size(); i++) {
            switch (argList.get(i).toLowerCase()) {
                case "-b", "-bookmark" -> {
                    if (argList.size() > i + 1) {
                        i++;
                        this.bookmark = Integer.parseInt(argList.get(i));
                    } else {
                        out.err("Please specify a bookmark count");
                        throw new IllegalArgumentException("Please specify a bookmark count");
                    }

                }
                case "-v", "-view" -> {
                    if (argList.size() > i + 1) {
                        i++;
                        this.view = Integer.parseInt(argList.get(i));
                    } else {
                        out.err("Please specify a view count");
                        throw new IllegalArgumentException("Please specify a view count");
                    }

                }
                case "-l", "-like" -> {
                    if (argList.size() > i + 1) {
                        i++;
                        this.like = Integer.parseInt(argList.get(i));
                    } else {
                        out.err("Please specify a like count");
                        throw new IllegalArgumentException("Please specify a like count");
                    }
                }
                case "-i" -> ignoreIncomplete = true;
            }
        }

        out.info("Got " + previous.size() + " artworks to filter with conditions:");
        if (like != -1) out.info(" - Minimum likes: " + like);
        if (bookmark != -1) out.info(" - Minimum bookmarks: " + bookmark);
        if (view != -1) out.info(" - Minimum views: " + view);
        if (ignoreIncomplete) out.info(" - Ignoring incomplete artworks");

        final List<PixivArtwork> result = new LinkedList<>();

        for (PixivArtwork p : previous) {
            if (p.getLikeCount() == 0 && p.getBookmarkData() == null) {
                if (!ignoreIncomplete) {
                    throw new IllegalArgumentException("Artwork " + p.getId() + " is incomplete, use -i to ignore.");
                }

                out.warn("Artwork " + p.getId() + " is incomplete, skipping.");
                continue;
            }

            if ((like != -1 && p.getLikeCount() < like)
                    || (bookmark != -1 && p.getBookmarkCount() < bookmark)
                    || (view != -1 && p.getViewCount() < view)) {
                continue;
            }

            result.add(p);
        }

        out.info("Filtered down to " + result.size() + " artworks. (" + (previous.size() - result.size()) + " filtered out)");

        return result;
    }
}
