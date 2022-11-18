package xyz.zcraft.ACGPicDownload.Commands;

import xyz.zcraft.ACGPicDownload.Util.Logger;
import xyz.zcraft.ACGPicDownload.Util.ScheduleUtil.Event;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static xyz.zcraft.ACGPicDownload.Util.SourceUtil.SourceManager.isEmpty;

public class Schedule {
    private final ArrayList<Event> events = new ArrayList<>();
    Logger l;

    public void main(ArrayList<String> args) {
        if (!parseEvent(args)) {
            return;
        }

        l = new Logger("Schedule", System.out);

        Scanner s = new Scanner(System.in);

        INPUT:
        while (true) {
            System.out.print("Schedule>");
            String in = s.nextLine();

            if (isEmpty(in)) {
                continue;
            }

            ArrayList<String> t = new ArrayList<>(List.of(in.toLowerCase().split(" ")));

            switch (t.get(0)) {
                case "exit" -> {
                    return;
                }
                case "add" -> {
                    if (t.size() > 2) {
                        t.remove(0);
                        parseEvent(t);
                    }
                }
                case "del" -> {
                    if (t.size() == 2) {
                        try {
                            int i = Integer.parseInt(t.get(1));
                            events.get(i).setActive(false);
                            events.remove(i);
                            break;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    System.err.println("Please enter a valid event id");
                }
                case "start" -> {
                    break INPUT;
                }
                case "list" -> {
                    int a = "Command".length();
                    int b = "Max Times".length();
                    int c = "Interval".length();
                    for (Event e : events) {
                        a = Math.max(a, e.getCommandString().length());
                        b = Math.max(b, String.valueOf(e.getMaxTimes()).length());
                        c = Math.max(c, e.getInterval().toString().length());
                    }
                    l.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", "Command", " | ", "Max Times", " | ", "Interval");
                    for (Event event : events) {
                        l.printlnf("%-" + a + "s %s %-" + b + "s %s %-" + c + "s", event.getCommandString(), " | ", String.valueOf(event.getMaxTimes()), " | ", event.getInterval().toString());
                    }
                    break;
                }
            }
        }

        s.close();

        while (events.size() > 0) {
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                if (e.isActive()) {
                    if (System.currentTimeMillis() - e.getLastTimeRan() >= e.getInterval().toMillis() && (e.getMaxTimes() == -1 || e.getTimesRan() <= e.getMaxTimes())) {
                        e.addTimesRan();
                        e.setLastTimeRan(System.currentTimeMillis());
                        Logger logger = new Logger(String.valueOf(i), l, System.out);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Fetch f = new Fetch();
                                Logger t = new Logger(String.valueOf(this.hashCode()), logger, System.out);
                                f.main(e.getCommands(), t);
                                t.info("[Event End]");
                            }
                        });
                        t.start();
                    }
                }
            }
        }
    }

    public boolean parseEvent(ArrayList<String> args) {
        if (args.size() != 0) {
            int maxTime = -1;
            Duration interval = null;
            for (int i = 0; i < args.size(); i++) {
                switch (args.get(i)) {
                    case "--max-times", "-m" -> {
                        if (args.size() > i + 1) {
                            try {
                                maxTime = Integer.parseInt(args.get(i + 1));
                                args.remove(i);
                                args.remove(i);
                                i -= 1;
                                break;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        l.err("Please enter a valid number for the max time");
                        return false;
                    }
                    case "--interval", "-i" -> {
                        if (args.size() > i + 1) {
                            try {
                                interval = Duration.parse("PT" + args.get(i + 1));
                                args.remove(i);
                                args.remove(i);
                                i -= 1;
                                break;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        l.err("Please enter a valid number for the interval");
                        return false;
                    }
                }
            }

            if (interval != null) {
                if (args.size() > 0) {
                    if (args.get(0).equalsIgnoreCase("schedule")) {
                        l.err("Could not use schedule command in a schedule!");
                        return false;
                    }
                }
                events.add(new Event(args, interval, maxTime));
                l.info("Event added. ID = " + (events.size() - 1));
                return true;
            } else {
                l.err("Please provide interval for the event");
                return false;
            }
        } else {
            return true;
        }
    }
}