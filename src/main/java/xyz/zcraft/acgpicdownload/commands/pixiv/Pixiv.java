package xyz.zcraft.acgpicdownload.commands.pixiv;

import xyz.zcraft.acgpicdownload.commands.pixiv.sub.*;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Pixiv {
    private final List<String> subCommands = List.of(
            "download", "fetch", "save", "load", "complete", "filter", "add"
    );
    private final List<Integer> fragments = new LinkedList<>(List.of(1));
    private String cookie = null;
    private String proxyHost = null;
    private int proxyPort = -1;
    private List<PixivArtwork> previous;
    private boolean ignoreError = false;
    private Logger logger;
    private Profile profile;

    public void invoke(ArrayList<String> argList, Logger logger) {
        this.logger = logger;

        for (int i = 1; i < argList.size(); i++) {
            if (subCommands.contains(argList.get(i).toLowerCase())) {
                fragments.add(i);
            }
        }

        fragments.add(argList.size());

        for (int i = 0; i < fragments.size() - 1; i++) {
            if (i == 0) {
                if (parseArgs(argList, i)) return;
                profile = new Profile(cookie, proxyHost, proxyPort);
            } else {
                try {
                    executeSubCommand(argList, i);
                } catch (Exception e) {
                    logger.err("Error executing sub-command " + argList.get(fragments.get(i)) + ": " + e.getMessage());
                    if (ignoreError) {
                        logger.info("Ignoring error and continuing...");
                    } else {
                        logger.info("Terminating due to error.");
                        return;
                    }
                }
            }
        }
    }

    private boolean parseArgs(ArrayList<String> argList, int i) {
        for (int j = 1; j < fragments.get(i + 1); j++) {
            switch (argList.get(j).toLowerCase()) {
                case "-c", "-cookie" -> {
                    if (argList.size() > j + 1) {
                        j++;
                        try {
                            var p = argList.get(j);
                            if (p.startsWith("\"") && p.endsWith("\"")) p = p.substring(1, p.length() - 1);
                            cookie = Files.readString(Path.of(p));
                        } catch (IOException e) {
                            logger.err("Cannot read cookie file " + argList.get(j));
                            return true;
                        }
                    } else {
                        logger.err("Please specify a cookie file");
                        return true;
                    }
                }
                case "-p", "-proxy" -> {
                    if (argList.size() > j + 1) {
                        j++;
                        try {
                            final String[] split = argList.get(j).split(":");
                            proxyHost = split[0];
                            proxyPort = Integer.parseInt(split[1]);

                            System.getProperties().put("proxySet", "true");
                            System.getProperties().put("proxyHost", proxyHost);
                            System.getProperties().put("proxyPort", String.valueOf(proxyPort));
                        } catch (Exception e) {
                            logger.err("Cannot parse proxy " + argList.get(j));
                        }
                    } else {
                        logger.err("Please specify a proxy");
                        return true;
                    }
                }
                case "-i", "-ignore" -> ignoreError = true;
            }
        }
        return false;
    }

    private void executeSubCommand(ArrayList<String> argList, int i) throws Exception {
        final List<String> subArgs = argList.subList(fragments.get(i), fragments.get(i + 1));

        SubCommand subCommand = switch (argList.get(fragments.get(i)).toLowerCase()) {
            case "fetch" -> new Fetch();
            case "download" -> new Download();
            case "save" -> new Save();
            case "load" -> new Load();
            case "complete" -> new Complete();
            case "filter" -> new Filter();
            case "add" -> new Add();
            default -> {
                logger.err("Unknown sub-command: " + argList.get(fragments.get(i)));
                throw new IllegalArgumentException("Unknown sub-command: " + argList.get(fragments.get(i)));
            }
        };

        previous = subCommand.invoke(subArgs, profile, previous);
    }
}
