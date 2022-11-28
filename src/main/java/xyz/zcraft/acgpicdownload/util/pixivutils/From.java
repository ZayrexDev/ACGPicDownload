package xyz.zcraft.acgpicdownload.util.pixivutils;

public enum From {
    Follow,
    Recommend,
    Other;

    @Override
    public String toString() {
        if (this == Follow) {
            return "Follow";
        } else if (this == Recommend) {
            return "Follow";
        } else {
            return "Other";
        }
    }
}
