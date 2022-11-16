package xyz.zcraft.ACGPicDownload.Util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final String name;
    private final Logger parentLogger;

    private final PrintStream out;

    public Logger(String name, Logger parentLogger, PrintStream out) {
        this.name = name;
        this.parentLogger = parentLogger;
        this.out = out;
    }

    public Logger(String name, PrintStream out) {
        this.name = name;
        this.out = out;
        parentLogger = null;
    }

    public String getName() {
        if (parentLogger != null) {
            return parentLogger.getName() + "|" + name;
        }
        return name;
    }

    public Logger getParentLogger() {
        return parentLogger;
    }

    public String getOutputName() {
        return "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "][" + getName() + "] ";
    }

    public void info(String message) {
        out.print("\r\n");
        out.println(getOutputName() + message);
        out.flush();
    }

    public void info() {
        out.println(getOutputName());
        out.flush();
    }

    public void r() {
        out.print("\r");
        out.flush();
        out.print(getOutputName());
        out.flush();
    }

    public void print(String str) {
        out.print(getOutputName());
        out.print(str);
        out.flush();
    }

    public void rprint(String str){
        out.print("\r");
        out.print(getOutputName());
        out.print(str);
        out.flush();
    }

    public void printOutputName() {
        out.print(getOutputName());
        out.flush();
    }

    public void err(String message) {
        System.err.println(getOutputName() + message);
    }

    public void printf(String format, String... arg) {
        out.print(getOutputName());
        out.printf(format, (Object[]) arg);
        out.flush();
    }

    public void printlnf(String format, String... arg) {
        printf(format, arg);
        out.println();
        out.flush();
    }
}
