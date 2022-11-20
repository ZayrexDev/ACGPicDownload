module xyz.zcraft.acgpicdownload {
    requires javafx.controls;
    requires com.alibaba.fastjson2;
    requires org.jsoup;
    requires javafx.base;
    requires javafx.graphics;

    opens xyz.zcraft.acgpicdownload to javafx.controls, javafx.base, javafx.graphics;
    exports xyz.zcraft.acgpicdownload;
}