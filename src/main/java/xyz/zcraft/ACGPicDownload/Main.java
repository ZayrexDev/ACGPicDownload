package xyz.zcraft.ACGPicDownload;

import xyz.zcraft.ACGPicDownload.Commands.Fetch;
import xyz.zcraft.ACGPicDownload.Commands.Schedule;
import xyz.zcraft.ACGPicDownload.Util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> argList = new ArrayList<>(List.of(args));
        if (argList.size() == 0) {
            Fetch f = new Fetch();
            f.main(argList, new Logger("Fetch", System.out));
        } else if (argList.get(0).equalsIgnoreCase("fetch")) {
            argList.remove(0);
            Fetch f = new Fetch();
            f.main(argList, new Logger("Fetch", System.out));
        } else if (argList.get(0).equalsIgnoreCase("schedule")) {
            argList.remove(0);
            Schedule s = new Schedule();
            s.main(argList);
        }
    }
}
