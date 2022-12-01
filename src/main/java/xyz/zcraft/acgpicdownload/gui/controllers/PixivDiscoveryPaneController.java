package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class PixivDiscoveryPaneController implements Initializable{
    GUI gui;
    TranslateTransition tt = new TranslateTransition();
    FadeTransition ft = new FadeTransition();
    @javafx.fxml.FXML
    private MFXSlider maxCountSlider;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;
    @javafx.fxml.FXML
    private AnchorPane loadingPane;
    @javafx.fxml.FXML
    private Label operationLabel;
    @javafx.fxml.FXML
    private Label subOperationLabel;
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private MFXButton backBtn;
    @javafx.fxml.FXML
    private MFXButton cookieHelpBtn;
    @javafx.fxml.FXML
    private MFXComboBox<String> modeCombo;
    @javafx.fxml.FXML
    private MFXTableView<PixivArtwork> dataTable;
    private ObservableList<PixivArtwork> data;
    @javafx.fxml.FXML
    private MFXTextField cookieField;

    public String getCookie() {
        return cookieField.getText();
    }

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
    public void submitCookie() {
        try {
            JSONObject json = Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"),
                    new JSONObject());
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
        tt.stop();
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
        tt.setFromY(mainPane.getHeight());
        tt.setOnFinished(null);
        tt.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    public void hide() {
        tt.stop();
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01);
        tt.setDuration(Duration.millis(5));
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.setFromY(0);
        tt.setToY(mainPane.getHeight());
        mainPane.setVisible(true);
        tt.setOnFinished((e) -> Platform.runLater(() -> mainPane.setVisible(false)));
        tt.play();
        gui.welcomePaneController.openPixivPane();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.setVisible(false);
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        initTable();

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("mfx-info-circle"));

        modeCombo.getItems().addAll(
                ResourceBundleUtil.getString("gui.pixiv.disc.mode.all"),
                ResourceBundleUtil.getString("gui.pixiv.disc.mode.safe"),
                ResourceBundleUtil.getString("gui.pixiv.disc.mode.adult")
        );

        modeCombo.getSelectionModel().selectFirst();

        cookieField.textProperty().addListener((observableValue, s, t1) -> ConfigManager.getTempConfig().put("cookie", t1));
        cookieField.setText(Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject()).getString("cookie"));
    }

    private void initTable() {
        data = FXCollections.observableArrayList(new PixivArtwork());

        MFXTableColumn<PixivArtwork> titleColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.title"), true);
        MFXTableColumn<PixivArtwork> fromColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.from"), true);
        MFXTableColumn<PixivArtwork> tagColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.tag"), true);
        MFXTableColumn<PixivArtwork> idColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.id"), true);

        titleColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTitle));
        fromColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getFrom));
        tagColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getOriginalTagsString));
        idColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getId));

        titleColumn.setAlignment(Pos.CENTER);
        fromColumn.setAlignment(Pos.CENTER);
        tagColumn.setAlignment(Pos.CENTER);
        idColumn.setAlignment(Pos.CENTER);

        titleColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.4).get());
        fromColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.4).get());
        tagColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());
        idColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());

        dataTable.getTableColumns().addAll(List.of(titleColumn, fromColumn, tagColumn, idColumn));

        dataTable.getFilters().addAll(List.of(
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.title"), PixivArtwork::getTitle),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.from"),
                        o -> o.getFrom().toString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.tag"),
                        PixivArtwork::getOriginalTagsString),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.id"), PixivArtwork::getId)));

        dataTable.setItems(data);
        dataTable.getSelectionModel().setAllowsMultipleSelection(true);
        data.clear();
    }

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

                List<PixivArtwork> pixivArtworks = PixivFetchUtil.getDiscovery(
                        modeCombo.getSelectedIndex(),
                        (int)maxCountSlider.getValue(),
                        cookieField.getText(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                    );

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
                                    Objects.requireNonNullElse(ConfigManager.getConfig().getString("proxyHost"), null),
                                    Objects.requireNonNullElse(ConfigManager.getConfig().getInteger("proxyPort"), 0));
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

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
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
        for (PixivArtwork data : dataTable.getSelectionModel().getSelectedValues()) {
            gui.pixivDownloadPaneController.getData().add(new PixivDownload(data));
        }
        data.removeAll(dataTable.getSelectionModel().getSelectedValues());
        Notice.showSuccess(
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("fetch.pixiv.notice.sent")),
                        a - data.size()),
                gui.mainPane);
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
}
