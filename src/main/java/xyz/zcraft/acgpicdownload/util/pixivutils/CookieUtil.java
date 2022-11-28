package xyz.zcraft.acgpicdownload.util.pixivutils;

import java.util.HashMap;

public class CookieUtil {
    public static HashMap<String,String> parseCookie(String cookieString){
        String[] t = cookieString.split(";");
        HashMap<String,String> cookieMap = new HashMap<String,String>();
        for (String t2 : t) {
            String[] t3 = t2.split("=");
            if(t3.length>=2){
                cookieMap.put(t3[0],t3[1]);
            }
        }
        return cookieMap;
    }
}
