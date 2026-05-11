package xyz.zcraft.acgpicdownload.commands.pixiv.sub;

import com.alibaba.fastjson2.JSONArray;
import xyz.zcraft.acgpicdownload.commands.pixiv.Profile;
import xyz.zcraft.acgpicdownload.commands.pixiv.SubCommand;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.LinkedList; 

public class Load extends SubCommand {
    private String fileName;
    private boolean append = false;

    public List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) throws Exception {
        for (int i = 1; i < argList.size(); i++) {
            if (!argList.get(i).startsWith("-")) break;
            switch (argList.get(i).toLowerCase()) {
                case "-f", "-file" -> {
                    if (argList.size() > i + 1) {
                        i++;
                        this.fileName = argList.get(i);
                    } else {
                        out.err("Please specify file name");
                        throw new IllegalArgumentException("Please specify file name");
                    }
                }

                case "-a", "-append" -> append = true;
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
            if (append && previous != null && !previous.isEmpty()) {
                out.info("Appending to previous " + previous.size() + " artwork data, now total: " + (previous.size() + list.size()));
                var result = new LinkedList<PixivArtwork>();
                result.addAll(previous);
                result.addAll(list);
                return List.copyOf(result);
            } else {
                return list;
            }
        } catch (Exception e) {
            log.error("Error reading file", e);
            out.err("Error reading file: " + fileName);
            throw new Exception("Error reading file: " + fileName, e);
        }
    }
}
