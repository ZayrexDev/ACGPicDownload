package xyz.zcraft.acgpicdownload.util.dl;

import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

public enum DownloadStatus {
    FAILED, STARTED, CREATED, COMPLETED, INITIALIZE, FILTERED;

    @Override
    public String toString() {
        if (this == DownloadStatus.CREATED) {
            return ResourceBundleUtil.getString("gui.download.status.created");
        } else if (this == DownloadStatus.COMPLETED) {
            return ResourceBundleUtil.getString("gui.download.status.completed");
        } else if (this == DownloadStatus.FILTERED) {
            return ResourceBundleUtil.getString("gui.download.status.filtered");
        } else if (this == DownloadStatus.INITIALIZE) {
            return ResourceBundleUtil.getString("gui.download.status.init");
        } else if (this == DownloadStatus.FAILED) {
            return ResourceBundleUtil.getString("gui.download.status.failed");
        } else if (this == DownloadStatus.STARTED) {
            return ResourceBundleUtil.getString("gui.download.status.started");
        } else {
            return "?";
        }
    }
}
