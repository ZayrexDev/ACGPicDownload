package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.PixivFetchPane;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.From;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PixivRelatedPaneController extends PixivFetchPane {
    @javafx.fxml.FXML
    private MFXTextField idField;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
    }

    @FXML
    @Override
    public void fetchBtnOnAction() {
        if (idField.getText().startsWith("https://www.pixiv.net/artworks/"))
            idField.setText(idField.getText().substring(idField.getText().lastIndexOf("/") + 1));
        loadingPane.setVisible(true);
        operationLabel.setText(ResourceBundleUtil.getString(""));
        ft.stop();
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setOnFinished(null);
        ft.play();

        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    operationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                    subOperationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                });

                PixivArtwork artwork = PixivFetchUtil.getArtwork(
                        idField.getText(),
                        getCookie(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                );
                artwork.setFrom(From.Spec);
                LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>(List.of(artwork));

                getRelated(pixivArtworks, (int) relatedDepthSlider.getValue(), getCookie(), subOperationLabel);
                Platform.runLater(() -> data.addAll(pixivArtworks));
                Notice.showSuccess(String.format(
                        Objects.requireNonNull(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetched")),
                        pixivArtworks.size()), gui.mainPane);
            } catch (IOException e) {
                Main.logError(e);
                gui.showError(e);
            } finally {
                ft.stop();
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished((e) -> loadingPane.setVisible(false));
                ft.play();
            }
        }).start();
    }
}
