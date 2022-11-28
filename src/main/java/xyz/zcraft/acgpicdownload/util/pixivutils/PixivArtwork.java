package xyz.zcraft.acgpicdownload.util.pixivutils;

import java.util.Arrays;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;

public class PixivArtwork {
    /*
     * {
     * "id": "",
     * "title": "",
     * "illustType": 0,
     * "xRestrict": 0,
     * "restrict": 0,
     * "sl": 2,
     * "url": "",
     * "description": "",
     * "tags": [
     * "",
     * ""
     * ],
     * "userId": "",
     * "userName": "",
     * "width": 0,
     * "height": 0,
     * "pageCount": 0,
     * "isBookmarkable": true,
     * "bookmarkData": null,
     * "alt": "",
     * "titleCaptionTranslation": {
     * "workTitle": null,
     * "workCaption": null
     * },
     * "createDate": "",
     * "updateDate": "",
     * "isUnlisted": false,
     * "isMasked": false,
     * "urls": {
     * "250x250": "",
     * "360x360": "",
     * "540x540": ""
     * },
     * "profileImageUrl": "",
     * "aiType": 1
     * }
     */

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
    private String[] tags;
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


    @Override
    public String toString() {
        return "PixivArtwork [id=" + id + ", title=" + title + ", illustType=" + illustType + ", xRestrict=" + xRestrict
                + ", restrict=" + restrict + ", sl=" + sl + ", url=" + url + ", description=" + description + ", tags="
                + Arrays.toString(tags) + ", userId=" + userId + ", userName=" + userName + ", width=" + width
                + ", height=" + height + ", pageCount=" + pageCount + ", bookmarkable=" + bookmarkable
                + ", bookmarkData=" + bookmarkData + ", alt=" + alt + ", titleCaptionTranslation="
                + titleCaptionTranslation + ", createDate=" + createDate + ", updateDate=" + updateDate + ", unlisted="
                + unlisted + ", masked=" + masked + ", urls=" + urls + ", profileImageUrl=" + profileImageUrl
                + ", aiType=" + aiType + "]";
    }

    public PixivArtwork(String createDate) {
        this.createDate = createDate;
    }

    public PixivArtwork(String id, String title, int illustType, int xRestrict, int restrict, int sl, String url,
            String description, String[] tags, String userId, String userName, int width, int height, int pageCount,
            boolean bookmarkable, Object bookmarkData, String alt, JSONObject titleCaptionTranslation,
            String createDate, String updateDate, boolean unlisted, boolean masked, JSONObject urls,
            String profileImageUrl, int aiType) {
        this.id = id;
        this.title = title;
        this.illustType = illustType;
        this.xRestrict = xRestrict;
        this.restrict = restrict;
        this.sl = sl;
        this.url = url;
        this.description = description;
        this.tags = tags;
        this.userId = userId;
        this.userName = userName;
        this.width = width;
        this.height = height;
        this.pageCount = pageCount;
        this.bookmarkable = bookmarkable;
        this.bookmarkData = bookmarkData;
        this.alt = alt;
        this.titleCaptionTranslation = titleCaptionTranslation;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.unlisted = unlisted;
        this.masked = masked;
        this.urls = urls;
        this.profileImageUrl = profileImageUrl;
        this.aiType = aiType;
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

    public void setIllustType(int illustType) {
        this.illustType = illustType;
    }

    public int getxRestrict() {
        return xRestrict;
    }

    public void setxRestrict(int xRestrict) {
        this.xRestrict = xRestrict;
    }

    public int getRestrict() {
        return restrict;
    }

    public void setRestrict(int restrict) {
        this.restrict = restrict;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isBookmarkable() {
        return bookmarkable;
    }

    public void setBookmarkable(boolean bookmarkable) {
        this.bookmarkable = bookmarkable;
    }

    public Object getBookmarkData() {
        return bookmarkData;
    }

    public void setBookmarkData(Object bookmarkData) {
        this.bookmarkData = bookmarkData;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public JSONObject getTitleCaptionTranslation() {
        return titleCaptionTranslation;
    }

    public void setTitleCaptionTranslation(JSONObject titleCaptionTranslation) {
        this.titleCaptionTranslation = titleCaptionTranslation;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isUnlisted() {
        return unlisted;
    }

    public void setUnlisted(boolean unlisted) {
        this.unlisted = unlisted;
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    public JSONObject getUrls() {
        return urls;
    }

    public void setUrls(JSONObject urls) {
        this.urls = urls;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getAiType() {
        return aiType;
    }

    public void setAiType(int aiType) {
        this.aiType = aiType;
    }

}
