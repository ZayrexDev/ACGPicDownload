package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUtil {
    public static void download(Result r, File toDic) throws IOException{
        toDic.mkdirs();

        URL url = new URL(r.getUrl());
        URLConnection c = url.openConnection();
        InputStream is = c.getInputStream();

        FileOutputStream fos = new FileOutputStream(new File(toDic, r.getFileName()));

        byte[] buffer = new byte[c.getContentLength()];
        int byteRead;
        while((byteRead = is.read(buffer)) != -1){
            fos.write(buffer, 0, byteRead);
        }

        is.close();
        fos.close();
    }
}
