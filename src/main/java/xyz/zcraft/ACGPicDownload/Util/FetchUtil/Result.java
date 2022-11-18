package xyz.zcraft.ACGPicDownload.Util.FetchUtil;

import com.alibaba.fastjson2.JSONObject;

public class Result {
    private String fileName;
    private String url;
    private JSONObject json;

    public Result(String fileName, String url, JSONObject json) {
        this.fileName = fileName;
        this.url = url;
        this.json = json;
    }

    public Result() {
    }



    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Result{fileName=" + fileName + ", url=" + url + "}";
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }
}
