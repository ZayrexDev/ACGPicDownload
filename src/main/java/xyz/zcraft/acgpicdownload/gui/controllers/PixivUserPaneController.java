package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.PixivFetchPane;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownload;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class PixivUserPaneController extends PixivFetchPane {
    @javafx.fxml.FXML
    private MFXTextField uidField;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;

    @javafx.fxml.FXML
    public void backBtnOnAction() {
        hide();
    }

    @javafx.fxml.FXML
    public void cookieHelpBtnOnAction() throws IOException, URISyntaxException {
        if (Locale.getDefault().equals(Locale.CHINA) || Locale.getDefault().equals(Locale.TAIWAN)) {
            java.awt.Desktop.getDesktop()
                    .browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/%E8%8E%B7%E5%8F%96Cookie"));
        } else {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/Get-cookie"));
        }
    }

    @javafx.fxml.FXML
    public void backToMenu() {
        super.hide();
        gui.welcomePaneController.showMain();
    }

    @javafx.fxml.FXML
    public void submitCookie() {
        try {
            JSONObject json = Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject());
            HashMap<String, String> stringStringHashMap = PixivFetchUtil.parseCookie(cookieField.getText());
            String s = stringStringHashMap.get("PHPSESSID");
            cookieField.setText("PHPSESSID" + "=" + s);
            json.put("cookie", cookieField.getText());
            ConfigManager.getConfig().put("pixiv", json);
            ConfigManager.saveConfig();
            Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.notice.savedCookie"), gui.mainPane);
        } catch (IOException e) {
            Main.logError(e);
            gui.showError(e);
        }
    }

    public void show() {
        cookieField.setText(ConfigManager.getTempConfig().get("cookie"));
        super.show();
    }

    public void hide() {
        super.hide();
        gui.welcomePaneController.openPixivPane();
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
    }

    public void sendToDownloadBtnOnAction() {
        for (PixivArtwork data : data) {
            gui.pixivDownloadPaneController.getData().add(new PixivDownload(data));
        }
        data.clear();

        Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.menu.notice.sent"), gui.mainPane);
    }

    @javafx.fxml.FXML
    public void removeSelectedBtnOnAction() {
        int a = data.size();
        data.removeAll(dataTable.getSelectionModel().getSelectedValues());
        dataTable.getSelectionModel().clearSelection();
        Notice.showSuccess(
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("gui.fetch.notice.removeCompleted")),
                        a - data.size()),
                gui.mainPane);
    }

    @javafx.fxml.FXML
    public void sendSelectedToDownloadBtnOnAction() {
        int a = data.size();
        dataTable.getSelectionModel().getSelectedValues().forEach(data -> gui.pixivDownloadPaneController.getData().add(new PixivDownload(data)));
        data.removeAll(dataTable.getSelectionModel().getSelectedValues());
        dataTable.getSelectionModel().clearSelection();
        Notice.showSuccess(
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.notice.sent")), a - data.size()),
                gui.mainPane
        );
    }

    @javafx.fxml.FXML
    public void clearSelected() {
        int i = dataTable.getSelectionModel().getSelectedValues().size();
        dataTable.getSelectionModel().clearSelection();
        Notice.showSuccess(
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.notice.clearSelected")),
                        i),
                gui.mainPane);
    }

    @javafx.fxml.FXML
    public void copySelected() {
        StringBuilder sb = new StringBuilder();
        for (PixivArtwork s : dataTable.getSelectionModel().getSelectedValues()) {
            sb.append(PixivFetchUtil.getArtworkPageUrl(s)).append("\n");
        }
        if (sb.length() > 0) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
            Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.download.copied"), gui.mainPane);
        }
    }

    public void fetchBtnOnAction() {
        if (uidField.getText().startsWith("https://www.pixiv.net/users/"))
            uidField.setText(uidField.getText().substring(uidField.getText().lastIndexOf("/") + 1));
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

                Set<String> artIDs = PixivFetchUtil.fetchUser(
                        uidField.getText(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                );
                List<String> queryString = PixivFetchUtil.buildQueryString(artIDs);

                LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();

                for (String s : queryString) {
                    pixivArtworks.addAll(
                            PixivFetchUtil.getUserArtworks(s, uidField.getText(), ConfigManager.getConfig().getString("proxyHost"),
                                    ConfigManager.getConfig().getInteger("proxyPort")));
                }

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
                Notice.showSuccess(
                        String.format(
                                Objects.requireNonNull(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetched"))
                                , pixivArtworks.size()
                        ),
                        gui.mainPane
                );
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
