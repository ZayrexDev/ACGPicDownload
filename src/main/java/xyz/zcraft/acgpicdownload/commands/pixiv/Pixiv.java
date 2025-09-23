package xyz.zcraft.acgpicdownload.commands.pixiv;

import xyz.zcraft.acgpicdownload.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Pixiv {
    private String cookie = null;
    private String proxyHost = null;
    private int proxyPort = -1;

    public void revoke(ArrayList<String> argList, Logger logger) {
        for (int i = 0; i < argList.size(); i++) {
            switch (argList.get(i).toLowerCase()) {
                case "-c", "-cookie": {
                    if (argList.size() > i + 1) {
                        i++;
                        try {
                            cookie = Files.readString(Path.of(argList.get(i)));
                        } catch (IOException e) {
                            logger.err("Cannot read cookie file " + argList.get(i));
                            return;
                        }
                    } else {
                        logger.err("Please specify a cookie file");
                        return;
                    }
                    break;
                }

                case "-p", "-proxy": {
                    if (argList.size() > i + 1) {
                        i++;
                        try {
                            final String[] split = argList.get(i).split(":");
                            proxyHost = split[0];
                            proxyPort = Integer.parseInt(split[1]);

                            System.getProperties().put("proxySet", "true");
                            System.getProperties().put("proxyHost", proxyHost);
                            System.getProperties().put("proxyPort", String.valueOf(proxyPort));
                        } catch (Exception e) {
                            logger.err("Cannot parse proxy " + argList.get(i));
                        }
                    } else {
                        logger.err("Please specify a proxy");
                        return;
                    }
                    break;
                }

                case "discovery", "disc": {
                    new Discovery().revoke(argList.subList(i + 1, argList.size()), cookie, proxyHost, proxyPort, logger);
                    return;
                }

                case "download", "dl": {
                    new Download().revoke(argList.subList(i + 1, argList.size()), cookie, proxyHost, proxyPort, logger);
                    return;
                }
            }
        }
    }
}
