package xyz.zcraft.acgpicdownload.util.sourceutil;

import java.util.ArrayList;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;

import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;

public class Source {
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

    @JSONField(name = "defaultArgs")
    private JSONObject defaultArgs = new JSONObject();

    @JSONField(name = "returnType")
    private String returnType;

    private ArrayList<Argument<? extends Object>> arguments;

    public Source() {}

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

    public JSONObject getDefaultArgs() {
        return defaultArgs;
    }

    public void setDefaultArgs(JSONObject defaultArgs) {
        this.defaultArgs = defaultArgs;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ArrayList<Argument<?>> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<Argument<?>> arguments) {
        this.arguments = arguments;
    }
}
