package xyz.zcraft.acgpicdownload.util.pixivutils;

import lombok.Data;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadStatus;

@Data
public class PixivDownload {
    private PixivArtwork artwork;
    private DownloadStatus status = DownloadStatus.CREATED;

    public PixivDownload(PixivArtwork artwork) {
        this.artwork = artwork;
    }

    public String getStatusString() {
        if (status == DownloadStatus.FILTERED) {
            return ResourceBundleUtil.getString("cli.download.status.filtered")
                    + " " + artwork.getBookmarkCount() + "|" + artwork.getLikeCount();
        } else {
            return status.toString();
        }
    }
}
