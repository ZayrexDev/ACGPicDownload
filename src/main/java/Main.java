import Util.Config;
import Util.DownloadUtil;
import Util.Result;
import Util.SourceUtil.Source;
import Util.SourceUtil.SourceFetcher;
import Util.SourceUtil.SourceManager;
import com.alibaba.fastjson.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        Config cfg = new Config();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s", "--source" -> {
                    if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
                        cfg.setSourceName(args[i + 1]);
                    } else {
                        System.err.println("Please provide a source name.");
                    }
                }
                case "-o", "--output" -> {
                    if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
                        cfg.setOutDir(args[i + 1]);
                    } else {
                        System.err.println("Please provide a output path.");
                    }
                }
                case "--arg" -> {
                    if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
                        String[] t = args[i + 1].split(",");
                        for (String s : t) {
                            String key = s.substring(0, s.indexOf("="));
                            String value = s.substring(s.indexOf("=") + 1);
                            cfg.getArg().put(key, value);
                        }
                    } else {
                        System.err.println("Please provide arguments.");
                    }
                }
                case "--list-sources" -> {
                    try {
                        List<Source> sources = SourceManager.parseFromConfig();
                        System.out.println("Name | Description | URL");
                        sources.forEach(t -> System.out.println(t.getName() + " | " + t.getDescription() + " | " + t.getUrl()));
                    } catch (IOException e) {
                        System.err.println("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
                    } catch (JSONException e) {
                        System.err.println("ERROR:Could not parse source config as JSON file. Please check if your sources.json is correctly configured. Error detail:" + e);
                    }
                    return;
                }
            }
        }

        if (cfg.getSourceName() == null) {
            System.err.println("Missing required value : Please enter a source name with \"-s\". To get all sources, use \"--list-sources\"");
            return;
        }
        if (cfg.getOutDir() == null) {
            System.err.println("Missing required value : Please enter a output path with \"-o\".");
            return;
        }
        execute(cfg);
    }

    private static void execute(Config cfg) {
        try {
            List<Source> sources = SourceManager.parseFromConfig();
            Source s = SourceManager.getSourceByName(sources, cfg.getSourceName());

            if (s != null) {
                s.getDefaultArgs().forEach((t, o) -> {
                    String value;

                    if (cfg.getArg().containsKey(t)) {
                        value = cfg.getArg().get(t);
                    } else {
                        value = String.valueOf(s.getDefaultArgs().get(t));
                    }

                    s.setUrl(s.getUrl().replaceAll("\\$\\{" + t + "}", value));
                });
                Result[] r = SourceFetcher.fetch(s);
                File outDir = new File(cfg.getOutDir());
                for (Result result : r) {
                    DownloadUtil.download(result, outDir);
                }
            } else {
                System.err.println("Could not find source named " + cfg.getSourceName() + ". Please check your sources.json file. To get all sources, use \"--list-sources\"");
            }
        } catch (IOException e) {
            System.err.println("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
        } catch (JSONException e) {
            System.err.println("ERROR:Could not parse source config as JSON file. Please check if your sources.json is correctly configured. Error detail:" + e);
        }
    }

    private static void usage() {
        System.out.println(
                """
                        Available arguments:\s
                           --list-sources : List all the sources
                         Fetching:
                           -s, --source <source name> : Set the source to use. Required.
                           -o, --output <output dictionary> : Set the output dictionary. Required.
                           --arg key1=value1,key2=value2,... : custom the argument in the url.
                                   Example:If the url is "https://www.someurl.com/pic?num=${num}", then with
                                            "--arg num=1", the exact url will be "https://www.someurl.com/pic?num=1\""""
        );
    }
}
