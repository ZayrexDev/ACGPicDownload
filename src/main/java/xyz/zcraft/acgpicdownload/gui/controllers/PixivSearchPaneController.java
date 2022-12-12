package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXComboBox;
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
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;

public class PixivSearchPaneController extends PixivFetchPane {
    private static final String[] SUFFIX = {
            "",
            "30000users入り",
            "20000users入り",
            "10000users入り",
            "5000users入り",
            "1000users入り",
            "500users入り",
            "300users入り",
            "100users入り",
            "50users入り"
    };
    @javafx.fxml.FXML
    public MFXComboBox<String> typeCombo;
    @javafx.fxml.FXML
    public MFXComboBox<String> modeCombo;
    @javafx.fxml.FXML
    public MFXSlider pageSlider;
    public MFXTextField keywordField;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;
    @FXML
    private MFXComboBox<String> suffixCombo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));

        typeCombo.getItems().addAll(
                ResourceBundleUtil.getString("gui.pixiv.search.mode.top"),
                ResourceBundleUtil.getString("gui.pixiv.search.mode.illust"),
                ResourceBundleUtil.getString("gui.pixiv.search.mode.manga")
        );

        modeCombo.getItems().addAll(
                ResourceBundleUtil.getString("gui.pixiv.disc.mode.all"),
                ResourceBundleUtil.getString("gui.pixiv.disc.mode.safe"),
                ResourceBundleUtil.getString("gui.pixiv.disc.mode.adult")
        );

        typeCombo.selectedIndexProperty().addListener((observableValue, number, t1) -> {
            int i = t1.intValue();
            modeCombo.setDisable(i == 0);
            pageSlider.setDisable(i == 0);
        });

        typeCombo.selectFirst();
        modeCombo.selectFirst();

        suffixCombo.getItems().addAll(SUFFIX);
        suffixCombo.selectFirst();
    }

    @FXML
    @Override
    public void fetchBtnOnAction() {
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
                LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();

                String keyword = keywordField.getText().concat(suffixCombo.getSelectedItem());
                if (typeCombo.getSelectedIndex() == 0) {
                    pixivArtworks.addAll(PixivFetchUtil.searchTopArtworks(
                            keyword,
                            getCookie(),
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort")
                    ));
                } else if (typeCombo.getSelectedIndex() == 1) {
                    pixivArtworks.addAll(PixivFetchUtil.searchIllustArtworks(
                            keyword,
                            modeCombo.getSelectedIndex(),
                            ((int) pageSlider.getValue()),
                            getCookie(),
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort")
                    ));
                } else if (typeCombo.getSelectedIndex() == 2) {
                    pixivArtworks.addAll(PixivFetchUtil.searchMangaArtworks(
                            keyword,
                            modeCombo.getSelectedIndex(),
                            ((int) pageSlider.getValue()),
                            getCookie(),
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort")
                    ));
                }

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
