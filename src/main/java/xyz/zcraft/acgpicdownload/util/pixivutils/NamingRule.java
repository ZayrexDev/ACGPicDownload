package xyz.zcraft.acgpicdownload.util.pixivutils;

import com.alibaba.fastjson2.JSONObject;
import xyz.zcraft.acgpicdownload.util.fetchutil.FetchUtil;

public record NamingRule(String rule, int multiP, String folderRule) {
    public String name(PixivArtwork artwork, int p) {
        JSONObject clone = artwork.getOrigJson().clone();
        if (p != -1) {
            clone.put("p", p);
        }
        return FetchUtil.replaceArgument(rule, clone);
    }

    public String name(PixivArtwork artwork) {
        return FetchUtil.replaceArgument(rule, artwork.getOrigJson());
    }

    public String nameFolder(PixivArtwork artwork) {
        return FetchUtil.replaceArgument(folderRule, artwork.getOrigJson());
    }
}
