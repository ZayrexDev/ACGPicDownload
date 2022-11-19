package xyz.zcraft.ACGPicDownload.Commands;

import com.alibaba.fastjson2.JSONException;
import xyz.zcraft.ACGPicDownload.Exceptions.SourceNotFoundException;
import xyz.zcraft.ACGPicDownload.Main;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.FetchUtil;
import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;
import xyz.zcraft.ACGPicDownload.Util.Logger;
import xyz.zcraft.ACGPicDownload.Util.SourceUtil.Source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fetch {

    private final HashMap<String, String> arguments = new HashMap<>();
    public boolean enableConsoleProgressBar = false;
    private String sourceName;
    private String outputDir = new File("").getAbsolutePath();
    private Logger logger;
    private int maxThread = 1;
    private String proxyHost;
    private int proxyPort;
    private int times = 1;
    private boolean saveFullResult = false;

    private boolean parseArguments(ArrayList<String> args){
        for (int i = 0; i < args.size(); i++) {
            switch (args.get(i)) {
                case "-s", "--source" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        sourceName = args.get(i + 1);
                        i += 1;
                    } else {
                        logger.err("Please provide a source name.");
                        return false;
                    }
                }
                case "-o", "--output" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        outputDir = args.get(i + 1);
                        i += 1;
                    } else {
                        logger.err("Please provide a output path.");
                        return false;
                    }
                }
                case "--arg", "-a", "--args" -> {
                    if (args.size() > i + 1 && !args.get(i + 1).startsWith("-")) {
                        String[] t = args.get(i + 1).split(",");
                        for (String s : t) {
                            String key = s.substring(0, s.indexOf("="));
                            String value = s.substring(s.indexOf("=") + 1);
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            }
                            arguments.put(key, value);
                        }
                        i += 1;
                    } else {
                        logger.err("Please provide arguments.");
                        return false;
                    }
                }
                case "-f", "--full" -> saveFullResult = true;
                case "--debug" -> Main.debugOn();
                case "--list-sources" -> {
                    try {
                        FetchUtil.listSources(logger);
                    } catch (IOException e) {
                        logger.err("Error:Cannot read source.json");
                    }
                    return false;
                }
                case "-h", "--help" -> {
                    usage(logger);
                    return false;
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
                    return false;
                }
                case "-p", "--proxy" -> {
                    if (args.size() > (i + 1)) {
                        String[] str = args.get(i + 1).split(":");
                        if(str.length == 2){
                            proxyHost = str[0];
                            try{
                                proxyPort = Integer.parseInt(str[1]);
                                i++;
                                break;
                            }catch(NumberFormatException ignored){}
                        }
                    }
                    logger.err("Please provide a vaild proxy");
                    return false;
                }
                case "-m", "--max-thread" -> {
                    if (args.size() > (i + 1)) {
                        try {
                            maxThread = Integer.parseInt(args.get(i + 1));
                            i++;
                        } catch (NumberFormatException ignored) {
                            maxThread = -1;
                        }
                    }else{
                        maxThread = -1;
                    }
                    break;
                }
                default -> {
                    logger.err("Unknown argument " + args.get(i) + " . Please use -h to see usage.");
                    return false;
                }
            }
        }

        if (sourceName == null || sourceName.trim().equals("")) {
            List<Source> sources;
            try {
                sources = FetchUtil.getSourcesConfig();
            } catch (IOException e) {
                logger.err("Error:Cannot read source.json");
                throw new RuntimeException(e);
            }
            if (sources == null || sources.size() == 0) {
                logger.err("No available sources");
                return false;
            } else {
                sourceName = sources.get(0).getName();
            }
        }
        if (outputDir == null || outputDir.trim().equals("")) {
            outputDir = "";
        }
        return true;
    }

    private Source parseSource(){
        Source s;
        try {
            s = FetchUtil.getSourceByName(sourceName);
        } catch (IOException e) {
            logger.err("Could read sources.json");
            return null;
        }catch(JSONException e){
            logger.err("Could not prase sources.json");
            return null;
        }catch(SourceNotFoundException e){
            logger.err("Could not find source " + sourceName);
            return null;
        }
        if (s == null) {
            logger.err("Could not find source named " + sourceName
                    + ". Please check your sources.json file. To get all sources, use \"--list-sources\"");
            return null;
        }
        return s;
    }

    public void main(ArrayList<String> args, Logger logger) {
        this.logger = logger;

        if(!parseArguments(args)){
            return;
        }

        if(proxyHost != null && proxyPort != 0){
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", proxyHost);
            System.getProperties().put("proxyPort", String.valueOf(proxyPort));
        }

        Source s = parseSource();

        if (s == null) {
            return;
        }

        FetchUtil.replaceArgument(s, arguments);

        ArrayList<Result> r = FetchUtil.fetch(s, times, logger, enableConsoleProgressBar,proxyHost,proxyPort);
        if (r.size() == 0) {
            logger.info("No pictures were found!");
            return;
        } else {
            logger.info("Got " + r.size() + " pictures!");
        }

        FetchUtil.startDownload(r, outputDir, logger, saveFullResult, enableConsoleProgressBar, maxThread);
    }

    public static void usage(Logger logger) {
        logger.info(
                """
                                Available arguments:
                                   --list-sources : List all the sources.
                                   -s, --source <source name> : Set the source to use.
                                   -o, --output <output dictionary> : Set the output dictionary.
                                   --arg key1=value1,key2=value2,... : custom the argument in the url.
                                   --multi-thread : (Experimental) Enable multi thread download. May improve download speed.
                                   -t, --t <times> : Set the number of times to fetch.
                                   -f, --full : Download the json data together with the image.

                                See the documentation to get more information about usage
                        """);
    }
}
