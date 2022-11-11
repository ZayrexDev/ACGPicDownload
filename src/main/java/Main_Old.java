import PicSources.DmoeSource;
import PicSources.LoliconSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Main_Old {
    static final int CLEAN_MODE = 0;  // 0:移动 1:删除 其他值:禁用
    static final File SFW_DIC = new File("D:\\ProgramData\\pic\\SFW"); //下载sfw图片的目录
    static final File BACKUP_DIC = new File("D:\\ProgramData\\pic\\BACKUP"); //移动旧图片的目标目录
    static final File NSFW_DIC = new File("D:\\ProgramData\\pic\\NSFW"); //下载nsfw图片的目录
    static final boolean FETCH_NSFW = true; //是否获取nsfw图片

    public static void main(String[] args) throws InterruptedException {
        long waitMin = 5;
        while (true) {
            try {
                executeSFW();
                if (FETCH_NSFW) {
                    executeNSFW();
                }
                System.out.println("Request completed.");
                waitMin = 5;
            } catch (Exception e) {
                System.out.println();
                System.out.println("Request failed:" + e);
                e.printStackTrace();
                if(waitMin * 2 <= 30){
                    waitMin = waitMin * 2;
                }
            }
            System.out.println("Waiting " + waitMin + "min till next refresh...");
            Thread.sleep(waitMin * 60 * 1000);
        }
    }

    public static void executeNSFW() throws Exception {
        LoliconSource loliconSource = new LoliconSource();

        HashMap<String, String> URLs = new HashMap<>();

        System.out.print("Fetching from Lolicon(NSFW)...");
        loliconSource.analyze(loliconSource.fetch("r18=1&num=20")).forEach(stringStringHashMap -> URLs.put(loliconSource.getDownloadLink(stringStringHashMap), loliconSource.getFileName(stringStringHashMap)));

        System.out.println(URLs.size() + " links found!");

        System.out.println("Got " + URLs.size() + " fresh images!");

        int s = download(URLs, NSFW_DIC);

        clean(NSFW_DIC, s, 200);
    }

    public static void executeSFW() throws Exception {
        LoliconSource loliconSource = new LoliconSource();
        DmoeSource dmoeSource = new DmoeSource();

        HashMap<String, String> URLs = new HashMap<>();

        System.out.print("Fetching from Dmoe...");
        for (int i = 0; i < 10; i++) {
            String link = dmoeSource.fetch();
            URLs.put(link, link.substring(link.lastIndexOf("/") + 1));
            Thread.sleep(200);
        }
        System.out.println(URLs.size() + " links found!");
        int t = URLs.size();

        System.out.print("Fetching from Lolicon...");
        loliconSource.analyze(loliconSource.fetch()).forEach(stringStringHashMap -> URLs.put(loliconSource.getDownloadLink(stringStringHashMap), loliconSource.getFileName(stringStringHashMap)));

        System.out.println((URLs.size() - t) + " links found!");

        System.out.println("Got " + URLs.size() + " fresh images!");

        int s = download(URLs, SFW_DIC);

        clean(SFW_DIC, s, 200);
    }

    public static int download(HashMap<String, String> URLs, File toDic) {
        if (!toDic.exists()) {
            toDic.mkdir();
        }

        final Integer[] success = {0};

        URLs.forEach((s, s2) -> {
            System.out.print("-Downloading " + s2 + " ...");
            File file = new File(toDic, s2);
            if (file.exists()) {
                return;
            }
            try {
                file.createNewFile();

                int byteread;

                URLConnection connection = new URL(s).openConnection();
                InputStream inStream = connection.getInputStream();
                FileOutputStream fs = new FileOutputStream(file);

                byte[] buffer = new byte[10240];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
                fs.close();

                success[0]++;
            } catch (Exception e) {
                System.err.println("ERROR:" + e);
                file.delete();
            }

            System.out.println("Done");
        });

        return success[0];
    }

    public static void clean(File dic, int count, int minCount) throws IOException {
        if (CLEAN_MODE != 0 && CLEAN_MODE != 1) {
            System.out.println("Cleaning is disabled.");
            return;
        }

        File[] files = dic.listFiles();
        if (files.length >= minCount) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long l = o1.lastModified() - o2.lastModified();
                    if (l < 0) {
                        return -1;
                    } else if (l > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            File moveDic = new File(BACKUP_DIC, dic.getName());
            for (int i = 0; i < count; i++) {
                System.out.print("Cleaning " + files[i] + " ...");
                if (CLEAN_MODE == 0 && files[i].length() > 0) {
                    File f = new File(moveDic, files[i].getName());
                    System.out.print("Moving to " + f + "...");
                    Files.copy(files[i].toPath(), new FileOutputStream(f));
                    System.out.print("Moved...");
                }
                if (files[i].delete()) {
                    System.out.println("Done");
                } else {
                    System.err.println("ERROR");
                }
            }
        }
    }
}
