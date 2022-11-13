package xyz.zcraft.ACGPicDownload.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class Config {
    private final HashMap<String, String> arg = new HashMap<>();
    private String sourceName;
    private String outDir;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getOutDir() {
        return Objects.requireNonNullElseGet(outDir, () -> new File("").getAbsolutePath());
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public HashMap<String, String> getArg() {
        return arg;
    }

    @Override
    public String toString() {
        return "Config{source=" + sourceName + ", out=" + outDir + ", arg=" + arg + "}";
    }
}
