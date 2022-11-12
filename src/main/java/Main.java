import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.alibaba.fastjson.JSONException;

import Util.Config;
import Util.DownloadUtil;
import Util.Result;
import Util.SourceUtil.Source;
import Util.SourceUtil.SourceFetcher;
import Util.SourceUtil.SourceManager;

public class Main {
    public static void main(String[] args){
        if(args.length == 0){
            usage();
            return;
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
                case "--list-sources":{
                    try {
                        List<Source> sources = SourceManager.parseFromConfig();
                        System.out.println("Name | Description | URL");
                        sources.forEach(new Consumer<Source>() {
                            @Override
                            public void accept(Source t) {
                                System.out.println(t.getName() + " | " + t.getDescription() + " | " + t.getUrl());
                            }
                        });
                    } catch (IOException e) {
                        System.err.println("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
                    }catch(JSONException e){
                        System.err.println("ERROR:Could not prase source config as JSON file. Please check if your sources.json is correctly configued. Error detail:" + e);
                    }
                    return;
                }
            }
        }

        if(cfg.getSourceName() == null){
            System.err.println("Missing required value : Please enter a source name with \"-s\". To get all sources, use \"--list-sources\"");
            return;
        }
        if(cfg.getOutDir() == null){
            System.err.println("Missing required value : Please enter a output path with \"-o\".");
            return;
        }
        execute(cfg);
    }

    private static void execute(Config cfg){
        try{
            List<Source> sources = SourceManager.parseFromConfig();
            Source s = SourceManager.getSourceByName(sources,cfg.getSourceName());
            
            if(s != null){
                s.getDefaultArgs().forEach(new BiConsumer<String,Object>() {
                    @Override
                    public void accept(String t, Object o) {
                        String value;
    
                        if(cfg.getArg().keySet().contains(t)){
                            value = cfg.getArg().get(t);
                        }else{
                            value = String.valueOf(s.getDefaultArgs().get(t));
                        }
    
                        s.setUrl(s.getUrl().replaceAll("\\$\\{" + t + "}", value));
                    }
                });
                Result[] r = SourceFetcher.fetch(s);
                File outDir = new File(cfg.getOutDir());
                for (int index = 0; index < r.length; index++) {
                    DownloadUtil.download(r[index], outDir);
                }
            }else{
                System.err.println("Could not find source named " + cfg.getSourceName() + ". Please check your sources.json file. To get all sources, use \"--list-sources\"");
            }
        }catch(IOException e){
            System.err.println("ERROR:Could not read source config. Please check your source config file. Error detail:" + e);
        }catch(JSONException e){
            System.err.println("ERROR:Could not prase source config as JSON file. Please check if your sources.json is correctly configued. Error detail:" + e);
        }
    }

    private static void usage() {
        System.out.println(
            "Avaliable arguments: \n"+
            "   --list-sources : List all the sources\n"+
            " Fetching:\n"+
            "   -s, --source <source name> : Set the source to use. Required.\n"+
            "   -o, --output <output dictionary> : Set the output dictionary. Required.\n"+
            "   --arg key1=value1,key2=value2,... : custom the argument in the url.\n"+
            "           Example:If the url is \"https://www.someurl.com/pic?num=${num}\", then with\n"+
            "                    \"--arg num=1\", the exact url will be \"https://www.someurl.com/pic?num=1\""
        );
    }
}
