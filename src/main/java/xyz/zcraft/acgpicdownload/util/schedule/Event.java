package xyz.zcraft.acgpicdownload.util.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;

public class Event {
    @Getter
    private final ArrayList<String> commands;
    @Getter
    private final Duration interval;
    @Getter
    private final int maxTimes;

    private boolean isActive = true;

    @Setter
    private int timesRan = 0;
    @Setter
    @Getter
    private long lastTimeRan;

    public Event(ArrayList<String> commands, Duration interval, int maxTimes) {
        this.commands = commands;
        this.interval = interval;
        this.maxTimes = maxTimes;
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

    public void addTimesRan() {
        this.timesRan++;
    }

    @Override
    public String toString() {
        return "Event{" + "commands=" + commands + ", interval=" + interval + ", maxTimes=" + maxTimes + ", isActive=" + isActive + ", timesRan=" + timesRan + ", lastTimeRan=" + lastTimeRan + '}';
    }

    public String getCommandString() {
        StringBuilder sb = new StringBuilder();
        for (String command : commands) {
            sb.append(command.concat(" "));
        }
        return sb.toString();
    }
}
