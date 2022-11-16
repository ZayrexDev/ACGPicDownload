package xyz.zcraft.ACGPicDownload.Util.ScheduleUtil;

import java.time.Duration;
import java.util.ArrayList;

public class Event {
    private final ArrayList<String> commands;
    private final Duration interval;
    private final int maxTimes;

    private boolean isActive = true;

    private int timesRan = 0;
    private long lastTimeRan;

    public Event(ArrayList<String> commands, Duration interval, int maxTimes) {
        this.commands = commands;
        this.interval = interval;
        this.maxTimes = maxTimes;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public Duration getInterval() {
        return interval;
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getTimesRan() {
        return timesRan;
    }

    public void setTimesRan(int timeRan) {
        this.timesRan = timeRan;
    }

    public void addTimesRan() {
        this.timesRan++;
    }

    @Override
    public String toString() {
        return "Event{" + "commands=" + commands + ", interval=" + interval + ", maxTimes=" + maxTimes + ", isActive=" + isActive + ", timesRan=" + timesRan + ", lastTimeRan=" + lastTimeRan + '}';
    }

    public long getLastTimeRan() {
        return lastTimeRan;
    }

    public void setLastTimeRan(long lastTimeRan) {
        this.lastTimeRan = lastTimeRan;
    }

    public String getCommandString() {
        StringBuilder sb = new StringBuilder();
        for (String command : commands) {
            sb.append(command.concat(" "));
        }
        return sb.toString();
    }
}
