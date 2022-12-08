package xyz.zcraft.acgpicdownload.util.pixivutils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.annotation.JSONField;

public class GifData {
    @JSONField(name = "src")
    private String src;
    @JSONField(name = "originalSrc")
    private String originalSrc;
    @JSONField(name = "mime_type")
    private String mime_type;
    @JSONField(name = "frames")
    private JSONArray origFrame;

    public GifData() {
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getOriginalSrc() {
        return originalSrc;
    }

    public void setOriginalSrc(String originalSrc) {
        this.originalSrc = originalSrc;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public JSONArray getOrigFrame() {
        return origFrame;
    }

    public void setOrigFrame(JSONArray origFrame) {
        this.origFrame = origFrame;
    }

    @Override
    public String toString() {
        return "GifData{" +
                "src='" + src + '\'' +
                ", originalSrc='" + originalSrc + '\'' +
                ", mime_type='" + mime_type + '\'' +
                ", origFrame=" + origFrame +
                '}';
    }
}
