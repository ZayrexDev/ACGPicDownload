package xyz.zcraft.ACGPicDownload.Commands;

import com.alibaba.fastjson.JSONException;

import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil.*;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.Source;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.SourceFetcher;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.SourceManager;
import xyz.zcraft.ACGPicDownload.Util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fetch {
    private final HashMap<String, String> arguments = new HashMap<>();
    private String sourceName;
    private String outputDir = new File("").getAbsolutePath();
    private boolean multiThread = false;

    private Logger logger;

    public boolean enableConsoleProgressBar = false;

    public void main(ArrayList<String> args, Logger logger) {
        this.logger = logger;
        for (int i = 0; i < args.size(); i++) {
            switch (args.get(i)) {
                case "-s", "--source" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        sourceName = args.get(i + 1);
                        i += 1;
                    } else {
                        logger.err("Please provide a source name.");
                    }
                }
                case "-o", "--output" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        outputDir = args.get(i + 1);
                        i += 1;
                    } else {
                        logger.err("Please provide a output path.");
                    }
                }
                case "--arg" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        String[] t = args.get(i + 1).split(",");
                        for (String s : t) {
                            String key = s.substring(0, s.indexOf("="));
                            String value = s.substring(s.indexOf("=") + 1);
                            arguments.put(key, value);
                        }
                        i += 1;
                    } else {
                        logger.err("Please provide arguments.");
                    }
                }
                case "--multi-thread" -> multiThread = true;
                case "--list-sources" -> {
                    listSources();
                    return;
                }
                case "-h", "--help" -> {
                    usage();
                    return;
                }
                default -> {
                    logger.err("Unknown argument " + args.get(i) + " . Please use -h to see usage.");
                    return;
                }
            }
        }

        if (sourceName == null || sourceName.trim().equals("")) {
            List<Source> sources = getSourcesConfig();
            if (sources == null || sources.size() == 0) {
                logger.err("No available sources");
                return;
            } else {
                sourceName = sources.get(0).getName();
            }
        }
        if (outputDir == null || outputDir.trim().equals("")) {
            outputDir = "";
        }
        execute();
    }

    private List<Source> getSourcesConfig() {
        try {
            if (SourceManager.getSources() == null) {
                SourceManager.readConfig();
            }
            return SourceManager.getSources();
        } catch (IOException e) {
            logger.err("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
        } catch (JSONException e) {
            logger.err("ERROR:Could not parse source config as JSON file. Please check if your sources.json is correctly configured. Error detail:" + e);
        }

        return null;
    }

    private void listSources() {
        List<Source> sources = getSourcesConfig();
        if (sources == null) {
            logger.info("No sources found");
        } else {
            int a = 0;
            int b = 0;
            int c = 0;
            for (Source source : sources) {
                a = Math.max(a, source.getName().length());
                b = Math.max(b, source.getDescription().length());
                c = Math.max(c, source.getUrl().length());
            }
            logger.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", "Name", " | ", "Description", " | ", "URL");
            for (Source source : sources) {
                logger.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", source.getName(), " | ", source.getDescription(), " | ", source.getUrl());
            }
        }
    }

    private void execute() {
        Source s;
        try {
            List<Source> sources = getSourcesConfig();
            if (sources == null) {
                logger.err("can't find source to use");
                return;
            }
            s = SourceManager.getSourceByName(sources, sourceName);
        } catch (IOException e) {
            logger.err("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
            return;
        }
        if (s != null) {
            s.getDefaultArgs().forEach((t, o) -> {
                String value;

                if (arguments.containsKey(t)) {
                    value = arguments.get(t);
                } else {
                    value = String.valueOf(s.getDefaultArgs().get(t));
                }

                s.setUrl(s.getUrl().replaceAll("\\$\\{" + t + "}", value));
            });

            logger.info("Fetching pictures from " + s.getUrl() + " ...");

            List<Result> r;
            try {
                r = SourceFetcher.fetch(s);
            } catch (Exception e) {
                logger.err("ERROR:Could not fetch. Error detail:" + e);
                return;
            }

            logger.info("Got " + r.size() + " pictures!");

            File outDir = new File(outputDir);
            if (!outDir.exists() && !outDir.mkdirs()) {
                logger.err("Can't create directory");
                return;
            }
            if(multiThread){
                DownloadManager manager;
                DownloadResult[] rs = new DownloadResult[r.size()];
                for (int index = 0; index < r.size(); index++) {
                    Result result = r.get(index);
                    DownloadResult dr = new DownloadResult();
                    new Thread(() -> {
                        try {
                            new DownloadUtil().download(result, outDir, dr);
                        } catch (IOException e) {
                            if(!enableConsoleProgressBar){
                                logger.err("ERROR:Failed to download " + result.getFileName() + " from " + result.getUrl() + " .Error detail:" + e);
                            }
                        }
                    }).start();
                    rs[index] = dr;
                }

                manager = new DownloadManager(rs);

                while(enableConsoleProgressBar && !manager.done()){
                    logger.rprint(manager.toString().concat("     "));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                }
            }else{
                for (int i = 0; i < r.size(); i++) {
                    Result result = r.get(i);
                    try {
                        logger.info("(" + i + " / " + r.size()  + ")Downloading " + result.getFileName() + " to " + outDir + " from " + result.getUrl() + " ...");
                        new DownloadUtil().download(result, outDir);
                    } catch (IOException e) {
                        logger.err("ERROR:Failed to download " + result.getFileName() + " from " + result.getUrl() + " .Error detail:" + e);
                    }
                }
            }
        } else {
            logger.err("Could not find source named " + sourceName + ". Please check your sources.json file. To get all sources, use \"--list-sources\"");
        }
    }

    private void usage() {
        logger.info(
                """
                                Available arguments:
                                   --list-sources : List all the sources
                                   -s, --source <source name> : Set the source to use. Required.
                                   -o, --output <output dictionary> : Set the output dictionary. Required.
                                   --arg key1=value1,key2=value2,... : custom the argument in the url.
                                           Example:If the url is "https://www.someurl.com/pic?num=${num}", then with
                                                    "--arg num=1", the exact url will be "https://www.someurl.com/pic?num=1"
                                   --multi-thread : (Experimental) Enable multi thread download. May improve download speed.
                        """
        );
    }
}
