package Util;

import java.util.HashMap;

public class Config {
    private String sourceName;
    private String outDir;
    private final HashMap<String,String> arg = new HashMap<>();
    public String getSourceName() {
        return sourceName;
    }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getOutDir() {
        return outDir;
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
