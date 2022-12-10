package xyz.zcraft.acgpicdownload.util.pixivutils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ArtworkCondition {
    private int bookmarkCount = -1;
    private int likeCount = -1;
    public boolean test(PixivArtwork artwork){
        return artwork.getBookmarkCount() >= bookmarkCount && artwork.getLikeCount() >= likeCount;
    }

    public ArtworkCondition bookmark(int count){
        this.bookmarkCount = count;
        return this;
    }

    public ArtworkCondition like(int count){
        this.likeCount = count;
        return this;
    }

    public static ArtworkCondition always(){
        return new ArtworkCondition();
    }
}
