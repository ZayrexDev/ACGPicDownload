package xyz.zcraft.acgpicdownload.util.dl;

import lombok.Getter;
import lombok.Setter;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.fetch.Result;

@Setter
@Getter
public class DownloadResult {
    protected long totalSize;
    protected DownloadStatus status = DownloadStatus.CREATED;
    protected long sizeDownloaded = 0;
    protected String errorMessage;
    protected Result result;

    public DownloadResult() {
    }

    public DownloadResult(Result result) {
        this.result = result;
    }

    public void addSizeDownloaded(long size) {
        sizeDownloaded += size;
    }

    public String getStatusString() {
        if (status == DownloadStatus.CREATED) {
            return ResourceBundleUtil.getString("gui.download.status.created");
        } else if (status == DownloadStatus.COMPLETED) {
            return ResourceBundleUtil.getString("gui.download.status.completed");
        } else if (status == DownloadStatus.FAILED) {
            return ResourceBundleUtil.getString("gui.download.status.failed");
        } else if (status == DownloadStatus.STARTED) {
            return ResourceBundleUtil.getString("gui.download.status.started");
        } else {
            return "?";
        }
    }
}

