package xyz.zcraft.acgpicdownload.util.fetch;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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


    @Override
    public String toString() {
        return "Result{fileName=" + fileName + ", url=" + url + "}";
    }

}
