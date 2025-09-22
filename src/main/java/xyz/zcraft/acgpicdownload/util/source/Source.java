package xyz.zcraft.acgpicdownload.util.source;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import xyz.zcraft.acgpicdownload.util.source.argument.Argument;

import java.util.ArrayList;

@Setter
@Getter
public class Source implements Cloneable {
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "description")
    private String description = "";
    @JSONField(name = "url")
    private String url;
    @JSONField(name = "nameRule")
    private String nameRule = "";
    @JSONField(name = "picUrl")
    private String picUrl;
    @JSONField(name = "sourceKey")
    private String sourceKey = "";

    private ArrayList<Argument<?>> arguments;

    @JSONField(name = "defaultArgs")
    private JSONObject defaultArgs = new JSONObject();

    @JSONField(name = "returnType")
    private String returnType;

    public Source() {
    }

    public Source(String name, String description, String url, String nameRule, String picUrl, String sourceKey, JSONObject defaultArgs, String returnType) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.nameRule = nameRule;
        this.picUrl = picUrl;
        this.sourceKey = sourceKey;
        this.defaultArgs = defaultArgs;
        this.returnType = returnType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
