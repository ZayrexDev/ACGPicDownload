import java.io.File;
import java.io.IOException;

import Util.Config;
import Util.DownloadUtil;
import Util.Result;
import Util.SourceUtil.Source;
import Util.SourceUtil.SourceFetcher;
import Util.SourceUtil.SourceManager;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            usage();
        }

        Config cfg = new Config();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]){
                case "-s","--source":{
                    if(args.length > i + 1 && !args[i + 1].startsWith("-")){
                        cfg.setSourceName(args[i + 1]);
                    }else{
                        System.err.println("Please provide a source name.");
                    }
                    break;
                }
                case "-o","--output":{
                    if(args.length > i + 1 && !args[i + 1].startsWith("-")){
                        cfg.setOutDir(args[i + 1]);
                    }else{
                        System.err.println("Please provide a output path.");
                    }
                    break;
                }
                case "--arg":{
                    if(args.length > i + 1 && !args[i + 1].startsWith("-")){
                        String[] t = args[i + 1].split(",");
                        for (int j = 0; j < t.length; j++) {
                            String key = t[j].substring(0, t[j].indexOf("="));
                            String value = t[j].substring(t[j].indexOf("=") + 1);
                            cfg.getArg().put(key, value);
                        }
                    }else{
                        System.err.println("Please provide arguments.");
                    }
                    break;
                }
            }
        }

        execute(cfg);
    }

    private static void execute(Config cfg) throws IOException{
        Source s = SourceManager.getSourceByName(cfg.getSourceName());
        Result[] r = SourceFetcher.fetch(s);
        File outDir = new File(cfg.getOutDir());
        for (int index = 0; index < r.length; index++) {
            DownloadUtil.download(r[index], outDir);
        }
    }

    private static void usage() {
    }
}
