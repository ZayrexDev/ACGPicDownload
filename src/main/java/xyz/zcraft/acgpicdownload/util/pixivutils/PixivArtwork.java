package xyz.zcraft.acgpicdownload.util.pixivutils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import java.util.LinkedHashSet;
import java.util.Objects;

public class PixivArtwork {
    @JSONField(name = "id")
    private String id;
    @JSONField(name = "title")
    private String title;
    @JSONField(name = "illustType")
    private int illustType;
    @JSONField(name = "xRestrict")
    private int xRestrict;
    @JSONField(name = "restrict")
    private int restrict;
    @JSONField(name = "sl")
    private int sl;
    @JSONField(name = "url")
    private String url;
    @JSONField(name = "description")
    private String description;
    @JSONField(name = "tags")
    private JSONArray originalTags;
    @JSONField(name = "userId")
    private String userId;
    @JSONField(name = "userName")
    private String userName;
    @JSONField(name = "width")
    private int width;
    @JSONField(name = "height")
    private int height;
    @JSONField(name = "pageCount")
    private int pageCount;
    @JSONField(name = "isBookmarkable")
    private boolean bookmarkable;
    @JSONField(name = "bookmarkData")
    private Object bookmarkData;
    @JSONField(name = "alt")
    private String alt;
    @JSONField(name = "titleCaptionTranslation")
    private JSONObject titleCaptionTranslation;
    @JSONField(name = "createDate")
    private String createDate;
    @JSONField(name = "updateDate")
    private String updateDate;
    @JSONField(name = "isUnlisted")
    private boolean unlisted;
    @JSONField(name = "isMasked")
    private boolean masked;
    @JSONField(name = "urls")
    private JSONObject urls;
    @JSONField(name = "profileImageUrl")
    private String profileImageUrl;
    @JSONField(name = "aiType")
    private int aiType;

    private LinkedHashSet<String> translatedTags = new LinkedHashSet<>();
    private String imageUrl;
    private GifData gifData;
    private From from;

    private JSONObject origJson;

    public JSONObject getOrigJson() {
        return origJson;
    }

    public void setOrigJson(JSONObject origJson) {
        this.origJson = origJson;
    }

    public PixivArtwork() {
    }
    public String getTypeString() {
        if (illustType == 2) return ResourceBundleUtil.getString("fetch.pixiv.type.gif");
        else if (illustType == 1 || illustType == 0)
            return Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.type.illust")).concat("-" + pageCount);
        else return "?";
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "PixivArtwork{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", illustType=" + illustType + ", xRestrict=" + xRestrict + ", restrict=" + restrict + ", sl=" + sl + ", url='" + url + '\'' + ", description='" + description + '\'' + ", originalTags=" + originalTags + ", userId='" + userId + '\'' + ", userName='" + userName + '\'' + ", width=" + width + ", height=" + height + ", pageCount=" + pageCount + ", bookmarkable=" + bookmarkable + ", bookmarkData=" + bookmarkData + ", alt='" + alt + '\'' + ", titleCaptionTranslation=" + titleCaptionTranslation + ", createDate='" + createDate + '\'' + ", updateDate='" + updateDate + '\'' + ", unlisted=" + unlisted + ", masked=" + masked + ", urls=" + urls + ", profileImageUrl='" + profileImageUrl + '\'' + ", aiType=" + aiType + '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIllustType() {
        return illustType;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONArray getOriginalTags() {
        return originalTags;
    }

    public String getTagsString() {
        if (translatedTags == null || translatedTags.size() == 0) {
            if (originalTags == null) return null;
            StringBuilder sb = new StringBuilder();
            for (Object tag : originalTags) {
                sb.append(tag);
                sb.append(",");
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object tag : translatedTags) {
                sb.append(tag);
                sb.append(",");
            }
            return sb.toString();
        }
    }
    public String getUserName() {
        return userName;
    }
    public LinkedHashSet<String> getTranslatedTags() {
        return translatedTags;
    }
    public void setTranslatedTags(LinkedHashSet<String> translatedTags) {
        this.translatedTags = translatedTags;
    }
    public void setGifData(GifData gifData) {
        this.gifData = gifData;
    }
}
