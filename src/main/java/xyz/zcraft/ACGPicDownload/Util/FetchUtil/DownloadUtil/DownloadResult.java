package xyz.zcraft.ACGPicDownload.Util.FetchUtil.DownloadUtil;

public class DownloadResult {
    private long totalSize;
    private DownloadStatus status = DownloadStatus.CREATED;
    private long sizeDownloaded = 0;
    public DownloadResult() {}
    public long getTotalSize() {
        return totalSize;
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
    public void addSizeDownloaded(long size){
        sizeDownloaded += size;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}

