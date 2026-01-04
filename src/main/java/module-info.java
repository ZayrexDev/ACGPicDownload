module xyz.zcraft.acgpicdownload {
    requires com.alibaba.fastjson2;
    requires org.jetbrains.annotations;
    requires lombok;
    requires log4j;
    requires org.jsoup;
    requires animated.gif.lib;
    requires fastjson;
    requires java.desktop;

    opens xyz.zcraft.acgpicdownload to com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.source to com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.pixiv to com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.source.argument to com.alibaba.fastjson2;

    exports xyz.zcraft.acgpicdownload;
    exports xyz.zcraft.acgpicdownload.exceptions;
    exports xyz.zcraft.acgpicdownload.util;
    exports xyz.zcraft.acgpicdownload.util.pixiv;
    exports xyz.zcraft.acgpicdownload.util.fetch;
    exports xyz.zcraft.acgpicdownload.util.source;
    exports xyz.zcraft.acgpicdownload.util.source.argument;
    exports xyz.zcraft.acgpicdownload.util.dl;
}