module xyz.zcraft.acgpicdownload {
    requires transitive javafx.controls;
    requires transitive com.alibaba.fastjson2;
    requires transitive org.jsoup;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive MaterialFX;
    requires animated.gif.lib;

    opens xyz.zcraft.acgpicdownload to javafx.controls, javafx.base, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    exports xyz.zcraft.acgpicdownload;
    exports xyz.zcraft.acgpicdownload.gui;
    opens xyz.zcraft.acgpicdownload.gui to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.controllers to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    exports xyz.zcraft.acgpicdownload.gui.controllers;
    opens xyz.zcraft.acgpicdownload.util.sourceutil to com.alibaba.fastjson2;
    exports xyz.zcraft.acgpicdownload.util.sourceutil;
    opens xyz.zcraft.acgpicdownload.gui.base to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    exports xyz.zcraft.acgpicdownload.gui.base;
    opens xyz.zcraft.acgpicdownload.gui.base.argpanes to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    exports xyz.zcraft.acgpicdownload.gui.base.argpanes;
    opens xyz.zcraft.acgpicdownload.util.sourceutil.argument to javafx.base, javafx.controls, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    exports xyz.zcraft.acgpicdownload.util.sourceutil.argument;
    opens xyz.zcraft.acgpicdownload.util.pixivutils to javafx.base, javafx.controls, javafx.graphics, javafx.fxml, com.alibaba.fastjson2;
    exports xyz.zcraft.acgpicdownload.util.pixivutils;
    exports xyz.zcraft.acgpicdownload.util.fetchutil;
    exports xyz.zcraft.acgpicdownload.util;
    exports xyz.zcraft.acgpicdownload.util.downloadutil;
}