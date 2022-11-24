package xyz.zcraft.acgpicdownload;

import xyz.zcraft.acgpicdownload.commands.Fetch;
import xyz.zcraft.acgpicdownload.commands.Schedule;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.Logger;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    private static boolean debug = false;
    public static PrintWriter debugOut;
    public static PrintStream log;
    public static PrintWriter err;
    public static boolean isDebug() {
        return debug;
    }

    public static void debugOn() {
        Main.debug = true;
        try {
            debugOut = new PrintWriter("debug.log");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logError(String message) {
        if (err == null) {
            try {
                err = new PrintWriter("error.log");
            } catch (FileNotFoundException ignored) {
            }
        }
        err.print("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
        err.println(message);
        err.flush();
    }

    public static void logError(Exception e) {
        if (err == null) {
            try {
                err = new PrintWriter("error.log");
            } catch (FileNotFoundException ignored) {
            }
        }
        err.print("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
        e.printStackTrace(err);
        err.flush();
    }

    public static void main(String[] args) {
        ArrayList<String> argList = new ArrayList<>(List.of(args));
        try {
            log = new PrintStream("out.log");
        } catch (FileNotFoundException ignored) {}
        if (argList.size() == 0) {
            GUI.main(args);
        } else if (argList.get(0).equalsIgnoreCase("fetch")) {
            argList.remove(0);
            Fetch f = new Fetch();
            f.enableConsoleProgressBar = true;
            f.main(argList, new Logger("Fetch", System.out));
        } else if (argList.get(0).equalsIgnoreCase("schedule")) {
            argList.remove(0);
            Schedule s = new Schedule();
            s.main(argList);
        }
    }
}
