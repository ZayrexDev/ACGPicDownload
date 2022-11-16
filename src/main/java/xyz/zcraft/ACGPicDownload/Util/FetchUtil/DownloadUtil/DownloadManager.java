package xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil;

import java.text.DecimalFormat;

public class DownloadManager {
    private final DownloadResult[] process;

    public DownloadResult[] getProcess() {
        return process;
    }

    public DownloadManager(DownloadResult[] process) {
        this.process = process;
    }

    long total = 0;
    long downloaded = 0;
    int failed = 0;
    int completed = 0;
    int started = 0;
    int created = 0;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        total = 0;
        downloaded = 0;
        failed = 0;
        completed = 0;
        started = 0;
        created = 0;

        for (int i = 0; i < process.length; i++) {
            DownloadResult r = process[i];

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
            }
        }

        if(created == 0){
            sb.append("R:").append(created).append(" S:").append(started).append(" C:").append(completed).append(" F:").append(failed).append(" |");
        double p = (double) downloaded / (double) total;
        int a = (int) (20 * p);
        int b = 20 - a;

        sb.append("=".repeat(a)).append(" ".repeat(b)).append("|").append(df.format(p));

        }else{
            sb.append("Starting ").append(started).append("/").append(process.length);
        }

        
        if(created == 0 && started == 0){
            done = true;
        }

        return sb.toString();
    }

    private boolean done = false;

    public boolean done() {
        return done;
    }

    private static final DecimalFormat df = new DecimalFormat("##.#%");
}
