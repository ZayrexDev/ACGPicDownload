package xyz.zcraft.ACGPicDownload;

import xyz.zcraft.ACGPicDownload.Commands.*;
import xyz.zcraft.ACGPicDownload.Util.Logger;

import java.io.*;
import java.util.*;

public class Main {
    private static boolean debug = false;
    public static PrintWriter out;

    public static boolean isDebug() {
        return debug;
    }

    public static void debugOn() {
        Main.debug = true;
        try {
            out = new PrintWriter("debug.log");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ArrayList<String> argList = new ArrayList<>(List.of(args));
        if (argList.size() == 0) {
            Fetch f = new Fetch();
            f.enableConsoleProgressBar = true;
            f.main(argList, new Logger("Fetch", System.out));
        } else if (argList.get(0).equalsIgnoreCase("fetch")) {
            argList.remove(0);
            Fetch f = new Fetch();
            f.enableConsoleProgressBar = true;
            f.main(argList, new Logger("Fetch", System.out));
        } else if (argList.get(0).equalsIgnoreCase("schedule")) {
            argList.remove(0);
            Schedule s = new Schedule();
            s.main(argList);
        } else {
            Fetch f = new Fetch();
            f.enableConsoleProgressBar = true;
            f.main(argList, new Logger("Fetch", System.out));
        }
    }
}
