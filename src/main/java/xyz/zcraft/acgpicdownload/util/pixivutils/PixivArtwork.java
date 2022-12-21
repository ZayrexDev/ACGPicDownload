package xyz.zcraft.acgpicdownload.util.pixivutils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import java.util.LinkedHashSet;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @JSONField(name = "bookmarkCount")
    private int bookmarkCount;
    @JSONField(name = "likeCount")
    private int likeCount;

    private LinkedHashSet<String> translatedTags = new LinkedHashSet<>();
    private String imageUrl;
    private GifData gifData;
    private From from;

    private JSONObject origJson;
    private String ranking;
    private String search;

    public String getTypeString() {
        if (illustType == 2) return ResourceBundleUtil.getString("fetch.pixiv.type.gif");
        else if (illustType == 1 || illustType == 0)
            return Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.type.illust")).concat("-" + pageCount);
        else return "?";
    }

    public String getFromString() {
        if(from == null) return null;
        if (from.equals(From.Ranking)) {
            return from.toString().concat(" ").concat(ranking);
        } else if (from.equals(From.Search)) {
            return from.toString().concat(" ").concat(search);
        } else {
            return from.toString();
        }
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
}
