module xyz.zcraft.acgpicdownload {
    requires javafx.controls;
    requires com.alibaba.fastjson2;
    requires org.jsoup;
    requires javafx.base;
    requires javafx.graphics;
    requires MaterialFX;

    opens xyz.zcraft.acgpicdownload to javafx.controls, javafx.base, javafx.graphics, javafx.fxml;
    exports xyz.zcraft.acgpicdownload;
    exports xyz.zcraft.acgpicdownload.gui;
    opens xyz.zcraft.acgpicdownload.gui to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    opens xyz.zcraft.acgpicdownload.gui.scenes to javafx.base, javafx.controls, javafx.graphics, javafx.fxml;
    exports xyz.zcraft.acgpicdownload.gui.scenes;
}