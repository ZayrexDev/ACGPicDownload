package xyz.zcraft.acgpicdownload.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import com.alibaba.fastjson2.JSONObject;

import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.PixivFetchPane;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.From;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

public class PixivRelatedPaneController extends PixivFetchPane {
    @javafx.fxml.FXML
    private MFXTextField idField;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        GUI.initTable(data, dataTable);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("mfx-info-circle"));

        cookieField.textProperty().addListener((observableValue, s, t1) -> ConfigManager.getTempConfig().put("cookie", t1));
        cookieField.setText(Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject()).getString("cookie"));
    }

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
                        cookieField.getText(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                );
                artwork.setFrom(From.Spec);
                LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>(List.of(artwork));

                if (relatedDepthSlider.getValue() > 0) {
                    List<PixivArtwork> temp2Artworks = new LinkedList<>();
                    List<PixivArtwork> temp = new LinkedList<>(pixivArtworks);
                    for (int i = 0; i < relatedDepthSlider.getValue(); i++) {
                        final int finalI = i;
                        Platform.runLater(() -> subOperationLabel
                                .setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchRel") + " "
                                        + (finalI + 1) + " / " + (int) relatedDepthSlider.getValue()));
                        temp2Artworks.clear();
                        for (int j = 0, tempSize = temp.size(); j < tempSize; j++) {
                            int finalJ = j;
                            Platform.runLater(() -> subOperationLabel
                                    .setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchRel") + " "
                                            + (finalI + 1) + " / " + (int) relatedDepthSlider.getValue() + " | "
                                            + (finalJ + 1) + " / " + temp.size()));
                            PixivArtwork temp2 = temp.get(j);
                            List<PixivArtwork> related = PixivFetchUtil.getRelated(temp2, 18,
                                    cookieField.getText(),
                                    ConfigManager.getConfig().getString("proxyHost"),
                                    ConfigManager.getConfig().getInteger("proxyPort"));
                            temp2Artworks.addAll(related);
                        }
                        temp.clear();
                        temp.addAll(temp2Artworks);
                        pixivArtworks.addAll(temp2Artworks);
                    }
                }
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
