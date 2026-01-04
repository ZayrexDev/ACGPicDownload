package xyz.zcraft.acgpicdownload.commands.pixiv.sub;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import xyz.zcraft.acgpicdownload.commands.pixiv.Profile;
import xyz.zcraft.acgpicdownload.commands.pixiv.SubCommand;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Save extends SubCommand {
    private String fileName;
    private boolean format = false;

    public List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) throws Exception {
        if (previous == null || previous.isEmpty()) {
            out.warn("No artwork data to save.");
            return previous;
        }

        for (int i = 1; i < argList.size(); i++) {
            if (!argList.get(i).startsWith("-")) break;
            switch (argList.get(i).toLowerCase()) {
                case "-o", "-output": {
                    if (argList.size() > i + 1) {
                        i++;
                        this.fileName = argList.get(i);
                    } else {
                        out.err("Please specify file name");
                        throw new IllegalArgumentException("Please specify file name");
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
            out.err("Please specify a file name to save.");
            throw new IllegalArgumentException("Please specify a file name to save.");
        }

        out.info("Saving " + previous.size() + " artwork data to file: " + fileName);

        try {
            Files.writeString(Path.of(fileName),
                    JSONArray.toJSONString(
                            previous.stream().flatMap(
                                    (Function<PixivArtwork, Stream<?>>) e -> Stream.of(e.getOrigJson())
                            ).toList()
                            , (format ? new JSONWriter.Feature[]{JSONWriter.Feature.PrettyFormat} : new JSONWriter.Feature[]{}))
            );
            out.info("File written to " + fileName);
        } catch (Exception e) {
            log.error("Error writing file", e);
            out.err("Error writing file: " + fileName);
            throw new Exception("Error writing file: " + fileName, e);
        }
        return previous;
    }
}
