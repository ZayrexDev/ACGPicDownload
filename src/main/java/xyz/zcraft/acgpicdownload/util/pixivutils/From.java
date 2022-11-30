package xyz.zcraft.acgpicdownload.util.pixivutils;

import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

public enum From {
    Follow,
    Recommend,
    RecommendUser,
    RecommendTag,
    Related,
    Other;

    @Override
    public String toString() {
        if (this == Follow) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.follow");
        } else if (this == Recommend) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.recommend");
        } else if (this == RecommendUser) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.recommendUser");
        } else if (this == RecommendTag) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.recommendTag");
        } else if (this == Related) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.related");
        } else {
            return ResourceBundleUtil.getString("fetch.pixiv.from.other");
        }
    }
}
