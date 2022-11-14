package xyz.zcraft.ACGPicDownload.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUtil {
    public static void download(Result r, File toDic) throws IOException {
        if(!toDic.exists() && !toDic.mkdirs()) {
            throw new IOException("Can't create directory");
        }

        URL url = new URL(r.getUrl());
        URLConnection c = url.openConnection();
        InputStream is = c.getInputStream();

        FileOutputStream fos = new FileOutputStream(new File(toDic, r.getFileName()));

        System.out.print("Downloading " + r.getFileName() + " to " + toDic + " from " + r.getUrl() + "...");

        byte[] buffer = new byte[20480];
        int byteRead;
        while ((byteRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, byteRead);
        }

        is.close();
        fos.close();

        System.out.println("Done");
    }
}
