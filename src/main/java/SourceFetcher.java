import SourceUtil.Source;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SourceFetcher {
    public static Result[] fetch(Source source) throws IOException {
        //Get the json from source
        String s = Jsoup.connect(source.getUrl())
                .followRedirects(true)
                .ignoreContentType(true)
                .get()
                .ownText();

        JSONObject obj = JSONObject.parseObject(s);

        //Follow the pathToSource
        String[] pathToSource = source.getSourceKey().split("/");
        for (int i = 0; i < pathToSource.length - 1; i++) {
            obj = (JSONObject) obj.get(pathToSource[i]);
        }

        //Judge if to parse as array
        ArrayList<JSONObject> pics = new ArrayList<>();
        if(source.isAsArray()){
            JSONArray array = (JSONArray) obj.get(pathToSource[pathToSource.length - 1]);
            array.forEach(new Consumer<Object>() {
                @Override
                public void accept(Object o) {
                    pics.add((JSONObject) o);
                }
            });
        }else{
            pics.add((JSONObject) obj.get(pathToSource[pathToSource.length - 1]));
        }

        //For each of the json objects
        pics.forEach(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) {

            }
        });

        return null;
    }

    //Follow the path. Stop at the last path.
    private static JSONObject followPath(JSONObject obj, String path){
        JSONObject result = obj.clone();

        String[] pathToSource = path.split("/");
        for (int i = 0; i < pathToSource.length - 1; i++) {
            result = (JSONObject) obj.get(pathToSource[i]);
        }

        return result;
    }
}
