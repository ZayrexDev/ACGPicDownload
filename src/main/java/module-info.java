module xyz.zcraft.acgpicdownload {
    requires javafx.controls;
    requires com.alibaba.fastjson2;
    requires javafx.base;
    requires MaterialFX;
    requires org.jetbrains.annotations;
    requires VirtualizedFX;
    requires lombok;
    requires log4j;
    requires org.jsoup;
    requires animated.gif.lib;
    requires fastjson;

    opens xyz.zcraft.acgpicdownload to javafx.controls, javafx.base, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.gui to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.base to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.base.argpanes to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.controllers to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.util.source to com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.pixiv to javafx.base, javafx.controls, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    opens xyz.zcraft.acgpicdownload.util.source.argument to javafx.base, javafx.controls, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;

    exports xyz.zcraft.acgpicdownload;
    exports xyz.zcraft.acgpicdownload.exceptions;
    exports xyz.zcraft.acgpicdownload.gui;
    exports xyz.zcraft.acgpicdownload.gui.controllers;
    exports xyz.zcraft.acgpicdownload.gui.base;
    exports xyz.zcraft.acgpicdownload.gui.base.argpanes;
    exports xyz.zcraft.acgpicdownload.util;
    exports xyz.zcraft.acgpicdownload.util.pixiv;
    exports xyz.zcraft.acgpicdownload.util.fetch;
    exports xyz.zcraft.acgpicdownload.util.source;
    exports xyz.zcraft.acgpicdownload.util.source.argument;
    exports xyz.zcraft.acgpicdownload.util.dl;
}