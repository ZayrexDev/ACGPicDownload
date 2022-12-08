package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.MyPane;
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
import java.util.*;

public class PixivRankingPaneController extends MyPane {
    private static final String[][] MAJORS = {
            {"daily", "daily_r18"}, {"weekly", "weekly_r18"},
            {"monthly"}, {"rookie"}, {"original"},
            {"daily_ai", "daily_r18_ai"}, {"male", "male_r18"}, {"female", "female_r18"}
    };
    private final ObservableList<PixivArtwork> data = FXCollections.observableArrayList();
    private final LinkedList<String> minors = new LinkedList<>();
    public MFXComboBox<String> majorCombo;
    public MFXComboBox<String> minorCombo;
    public MFXToggleButton resToggle;
    FadeTransition ft = new FadeTransition();
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private MFXButton backBtn;
    @javafx.fxml.FXML
    private MFXTextField cookieField;
    @javafx.fxml.FXML
    private MFXButton cookieHelpBtn;
    @javafx.fxml.FXML
    private MFXTableView<PixivArtwork> dataTable;
    @javafx.fxml.FXML
    private AnchorPane loadingPane;
    @javafx.fxml.FXML
    private Label operationLabel;
    @javafx.fxml.FXML
    private Label subOperationLabel;

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

        majorCombo.getItems().addAll(
                ResourceBundleUtil.getString("gui.pixiv.ranking.daily"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.weekly"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.monthly"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.rookie"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.original"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.daily_ai"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.male"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.female")
        );

        majorCombo.selectedIndexProperty().addListener((observableValue, number, t1) -> {
            int i = t1.intValue();
            if(i == 0 || i == 1 || i == 5 || i == 6 || i == 7){
                resToggle.setDisable(false);
            }else{
                resToggle.setSelected(false);
                resToggle.setDisable(true);
            }
            minorCombo.getItems().clear();
            minors.clear();
            minors.add("");
            minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.all"));
            if (i == 0 || i == 1 || i == 2 || i == 3) {
                minors.add("illust");
                minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.illust"));
            }
            if (i == 0 || i == 1) {
                minors.add("ugoira");
                minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.ugoira"));
            }
            if (i == 0 || i == 1 || i == 2 || i == 3) {
                minors.add("manga");
                minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.manga"));
            }

            minorCombo.selectFirst();
        });

        majorCombo.selectFirst();
        minorCombo.selectFirst();
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
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.notice.clearSelected")), i),
                gui.mainPane
        );
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
        var major = MAJORS[majorCombo.getSelectedIndex()][resToggle.isSelected() ? 1 : 0];
        var minor = minors.get(minorCombo.getSelectedIndex());

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

                LinkedList<String> ids = PixivFetchUtil.getRankingIDs(
                        major,
                        minor,
                        cookieField.getText(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                );

                int[] i = {0,0};
                for (; i[0] < ids.size(); i[0]++) {
                    Platform.runLater(()->subOperationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.ranking.notice.getting") + " " + (i[0] + 1) + "/" + ids.size() + " " + (i[1]+1)));
                    try{
                        pixivArtworks.add(
                                PixivFetchUtil.getArtwork(
                                        ids.get(i[0]),
                                        cookieField.getText(),
                                        ConfigManager.getConfig().getString("proxyHost"),
                                        ConfigManager.getConfig().getInteger("proxyPort")
                                )
                        );
                        i[1] = 0;
                    }catch(Exception e){
                        i[1]++;
                        i[0]--;
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
