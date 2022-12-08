package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.scene.control.Label;
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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PixivSearchPaneController extends PixivFetchPane {
    @javafx.fxml.FXML
    public MFXComboBox<String> typeCombo;
    @javafx.fxml.FXML
    public MFXComboBox<String> modeCombo;
    @javafx.fxml.FXML
    public MFXSlider pageSlider;
    public MFXTextField keywordField;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;

    public static void getRelated(LinkedList<PixivArtwork> source, int depth, String cookieString, Label subOperationLabel) throws IOException {
        if (depth > 0) {
            List<PixivArtwork> temp2Artworks = new LinkedList<>();
            List<PixivArtwork> temp = new LinkedList<>(source);
            for (int i = 0; i < depth; i++) {
                final int finalI = i;
                Platform.runLater(() -> subOperationLabel
                        .setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchRel") + " "
                                + (finalI + 1) + " / " + depth));
                temp2Artworks.clear();
                for (int j = 0, tempSize = temp.size(); j < tempSize; j++) {
                    int finalJ = j;
                    Platform.runLater(() -> subOperationLabel
                            .setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchRel") + " "
                                    + (finalI + 1) + " / " + depth + " | "
                                    + (finalJ + 1) + " / " + temp.size()));
                    PixivArtwork temp2 = temp.get(j);
                    List<PixivArtwork> related = PixivFetchUtil.getRelated(temp2, 18,
                            cookieString,
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort"));
                    temp2Artworks.addAll(related);
                }
                temp.clear();
                temp.addAll(temp2Artworks);
                source.addAll(temp2Artworks);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("mfx-info-circle"));

        cookieField.textProperty().addListener((observableValue, s, t1) -> ConfigManager.getTempConfig().put("cookie", t1));
        cookieField.setText(Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject()).getString("cookie"));

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
    }

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

                if (typeCombo.getSelectedIndex() == 0) {
                    pixivArtworks.addAll(PixivFetchUtil.searchTopArtworks(
                            keywordField.getText(),
                            cookieField.getText(),
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort")
                    ));
                } else if (typeCombo.getSelectedIndex() == 1) {
                    pixivArtworks.addAll(PixivFetchUtil.searchIllustArtworks(
                            keywordField.getText(),
                            modeCombo.getSelectedIndex(),
                            ((int) pageSlider.getValue()),
                            cookieField.getText(),
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort")
                    ));
                } else if (typeCombo.getSelectedIndex() == 2) {
                    pixivArtworks.addAll(PixivFetchUtil.searchMangaArtworks(
                            keywordField.getText(),
                            modeCombo.getSelectedIndex(),
                            ((int) pageSlider.getValue()),
                            cookieField.getText(),
                            ConfigManager.getConfig().getString("proxyHost"),
                            ConfigManager.getConfig().getInteger("proxyPort")
                    ));
                }

                getRelated(pixivArtworks, ((int) relatedDepthSlider.getValue()), cookieField.getText(), subOperationLabel);
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
