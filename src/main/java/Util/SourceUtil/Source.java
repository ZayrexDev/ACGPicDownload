package Util.SourceUtil;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;

public class Source {
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "description")
    private String description;
    @JSONField(name = "url")
    private String url;
    @JSONField(name = "nameRule")
    private String nameRule;
    @JSONField(name = "picUrl")
    private String picUrl;
    @JSONField(name = "sourceKey")
    private String sourceKey;
    @JSONField(name = "asArray")
    private boolean asArray;
    @JSONField(name = "defaultArgs")
    private JSONObject defaultArgs;

    public Source(String name, String description, String url, String nameRule, String picUrl, String sourceKey,
                  boolean asArray, JSONObject defaultArgs) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.nameRule = nameRule;
        this.picUrl = picUrl;
        this.sourceKey = sourceKey;
        this.asArray = asArray;
        this.defaultArgs = defaultArgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNameRule() {
        return nameRule;
    }

    public void setNameRule(String nameRule) {
        this.nameRule = nameRule;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public boolean isAsArray() {
        return asArray;
    }

    public void setAsArray(boolean asArray) {
        this.asArray = asArray;
    }

    public JSONObject getDefaultArgs() {
        return defaultArgs;
    }

    public void setDefaultArgs(JSONObject defaultArgs) {
        this.defaultArgs = defaultArgs;
    }

    @Override
    public String toString() {
        return "Source [name=" + name + ", description=" + description + ", url=" + url + ", nameRule=" + nameRule
                + ", picUrl=" + picUrl + ", sourceKey=" + sourceKey + ", asArray=" + asArray + ", defaultArgs="
                + defaultArgs + "]";
    }

}
