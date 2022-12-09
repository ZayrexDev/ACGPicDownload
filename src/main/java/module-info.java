module xyz.zcraft.acgpicdownload {
    requires transitive javafx.controls;
    requires transitive com.alibaba.fastjson2;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive MaterialFX;
    requires transitive org.jetbrains.annotations;
    requires transitive VirtualizedFX;
    requires transitive lombok;
    requires animated.gif.lib;
    requires org.jsoup;

    opens xyz.zcraft.acgpicdownload to javafx.controls, javafx.base, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.gui to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.base to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.base.argpanes to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.controllers to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.util.sourceutil to com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.pixivutils to javafx.base, javafx.controls, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.sourceutil.argument to javafx.base, javafx.controls, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    exports xyz.zcraft.acgpicdownload;
    exports xyz.zcraft.acgpicdownload.exceptions;
    exports xyz.zcraft.acgpicdownload.gui;
    exports xyz.zcraft.acgpicdownload.gui.controllers;
    exports xyz.zcraft.acgpicdownload.gui.base;
    exports xyz.zcraft.acgpicdownload.gui.base.argpanes;
    exports xyz.zcraft.acgpicdownload.util;
    exports xyz.zcraft.acgpicdownload.util.pixivutils;
    exports xyz.zcraft.acgpicdownload.util.fetchutil;
    exports xyz.zcraft.acgpicdownload.util.sourceutil;
    exports xyz.zcraft.acgpicdownload.util.sourceutil.argument;
    exports xyz.zcraft.acgpicdownload.util.downloadutil;
}