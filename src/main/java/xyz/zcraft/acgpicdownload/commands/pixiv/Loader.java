package xyz.zcraft.acgpicdownload.commands.pixiv;

import com.alibaba.fastjson2.JSONArray;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Loader {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Loader.class);
    private static final Logger out = new Logger("Loader");
    private String fileName;

    public List<PixivArtwork> invoke(List<String> argList) throws Exception {
        for (int i = 1; i < argList.size(); i++) {
            if (!argList.get(i).startsWith("-")) break;
            switch (argList.get(i).toLowerCase()) {
                case "-f", "-file": {
                    if (argList.size() > i + 1) {
                        i++;
                        this.fileName = argList.get(i);
                    } else {
                        out.err("Please specify file name");
                        throw new IllegalArgumentException("Please specify file name");
                    }

                    break;
                }
            }
        }

        if (fileName == null) {
            out.err("Please specify a file name to read.");
            throw new IllegalArgumentException("Please specify a file name to read.");
        }

        out.info("Reading artwork data from file: " + fileName);

        try {
            final String s = Files.readString(Path.of(fileName));
            final List<PixivArtwork> list = JSONArray.parseArray(s, PixivArtwork.class);

            out.info("Read " + list.size() + " artwork data from file: " + fileName);
            return list;
        } catch (Exception e) {
            log.error("Error reading file", e);
            out.err("Error reading file: " + fileName);
            throw new Exception("Error reading file: " + fileName, e);
        }
    }
}
