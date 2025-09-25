package xyz.zcraft.acgpicdownload.commands.pixiv;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Saver {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Saver.class);
    private String fileName;
    private boolean format = false;

    public void invoke(List<String> argList, Logger logger, List<PixivArtwork> previous) {
        if (previous == null || previous.isEmpty()) {
            logger.warn("No artwork data to save.");
            return;
        }

        for (int i = 1; i < argList.size(); i++) {
            if (!argList.get(i).startsWith("-")) break;
            switch (argList.get(i).toLowerCase()) {
                case "-o", "-output": {
                    if (argList.size() > i + 1) {
                        i++;
                        this.fileName = argList.get(i);
                    } else {
                        logger.err("Please specify file name");
                        return;
                    }

                    break;
                }

                case "-f", "-format": {
                    format = true;
                    break;
                }
            }
        }

        if (fileName == null) {
            logger.err("Please specify a file name to save.");
            return;
        }

        logger.info("Saving " + previous.size() + " artwork data to file: " + fileName);

        try {
            Files.writeString(Path.of(fileName),
                    JSONArray.toJSONString(
                            previous.stream().flatMap(
                                    (Function<PixivArtwork, Stream<?>>) e -> Stream.of(e.getOrigJson())
                            ).toList()
                            , (format ? new JSONWriter.Feature[]{JSONWriter.Feature.PrettyFormat} : new JSONWriter.Feature[]{}))
            );
            logger.info("File written to " + fileName);
        } catch (Exception e) {
            log.error("Error writing file", e);
            logger.err("Error writing file: " + fileName);
        }
    }
}
