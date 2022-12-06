package xyz.zcraft.acgpicdownload;

import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.i18n.Language;
import xyz.zcraft.acgpicdownload.commands.Fetch;
import xyz.zcraft.acgpicdownload.commands.Schedule;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.SSLUtil;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main {
    public static PrintWriter debugOut;
    public static PrintStream log;
    public static PrintWriter err;
    private static boolean debug = false;

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
        e.printStackTrace();
        err.print("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]");
        e.printStackTrace(err);
        err.flush();
    }

    public static void main(String[] args) {
        try {
            SSLUtil.ignoreSsl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setMFXLanguage();
        ArrayList<String> argList = new ArrayList<>(List.of(args));
        try {
            log = new PrintStream("out.log");
        } catch (FileNotFoundException ignored) {
        }
        if (argList.size() == 0) {
            GUI.start(args);
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

    public static void setMFXLanguage() {
        for (Language v : Language.values()) {
            if (v.getLocale().equals(Locale.getDefault())) {
                I18N.setLanguage(v);
                return;
            }
        }
    }
}
