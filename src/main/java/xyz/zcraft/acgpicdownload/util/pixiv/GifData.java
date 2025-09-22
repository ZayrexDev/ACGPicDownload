package xyz.zcraft.acgpicdownload.util.pixiv;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
