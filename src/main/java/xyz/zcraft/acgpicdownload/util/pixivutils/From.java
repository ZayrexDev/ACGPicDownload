package xyz.zcraft.acgpicdownload.util.pixivutils;

import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

public enum From {
    Follow,
    Recommend,
    RecommendUser,
    RecommendTag,
    Related,
    Spec,
    Discovery,
    Ranking,
    Search,
    User,
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
        } else if (this == Search) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.search");
        } else if (this == Spec) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.spec");
        } else if (this == User) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.user");
        } else if (this == Discovery) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.disc");
        } else if (this == Ranking) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.ranking");
        } else if (this == Related) {
            return ResourceBundleUtil.getString("fetch.pixiv.from.related");
        } else {
            return ResourceBundleUtil.getString("fetch.pixiv.from.other");
        }
    }
}
