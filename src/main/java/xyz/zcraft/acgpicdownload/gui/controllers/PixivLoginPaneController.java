package xyz.zcraft.acgpicdownload.gui.controllers;

import javafx.fxml.Initializable;
import javafx.scene.web.WebView;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class PixivLoginPaneController implements Initializable {
    public WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webView.setZoom(0.8);
        webView.getEngine().locationProperty().addListener((a, b, newValue) -> {
            if (newValue.startsWith("https://www.pixiv.net/")) {
                webView.getScene().getWindow().hide();
            }
        });
    }

    public void reload() {
        webView.getEngine().load(PixivFetchUtil.LOGIN);
    }
}
