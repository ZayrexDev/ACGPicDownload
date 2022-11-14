package xyz.zcraft.ACGPicDownload;

import com.alibaba.fastjson.JSONException;
import xyz.zcraft.ACGPicDownload.Util.DownloadUtil;
import xyz.zcraft.ACGPicDownload.Util.Result;
import xyz.zcraft.ACGPicDownload.Util.SourceUtil.Source;
import xyz.zcraft.ACGPicDownload.Util.SourceUtil.SourceFetcher;
import xyz.zcraft.ACGPicDownload.Util.SourceUtil.SourceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static final HashMap<String, String> arguments = new HashMap<>();
    private static String sourceName;
    private static String outputDir = new File("").getAbsolutePath();
    private static boolean multiThread = false;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s", "--source" -> {
                    if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
                        sourceName = args[i + 1];
                        i += 1;
                    } else {
                        System.err.println("Please provide a source name.");
                    }
                }
                case "-o", "--output" -> {
                    if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
                        outputDir = args[i + 1];
                        i += 1;
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
                            arguments.put(key, value);
                        }
                        i += 1;
                    } else {
                        System.err.println("Please provide arguments.");
                    }
                }
                case "--multi-thread" -> {
                    multiThread = true;
                }
                case "--list-sources" ->{
                    listSources();
                    System.exit(0);
                }
                case "-h", "--help" -> {
                    usage();
                    System.exit(0);
                }
                default ->{
                    System.err.println("Unknow argument " + args[i] + " . Please use -h to see usage.");
                    System.exit(0);
                }
            }
        }

        if (sourceName == null || sourceName.trim().equals("")) {
            List<Source> sources = getSourcesConfig();
            if (sources == null || sources.size() == 0) {
                System.err.println("No available sources");
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

    private static List<Source> getSourcesConfig() {
        try {
            if (SourceManager.getSources() == null) {
                SourceManager.readConfig();
            }
            return SourceManager.getSources();
        } catch (IOException e) {
            System.err.println("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
        } catch (JSONException e) {
            System.err.println("ERROR:Could not parse source config as JSON file. Please check if your sources.json is correctly configured. Error detail:" + e);
        }

        return null;
    }

    private static void listSources() {
        List<Source> sources = getSourcesConfig();
        if (sources == null) {
            System.out.println("No sources found");
        }else{
            int a = 0;
            int b = 0;
            int c = 0;
            for (Source source : sources) {
                a = Math.max(a, source.getName().length());
                b = Math.max(b, source.getDescription().length());
                c = Math.max(c, source.getUrl().length());
            }
            System.out.printf("%-"+ a +"s %s %-"+ b +"s %s %-"+ c +"s","Name"," | ","Description"," | ","URL");
            System.out.println();
            for (Source source : sources) {
                System.out.printf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", source.getName(), " | ", source.getDescription(), " | ", source.getUrl());
                System.out.println();
            }
        }
    }

    private static void execute() {
        Source s;
        try {
            List<Source> sources = getSourcesConfig();
            if (sources == null) {
                System.err.println("can't find source to use");
                return;
            }
            s = SourceManager.getSourceByName(sources, sourceName);
        } catch (IOException e) {
            System.err.println("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
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

            System.out.println("Fetching pictures from " + s.getUrl() + "...");

            Result[] r;
            try {
                r = SourceFetcher.fetch(s);
            } catch (IOException e) {
                System.err.println("ERROR:Could not fetch. Error detail:" + e);
                return;
            }

            System.out.println("Got " + r.length + " pictures!");

            File outDir = new File(outputDir);
            if(!outDir.exists() && !outDir.mkdirs()) {
                System.err.println("Can't create directory");
                return;
            }
            for (Result result : r) {
                if(multiThread){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new DownloadUtil().download(result, outDir);
                            } catch (IOException e) {
                                System.err.println("ERROR:Failed to download " + result.getFileName() + " from " + result.getUrl() + " .Error detail:" + e);
                            }
                        }
                    }).start();
                }else{
                    try {
                        new DownloadUtil().download(result, outDir);
                    } catch (IOException e) {
                        System.err.println("ERROR:Failed to download " + result.getFileName() + " from " + result.getUrl() + " .Error detail:" + e);
                    }
                }
            }
        } else {
            System.err.println("Could not find source named " + sourceName + ". Please check your sources.json file. To get all sources, use \"--list-sources\"");
        }
    }

    private static void usage() {
        System.out.println(
                """
                        Available arguments:\s
                           --list-sources : List all the sources
                           -s, --source <source name> : Set the source to use. Required.
                           -o, --output <output dictionary> : Set the output dictionary. Required.
                           --arg key1=value1,key2=value2,... : custom the argument in the url.
                                   Example:If the url is "https://www.someurl.com/pic?num=${num}", then with
                                            "--arg num=1", the exact url will be "https://www.someurl.com/pic?num=1\"
                            --multi-thread : (Experimental) Enable multi thread download. May improve download speed.           
                """
        );
    }
}
