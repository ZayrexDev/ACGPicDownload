package xyz.zcraft.ACGPicDownload.Commands;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import xyz.zcraft.ACGPicDownload.Main;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil.DownloadManager;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil.DownloadResult;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil.DownloadStatus;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil.DownloadUtil;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.Source;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.SourceFetcher;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.SourceManager;
import xyz.zcraft.ACGPicDownload.Util.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Fetch {
    private final HashMap<String, String> arguments = new HashMap<>();
    private String sourceName;
    private String outputDir = new File("").getAbsolutePath();
    private boolean multiThread = false;

    private Logger logger;

    public boolean enableConsoleProgressBar = false;
    private static final DecimalFormat df = new DecimalFormat("#.##%");
    private int times = 1;

    private List<Source> getSourcesConfig() {
        try {
            if (SourceManager.getSources() == null) {
                SourceManager.readConfig();
            }
            return SourceManager.getSources();
        } catch (IOException e) {
            logger.err("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
        } catch (JSONException e) {
            logger.err(
                    "ERROR:Could not parse source config as JSON file. Please check if your sources.json is correctly configured. Error detail:"
                            + e);
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
                logger.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", source.getName(), " | ",
                        source.getDescription(), " | ", source.getUrl());
            }
        }
    }

    private boolean saveFullResult = false;

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
                case "--arg", "-a", "--args" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        String[] t = args.get(i + 1).split(",");
                        for (String s : t) {
                            String key = s.substring(0, s.indexOf("="));
                            String value = s.substring(s.indexOf("=") + 1);
                            if(value.startsWith("\"") && value.endsWith("\"")){
                                value = value.substring(1, value.length() - 1);
                            }
                            arguments.put(key, value);
                        }
                        i += 1;
                    } else {
                        logger.err("Please provide arguments.");
                    }
                }
                case "--multi-thread" -> multiThread = true;
                case "-f","--full" -> saveFullResult = true;
                case "--debug" -> Main.debugOn();
                case "--list-sources" -> {
                    listSources();
                    return;
                }
                case "-h", "--help" -> {
                    usage();
                    return;
                }
                case "-t", "--times" -> {
                    if (args.size() > (i + 1)) {
                        try {
                            times = Integer.parseInt(args.get(i + 1));
                            i++;
                            break;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    logger.err("Please enter a number");
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

    private List<Result> fetchResult(Source s) throws Exception {
        List<Result> r;
        r = SourceFetcher.fetch(s);
        return r;
    }

    public static String replaceArgument(String orig, JSONObject args) {
        if (orig == null) {
            return null;
        }

        int l;
        int r;

        while (((l = orig.indexOf("{")) != -1) && (r = orig.indexOf("}") + 1) != 0) {
            String[] a = {orig.substring(l, r)};
            boolean[] have = {false};

            args.forEach((t, o) -> {
                String value = null;

                if (args.containsKey(t)) {
                    value = String.valueOf(args.get(t));
                }

                if (a[0].contains("$" + t) && value != null) {
                    have[0] = true;
                    a[0] = a[0].substring(1, a[0].length() - 1).replaceAll("\\$" + t, value);
                }
            });

            if (!have[0]) {
                orig = orig.substring(0, l) + orig.substring(r);
            } else {
                orig = orig.substring(0, l) + a[0] + orig.substring(r);
            }
        }

        return orig;
    }

    private void startDownload(List<Result> r) {
        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            logger.err("Can't create directory");
            return;
        }
        ThreadPoolExecutor tpe;
        DownloadResult[] rs = new DownloadResult[r.size()];
        if (multiThread) {
            tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else {
            tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        }

        for (int i = 0; i < r.size(); i++) {
            Result result = r.get(i);
            DownloadResult dr = new DownloadResult();
            dr.setResult(result);
            tpe.execute(() -> {
                try {
                    new DownloadUtil(1).download(result, outDir, dr, saveFullResult);
                } catch (Exception e) {
                    if (enableConsoleProgressBar) {
                        dr.setStatus(DownloadStatus.FAILED);
                        dr.setErrorMessage(e.toString());
                    }
                }
            });
            rs[i] = dr;
        }

        startMonitoring(rs, tpe);
    }

    private void startMonitoring(DownloadResult[] result, ThreadPoolExecutor tpe) {
        DownloadManager manager = new DownloadManager(result);
        Thread t = new Thread(() -> {
            int lastLength = 0;
            while (enableConsoleProgressBar && !manager.done()) {
                String m = manager.toString();
                if (Main.isDebug() && tpe != null) {
                    m += "  Queue:" + tpe.getQueue().size() + " Active:" + tpe.getActiveCount() + " Pool Size:" + tpe.getPoolSize() + " Done:" + tpe.getCompletedTaskCount();
                }
                logger.printr(m.concat(" ".repeat(Math.max(0, lastLength - m.length()))));
                lastLength = m.length();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String m = manager.toString();
            if (Main.isDebug() && tpe != null) {
                m += "  Queue:" + tpe.getQueue().size() + " Active:" + tpe.getActiveCount() + " Pool Size:" + tpe.getPoolSize();
            }
            logger.printr(m.concat(" ".repeat(Math.max(0, lastLength - m.length()))).concat("\n"));
            logger.info("Done");

            if (tpe != null) {
                tpe.shutdown();
            }

            printResult(result);
        });
        t.setPriority(5);
        t.start();
    }

    private void printResult(DownloadResult[] r) {
        HashMap<String, String> mapFailed = new HashMap<>();

        for (DownloadResult downloadResult : r) {
            String fileName = downloadResult.getResult().getFileName();
            String url = downloadResult.getResult().getUrl();
            if (downloadResult.getStatus() != DownloadStatus.COMPLETED) {
                mapFailed.put(fileName, url);
            }
        }

        if (!mapFailed.isEmpty()) {
            logger.info("Failed:");
            mapFailed.forEach((s, s2) -> logger.info(s + " : " + s2));
        }
    }

    private void execute() {
        Source s;
        List<Source> sources = getSourcesConfig();
        if (sources == null) {
            logger.err("can't find source to use");
            return;
        }
        s = SourceManager.getSourceByName(sources, sourceName);
        if (s != null) {
            replaceArgument(s);

            logger.info("Fetching pictures from " + s.getUrl() + " ...");

            List<Result> r = new ArrayList<>();

            int failed = 0;
            int lastLength = 0;
            for (int i = 0; i < times; ) {
                if (times > 1 && enableConsoleProgressBar) {
                    StringBuilder sb = new StringBuilder();
                    double p = (double) i / (double) times;
                    int a = (int) (20 * p);
                    int b = 20 - a;
                    sb.append("Fetching ").append(i).append("/").append(times);
                    if (failed != 0) {
                        sb.append(" Failed:").append(failed);
                    }
                    sb.append(" |").append("=".repeat(a)).append(" ".repeat(b)).append("|").append(df.format(p));
                    logger.printr(sb.toString());
                    lastLength = sb.length();
                }
                try {
                    r.addAll(fetchResult(s));
                } catch (Exception e) {
                    failed++;
                    if (!enableConsoleProgressBar) {
                        logger.err("ERROR:Could not fetch. Error detail:" + e);
                    } else {
                        logger.printr("ERROR:" + e + "\n");
                    }
                }
                i++;
                if (times > 1 && enableConsoleProgressBar) {
                    StringBuilder sb = new StringBuilder();
                    double p = (double) i / (double) times;
                    int a = (int) (20 * p);
                    int b = 20 - a;
                    sb.append("Fetching ").append(i).append("/").append(times);
                    if (failed != 0) {
                        sb.append(" Failed:").append(failed);
                    }
                    sb.append(" |").append("=".repeat(a)).append(" ".repeat(b)).append("|").append(df.format(p));
                    logger.printr(sb.toString());
                    lastLength = sb.length();
                }
            }

            if (times > 1 && enableConsoleProgressBar) {
                StringBuilder sb = new StringBuilder();
                sb.append("Fetching Done");
                if (failed != 0) {
                    sb.append(" Failed:").append(failed);
                }
                logger.printr(sb.append(" ".repeat(Math.max(0, lastLength - sb.length()))).toString());
            }

            if (r.size() == 0) {
                logger.info("No pictures were found!");
                return;
            }

            logger.info("Got " + r.size() + " pictures!");

            startDownload(r);
        } else {
            logger.err("Could not find source named " + sourceName
                    + ". Please check your sources.json file. To get all sources, use \"--list-sources\"");
        }
    }

    public void replaceArgument(Source s) {
        String orig = s.getUrl();
        if (orig == null) {
            return;
        }

        int l;
        int r;

        while(((l = orig.indexOf("{")) != -1) && (r = orig.indexOf("}") + 1) != 0){
            String[] a = { orig.substring(l, r) };
            boolean[] have = { false };

            s.getDefaultArgs().forEach((t, o) -> {
                String[] value;

                if (arguments.containsKey(t)) {
                    value = arguments.get(t).split("&");
                } else {
                    value = String.valueOf(s.getDefaultArgs().get(t)).split("&");
                }

                if (a[0].contains("$" + t) && value != null) {
                    have[0] = true;
                    String str = a[0].substring(1, a[0].length() - 1);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < value.length; i++) {
                        sb.append(str.replaceAll("\\$" + t, value[i]));
                        if(i != value.length - 1 && !str.startsWith("&")){
                            sb.append("&");
                        }
                    }
                    a[0] = sb.toString();
                }
            });

            if (!have[0]) {
                orig = orig.substring(0, l) + orig.substring(r);
            } else {
                orig = orig.substring(0, l) + a[0] + orig.substring(r);
            }
        }

        s.setUrl(orig);
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
                        """);
    }
}
