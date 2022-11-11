package PicSources;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoliconSource {
    public String fetch() throws Exception {
        return fetch("r18=0&num=20");
    }

    public String fetch(String arg) throws Exception {
        String d = Jsoup.connect("https://api.lolicon.app/setu/v2?" + arg)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.26")
                .get()
                .toString();
        return d;
    }

    public ArrayList<HashMap<String, String>> analyze(String source){
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        while (source.contains("\"original\":\"")) {
            HashMap<String,String> map = new HashMap<>();
            for (int i = 0; i < values.length; i++) {
                source = source.substring(source.indexOf("\"" + values[i] + "\":") + ("\"" + values[i] + "\":").length());
                String t = source.substring(0, source.contains(",") ? source.indexOf(",") : source.indexOf("}"));
                if(t.startsWith("\"") && t.endsWith("\"}}")){
                    t = t.substring(1,t.length() - 3);
                }
                if(t.startsWith("\"") && t.endsWith("\"")){
                    t = t.substring(1,t.length() - 1);
                }
                map.put(values[i],t);
            }
            list.add(map);
        }
        return list;
    }

    public String getDownloadLink(HashMap<String,String> map){
        return map.get("original");
    }

    private static final char[] ILLEGAL_CHARS = {'/','\\',':','*','?','<','>','|','\"'};
    public String getFileName(HashMap<String,String> map){
        StringBuilder sb = new StringBuilder();
        if(map.containsKey("pid")){
            sb.append("PID：").append(map.get("pid")).append(" ");
        }
        if(map.containsKey("title")){
            sb.append(map.get("title"));
        }
        if(map.containsKey("author")){
            sb.append(" by ").append(map.get("author"));
        }
        sb.append(".").append(map.get("ext"));
        String t = sb.toString();
        for (int i = 0; i < ILLEGAL_CHARS.length; i++) {
            t = t.replace(ILLEGAL_CHARS[i], '_');
        }
        return t;
    }

    private static String[] values = {"pid","p","uid","title","author","r18","width","height","ext","uploadDate","original"};
}
