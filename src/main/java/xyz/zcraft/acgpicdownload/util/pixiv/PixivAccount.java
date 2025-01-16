package xyz.zcraft.acgpicdownload.util.pixiv;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PixivAccount {
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "id")
    private String id;
    @JSONField(name = "profileImg")
    private String profileImg;
    @JSONField(name = "cookie")
    private String cookie;

    private String token;
}
