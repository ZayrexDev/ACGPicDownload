package xyz.zcraft.ACGPicDownload.Commands;

import xyz.zcraft.ACGPicDownload.Util.Logger;
import xyz.zcraft.ACGPicDownload.Util.ScheduleUtil.Event;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static xyz.zcraft.ACGPicDownload.Util.FetchUtil.SourceUtil.SourceManager.isEmpty;

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
            }
        }

        while (events.size() > 0) {
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                if (e.isActive()) {
                    if (System.currentTimeMillis() - e.getLastTimeRan() >= e.getInterval().toMillis() && (e.getMaxTimes() == -1 || e.getTimesRan() <= e.getMaxTimes())) {
                        e.addTimesRan();
                        Fetch f = new Fetch();
                        Logger logger = new Logger(String.valueOf(i), l, System.out);
                        f.main(e.getCommands(), logger);
                        e.setLastTimeRan(System.currentTimeMillis());
                        logger.info("[Done]");
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
                    case "--max-time" -> {
                        if (args.size() > i + 1) {
                            try {
                                maxTime = Integer.parseInt(args.get(i + 1));
                                args.remove(i);
                                args.remove(i + 1);
                                break;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        l.err("Please enter a valid number for the max time");
                        return false;
                    }
                    case "--interval" -> {
                        if (args.size() > i + 1) {
                            try {
                                interval = Duration.parse("PT" + args.get(i + 1));
                                args.remove(i);
                                args.remove(i);
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