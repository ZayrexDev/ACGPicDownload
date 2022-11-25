package xyz.zcraft.acgpicdownload.util.downloadutil;

import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;

public class DownloadResult {
    private long totalSize;
    private DownloadStatus status = DownloadStatus.CREATED;
    private long sizeDownloaded = 0;
    private String errorMessage;
    private Result result;

    public DownloadResult() {
    }

    public DownloadResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public long getSizeDownloaded() {
        return sizeDownloaded;
    }

    public void setSizeDownloaded(long sizeDownloaded) {
        this.sizeDownloaded = sizeDownloaded;
    }

    public void addSizeDownloaded(long size) {
        sizeDownloaded += size;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatusString() {
        if (status == DownloadStatus.CREATED) {
            return ResourceBundleUtil.getString("cli.download.status.created");
        } else if (status == DownloadStatus.COMPLETED) {
            return ResourceBundleUtil.getString("cli.download.status.completed");
        } else if (status == DownloadStatus.FAILED) {
            return ResourceBundleUtil.getString("cli.download.status.failed");
        } else if (status == DownloadStatus.STARTED) {
            return ResourceBundleUtil.getString("cli.download.status.started");
        } else {
            return "?";
        }
    }
}

