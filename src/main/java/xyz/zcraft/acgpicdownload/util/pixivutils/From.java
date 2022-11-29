package xyz.zcraft.acgpicdownload.util.pixivutils;

public enum From {
    Follow,
    Recommend,
    Related,
    Other;

    @Override
    public String toString() {
        if (this == Follow) {
            return "Follow";
        } else if (this == Recommend) {
            return "Follow";
        } else if (this == Related) {
            return "Related";
        } else {
            return "Other";
        }
    }
}
