package xyz.zcraft.acgpicdownload.util.downloadutil;

import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

public enum DownloadStatus {
    FAILED, STARTED, CREATED, COMPLETED, INITIALIZE, FILTERED;

    @Override
    public String toString() {
        if (this == DownloadStatus.CREATED) {
            return ResourceBundleUtil.getString("cli.download.status.created");
        } else if (this == DownloadStatus.COMPLETED) {
            return ResourceBundleUtil.getString("cli.download.status.completed");
        } else if (this == DownloadStatus.FILTERED) {
            return ResourceBundleUtil.getString("cli.download.status.filtered");
        } else if (this == DownloadStatus.INITIALIZE) {
            return ResourceBundleUtil.getString("cli.download.status.init");
        } else if (this == DownloadStatus.FAILED) {
            return ResourceBundleUtil.getString("cli.download.status.failed");
        } else if (this == DownloadStatus.STARTED) {
            return ResourceBundleUtil.getString("cli.download.status.started");
        } else {
            return "?";
        }
    }
}
