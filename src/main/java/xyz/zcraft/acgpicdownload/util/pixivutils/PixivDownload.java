package xyz.zcraft.acgpicdownload.util.pixivutils;

import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadResult;

public class PixivDownload extends DownloadResult {
    protected PixivArtwork artwork;

    public PixivDownload(PixivArtwork artwork) {
        this.artwork = artwork;
    }

    public PixivArtwork getArtwork() {
        return artwork;
    }

    public void setArtwork(PixivArtwork artwork) {
        this.artwork = artwork;
    }
}
