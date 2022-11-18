package xyz.zcraft.ACGPicDownload.Util.DownloadUtil;

import xyz.zcraft.ACGPicDownload.Util.FetchUtil.Result;

public class DownloadResult {
    private long totalSize;
    private DownloadStatus status = DownloadStatus.CREATED;
    private long sizeDownloaded = 0;
    private String errorMessage;

    private Result result;

    public DownloadResult() {
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
}

