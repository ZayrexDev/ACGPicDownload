package xyz.zcraft.acgpicdownload.gui.base;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import com.alibaba.fastjson2.JSONObject;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownload;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

public abstract class PixivFetchPane extends MyPane{
    abstract public void fetchBtnOnAction();

    protected FadeTransition ft = new FadeTransition();
    protected final ObservableList<PixivArtwork> data = FXCollections.observableArrayList();
    @javafx.fxml.FXML
    protected AnchorPane loadingPane;
    @javafx.fxml.FXML
    protected Label operationLabel;
    @javafx.fxml.FXML
    protected Label subOperationLabel;
    @javafx.fxml.FXML
    protected AnchorPane mainPane;
    @javafx.fxml.FXML
    protected MFXButton backBtn;
    @javafx.fxml.FXML
    protected MFXButton cookieHelpBtn;
    @javafx.fxml.FXML
    protected MFXTableView<PixivArtwork> dataTable;
    @javafx.fxml.FXML
    protected MFXTextField cookieField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        GUI.initTable(data, dataTable);
    }

    @javafx.fxml.FXML
    public void backBtnOnAction() {
        hide();
    }

    @javafx.fxml.FXML
    public void backToMenu() {
        super.hide();
        gui.welcomePaneController.showMain();
    }

    public void show() {
        cookieField.setText(ConfigManager.getTempConfig().get("cookie"));
        super.show();
    }

    public void hide() {
        super.hide();
        gui.welcomePaneController.openPixivPane();
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

    @javafx.fxml.FXML
    public void sendSelectedToDownloadBtnOnAction() {
        int a = data.size();
        for (PixivArtwork data : dataTable.getSelectionModel().getSelectedValues()) {
            gui.pixivDownloadPaneController.getData().add(new PixivDownload(data));
        }
        data.removeAll(dataTable.getSelectionModel().getSelectedValues());
        dataTable.getSelectionModel().clearSelection();
        Notice.showSuccess(
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.notice.sent")),
                        a - data.size()),
                gui.mainPane);
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
    public void cookieHelpBtnOnAction() throws IOException, URISyntaxException {
        if (Locale.getDefault().equals(Locale.CHINA) || Locale.getDefault().equals(Locale.TAIWAN)) {
            java.awt.Desktop.getDesktop()
                    .browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/%E8%8E%B7%E5%8F%96Cookie"));
        } else {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/Get-cookie"));
        }
    }

    @javafx.fxml.FXML
    public void submitCookie() {
        try {
            JSONObject json = Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"),
                    new JSONObject());
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
}
