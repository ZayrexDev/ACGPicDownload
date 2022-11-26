package xyz.zcraft.acgpicdownload.util.downloadutil;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

public class DownloadManager {
    private static final DecimalFormat df = new DecimalFormat("##.#%");
    private static final DecimalFormat df2 = new DecimalFormat("#.##");
    private static final int PROGRESS_BAR_SIZE = 25;
    private final DownloadResult[] process;
    long total = 0;
    long downloaded = 0;
    int failed = 0;
    int completed = 0;
    int started = 0;
    private final long startTime;
    private final LinkedList<String> error = new LinkedList<>();
    ThreadPoolExecutor tpe;

    int created = 0;
    private boolean done = false;
    private long lastDownloaded = 0;
    private long lastTime = 0;
    String speed;
    private int timesGot = 0;
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

    public ThreadPoolExecutor getTpe() {
        return tpe;
    }

    public void setTpe(ThreadPoolExecutor tpe) {
        this.tpe = tpe;
    }

    public DownloadResult[] getProcess() {
        return process;
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

    public long getTotal() {
        return total;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public int getFailed() {
        return failed;
    }

    public int getCompleted() {
        return completed;
    }

    public int getStarted() {
        return started;
    }

    public int getCreated() {
        return created;
    }

    public boolean isDone() {
        if (tpe == null) {
            return done;
        } else {
            return tpe.getCompletedTaskCount() == tpe.getTaskCount();
        }
    }

    public long getLastDownloaded() {
        return lastDownloaded;
    }

    public long getLastTime() {
        return lastTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTimesGot() {
        return timesGot;
    }

    public double getP() {
        return p;
    }

    public String getSpeed() {
        return speed;
    }

    public LinkedList<String> getError() {
        return error;
    }

    @Override
    public String toString() {
        update();

        timesGot++;
        StringBuilder sb = new StringBuilder();

        for (String error2 : error) {
            sb.append(ResourceBundleUtil.getString("cli.fetch.err")).append(":").append(error2).append("\n");
        }

        error.clear();

        int mtf = 8;
        sb.append(timesGot > mtf ? "W" : ResourceBundleUtil.getString("cli.download.status.created")).append(":").append(created).append(" ").append(timesGot > mtf ? "S" : ResourceBundleUtil.getString("cli.download.status.started"))
                .append(":").append(started).append(" ").append(timesGot > mtf ? "D" : ResourceBundleUtil.getString("cli.download.status.completed")).append(":").append(completed)
                .append(" ").append(timesGot > mtf ? "F" : ResourceBundleUtil.getString("cli.download.status.failed"))
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
