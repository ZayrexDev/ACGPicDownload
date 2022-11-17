package xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class DownloadManager {
    private static final DecimalFormat df = new DecimalFormat("##.#%");
    private static final DecimalFormat df2 = new DecimalFormat("#.##");
    private final DownloadResult[] process;
    long total = 0;
    long downloaded = 0;
    int failed = 0;
    int completed = 0;
    int started = 0;
    int created = 0;
    private static final int PROGRESS_BAR_SIZE = 30;
    private boolean done = false;

    public DownloadManager(DownloadResult[] process) {
        this.process = process;
    }

    public DownloadResult[] getProcess() {
        return process;
    }

    private long lastDownloaded = 0;
    private long lastTime = 0;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        total = 0;
        downloaded = 0;
        failed = 0;
        completed = 0;
        started = 0;
        created = 0;

        ArrayList<String> error = new ArrayList<>();

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
                if(!Objects.equals("", r.getErrorMessage())){
                    error.add(r.getErrorMessage());
                    r.setErrorMessage("");
                }
            }
        }

        for (String error2 : error) {
            sb.append("Error:").append(error2).append("\n");
        }

        sb.append("Waiting:").append(created).append(" Started:").append(started).append(" Completed:").append(completed).append(" Failed:").append(failed).append(" |");
        double p = (double) downloaded / (double) total;
        int a = (int) (PROGRESS_BAR_SIZE * p);
        int b = PROGRESS_BAR_SIZE - a;
        sb.append("=".repeat(Math.abs(Math.min(PROGRESS_BAR_SIZE, a)))).append(" ".repeat(Math.abs(Math.max(0, b)))).append("|");

        if (p > 1) {
            sb.append("Waiting");
        } else {
            sb.append(df.format(p));
        }

        if(lastTime != 0){
            sb.append(" ");
            double speed = (((double)(downloaded - lastDownloaded)) / 1024.0) / (((double)(System.currentTimeMillis() - lastTime)) / 1000.0);
            if(speed > 1024.0){
                sb.append(df2.format(speed / 1024.0)).append("mb/s");
            }else{
                sb.append(df2.format(speed)).append("kb/s");
            }
        }

        lastDownloaded = downloaded;
        lastTime = System.currentTimeMillis();


        // sb.append("|").append(df2.format(downloaded / 1024)).append("/").append(df2.format(total / 1024));
        if (created == 0 && started == 0) {
            done = true;
        }

        return sb.toString();
    }

    public boolean done() {
        return done;
    }
}
