package xyz.zcraft.acgpicdownload.util.dl;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadManager {
    private static final DecimalFormat df = new DecimalFormat("##.#%");
    private static final DecimalFormat df2 = new DecimalFormat("#.##");
    private static final int PROGRESS_BAR_SIZE = 25;
    @Getter
    private final DownloadResult[] process;
    @Getter
    private final long startTime;
    @Getter
    private final LinkedList<String> error = new LinkedList<>();
    @Getter
    long total = 0;
    @Getter
    long downloaded = 0;
    @Getter
    int failed = 0;
    @Getter
    int completed = 0;
    @Getter
    int started = 0;
    @Setter
    @Getter
    ThreadPoolExecutor tpe;

    @Getter
    int created = 0;
    @Getter
    String speed;
    private boolean done = false;
    @Getter
    private long lastDownloaded = 0;
    @Getter
    private long lastTime = 0;
    @Getter
    private int timesGot = 0;
    @Getter
    private double p = 0;

    public DownloadManager(DownloadResult[] process, ThreadPoolExecutor tpe) {
        this.tpe = tpe;
        startTime = System.currentTimeMillis();
        this.process = process;
    }

    public DownloadManager(DownloadResult[] process) {
        startTime = System.currentTimeMillis();
        this.process = process;
    }

    public double getPercentComplete() {
        return p;
    }

    public void update() {
        total = 0;
        downloaded = 0;
        failed = 0;
        completed = 0;
        started = 0;
        created = 0;


        for (DownloadResult r : process) {
            if (r.getStatus() == DownloadStatus.CREATED) {
                created++;
            } else if (r.getStatus() == DownloadStatus.STARTED) {
                started++;
                total += r.getTotalSize();
                downloaded += r.getSizeDownloaded();
            } else if (r.getStatus() == DownloadStatus.COMPLETED) {
                completed++;
                total += r.getTotalSize();
                downloaded += r.getTotalSize();
            } else if (r.getStatus() == DownloadStatus.FAILED) {
                failed++;
                if (!Objects.equals("", r.getErrorMessage())) {
                    error.add(r.getErrorMessage());
                    r.setErrorMessage("");
                }
            }
        }

        if (total != 0) {
            p = (double) downloaded / (double) total;
            if (p < 0) p = 0;
            if (p > 1) p = 1;
        }

        double speed = Math.max((((double) (downloaded - lastDownloaded)) / 1024.0) / (((double) (System.currentTimeMillis() - lastTime)) / 1000.0), 0);
        if (speed > 1024.0) {
            this.speed = df2.format(speed / 1024.0).concat("mb/s");
        } else {
            this.speed = df2.format(speed).concat("kb/s");
        }

        lastDownloaded = downloaded;
        lastTime = System.currentTimeMillis();
    }

    public boolean isDone() {
        if (tpe == null) {
            return done;
        } else {
            return tpe.getCompletedTaskCount() == tpe.getTaskCount();
        }
    }

    @Override
    public String toString() {
        update();

        timesGot++;
        StringBuilder sb = new StringBuilder();

        for (String error2 : error) {
            sb.append("Error occurred").append(":").append(error2).append("\n");
        }

        error.clear();

        int mtf = 8;
        sb.append(timesGot > mtf ? "W" : "Waiting").append(":").append(created).append(" ").append(timesGot > mtf ? "S" : "Started")
                .append(":").append(started).append(" ").append(timesGot > mtf ? "D" : "Done").append(":").append(completed)
                .append(" ").append(timesGot > mtf ? "F" : "Failed")
                .append(":").append(failed).append(" |");

        int a = (int) (PROGRESS_BAR_SIZE * p);
        if (a < 0) {
            a = 0;
        } else if (a > PROGRESS_BAR_SIZE) {
            a = PROGRESS_BAR_SIZE;
        }
        int b = PROGRESS_BAR_SIZE - a;
        sb.append("=".repeat(a)).append(" ".repeat(b)).append("|");

        if (p > 1) {
            sb.append("...");
        } else {
            sb.append(df.format(p));
        }

        if (lastTime != 0) {
            sb.append(" ").append(speed);

            double avg = Math.max(((double) (total) / 1024.0) / ((double) (System.currentTimeMillis() - startTime) / 1000.0), 0);
            if (avg > 1024.0) {
                sb.append(" AVG:").append(df2.format(avg / 1024.0).concat("mb/s"));
            } else {
                sb.append(" AVG:").append(df2.format(avg).concat("kb/s"));
            }

            double eta = (double) (total - downloaded) / 1024 / avg;

            if (eta >= 0) {
                sb.append(" ETA:").append(df2.format(eta)).append("s");
            }
        }

        if (created == 0 && started == 0) {
            done = true;
        }

        return sb.toString();
    }

    public boolean done() {
        return done;
    }
}
