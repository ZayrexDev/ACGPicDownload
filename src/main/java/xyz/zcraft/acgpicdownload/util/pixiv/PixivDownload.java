package xyz.zcraft.acgpicdownload.util.pixiv;

import lombok.Data;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.dl.DownloadStatus;

@Data
public class PixivDownload {
    private PixivArtwork artwork;
    private DownloadStatus status = DownloadStatus.CREATED;
    private Exception exception;
    private int progress = 0;
    private int total = 0;

    public PixivDownload(PixivArtwork artwork) {
        this.artwork = artwork;
    }

    public String getStatusString() {
        if (status == DownloadStatus.FILTERED) {
            return ResourceBundleUtil.getString("gui.download.status.filtered")
                    + " " + artwork.getBookmarkCount() + "|" + artwork.getLikeCount();
        } else if (status == DownloadStatus.STARTED) {
            return ResourceBundleUtil.getString("gui.download.status.started") + "(" + progress + "/" + total + ")";
        } else {
            return status.toString();
        }
    }
}
