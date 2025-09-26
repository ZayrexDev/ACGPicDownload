package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import lombok.Getter;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.gui.base.MyPane;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.dl.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.pixiv.*;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class PixivDownloadPaneController extends MyPane {
    public MFXTextField namingRuleField;
    public MFXComboBox<String> multiPageCombo;
    public MFXTextField folderNamingRuleField;
    public MFXTextField bookmarkCountField;
    public MFXTextField likeCountField;
    public MFXButton namingRuleHelpBtn;
    public MFXButton saveConfigBtn;
    @FXML
    private MFXTableView<PixivDownload> dataTable;
    @FXML
    private AnchorPane mainPane;
    @Getter
    private volatile ObservableList<PixivDownload> data;
    @FXML
    private MFXTextField outputDirField;
    @FXML
    private MFXButton selectDirBtn;
    @FXML
    private MFXSlider threadCountSlider;
    @FXML
    private MFXToggleButton fullResultToggle;
    @FXML
    private MFXButton backBtn;
    @FXML
    private Label statusLabel;

    public void startDownload() {
        ArtworkCondition condition = ArtworkCondition.always();
        try{condition.bookmark(Integer.parseInt(bookmarkCountField.getText()));}catch (NumberFormatException ignored){}
        try{condition.like(Integer.parseInt(likeCountField.getText()));}catch (NumberFormatException ignored){}

        PixivDownloadUtil.startDownload(
                data,
                outputDirField.getText(),
                new Logger("GUI", System.out, Main.log),
                (int) threadCountSlider.getValue(),
                ConfigManager.getSelectedAccount().getCookie(),
                new NamingRule(namingRuleField.getText(), multiPageCombo.getSelectedIndex(), folderNamingRuleField.getText()),
                fullResultToggle.isSelected(),
                ConfigManager.getConfig().getString("proxyHost"),
                ConfigManager.getConfig().getInteger("proxyPort"),
                condition
        );
    }

    @javafx.fxml.FXML
    public void backToMenu() {
        super.hide();
        gui.menuPaneController.showMain();
    }

    private void updateStatus() {
        Platform.runLater(() -> dataTable.update());
        String sb = ResourceBundleUtil.getString("gui.download.status.created") + data.filtered((e) -> e.getStatus() == DownloadStatus.CREATED).size() + " "
                + ResourceBundleUtil.getString("gui.download.status.init") + data.filtered((e) -> e.getStatus() == DownloadStatus.INITIALIZE).size() + " "
                + ResourceBundleUtil.getString("gui.download.status.started") + data.filtered((e) -> e.getStatus() == DownloadStatus.STARTED).size() + " "
                + ResourceBundleUtil.getString("gui.download.status.completed") + data.filtered((e) -> e.getStatus() == DownloadStatus.COMPLETED).size() + " "
                + ResourceBundleUtil.getString("gui.download.status.failed") + data.filtered((e) -> e.getStatus() == DownloadStatus.FAILED).size() + " "
                + ResourceBundleUtil.getString("gui.download.status.filtered") + data.filtered((e) -> e.getStatus() == DownloadStatus.FILTERED).size();
        Platform.runLater(() -> statusLabel.setText(sb));
    }

    public void delCompleted() {
        int a = data.size();
        data.removeIf(datum -> datum.getStatus() == DownloadStatus.COMPLETED || datum.getStatus() == DownloadStatus.FILTERED);
        updateStatus();
        Notice.showSuccess(
                String.format(
                        Objects.requireNonNull(ResourceBundleUtil.getString("gui.fetch.notice.removeCompleted")),
                        a - data.size()
                ),
                gui.mainPane
        );
    }

    public void delSelected() {
        int a = data.size();
        data.removeAll(dataTable.getSelectionModel().getSelectedValues());
        dataTable.getSelectionModel().clearSelection();
        updateStatus();
        Notice.showSuccess(String.format(Objects.requireNonNull(ResourceBundleUtil.getString("gui.fetch.notice.removeCompleted")), a - data.size()), gui.mainPane);
    }

    public void show() {
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
        super.show();
    }

    public void backBtnOnAction() {
        super.hide();
        gui.pixivPaneController.openPixivPane();
    }

    private void initTable() {
        data = FXCollections.observableArrayList(new PixivDownload(new PixivArtwork()));

        MFXTableColumn<PixivDownload> titleColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.title"), true);
        MFXTableColumn<PixivDownload> authorColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.menu.column.author"), true);
        MFXTableColumn<PixivDownload> fromColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.from"), true);
        MFXTableColumn<PixivDownload> tagColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.tag"), true);
        MFXTableColumn<PixivDownload> idColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.id"), true);
        MFXTableColumn<PixivDownload> statusColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.status"), true);
        MFXTableColumn<PixivDownload> typeColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.type"), true);

        titleColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(o -> o.getArtwork().getTitle()));
        authorColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(o -> o.getArtwork().getUserName()));
        fromColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(o -> o.getArtwork().getFromString()));
        tagColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(o -> o.getArtwork().getTagsString()));
        idColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(o -> o.getArtwork().getId()));
        statusColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(PixivDownload::getStatusString));
        typeColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(o -> o.getArtwork().getTypeString()));

        titleColumn.setAlignment(Pos.CENTER);
        authorColumn.setAlignment(Pos.CENTER);
        fromColumn.setAlignment(Pos.CENTER);
        tagColumn.setAlignment(Pos.CENTER);
        idColumn.setAlignment(Pos.CENTER);
        statusColumn.setAlignment(Pos.CENTER);
        typeColumn.setAlignment(Pos.CENTER);

        dataTable.getTableColumns().addAll(List.of(titleColumn, authorColumn, fromColumn, tagColumn, idColumn, statusColumn, typeColumn));

        dataTable.getFilters().addAll(List.of(
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.title"), o -> o.getArtwork()
                        .getTitle()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.author"), o -> o.getArtwork()
                        .getUserName()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.from"),
                        o -> o.getArtwork().getFromString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.tag"),
                        o -> o.getArtwork()
                                .getTagsString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.id"),
                        o -> o.getArtwork().getId()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.status"),
                        o -> o.getStatus().toString())
                , new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.type"),
                        o -> o.getStatus().toString())));

        dataTable.setItems(data);
        dataTable.features().enableBounceEffect();
        dataTable.features().enableSmoothScrolling(0.7);
        data.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initTable();

        multiPageCombo.getItems().addAll(
                ResourceBundleUtil.getString("gui.pixiv.download.multiPageRule.separated"),
                ResourceBundleUtil.getString("gui.pixiv.download.multiPageRule.gathered")
        );

        multiPageCombo.selectedIndexProperty().addListener((_, _, t1) -> folderNamingRuleField.setDisable(t1.intValue() != 0));

        multiPageCombo.selectFirst();

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("fas-angle-down"));
        selectDirBtn.setText("");
        selectDirBtn.setGraphic(new MFXFontIcon("fas-folder"));
        namingRuleHelpBtn.setText("");
        namingRuleHelpBtn.setGraphic(new MFXFontIcon("fas-circle-info"));
        saveConfigBtn.setText("");
        saveConfigBtn.setGraphic(new ImageView(new Image(ResourceLoader.loadStream("icon/save.png"), 18, 18, true, false)));

        ScheduledService<Void> s = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        updateStatus();
                        return null;
                    }
                };
            }
        };

        s.setPeriod(Duration.seconds(1));
        s.start();

        restoreConfig();
    }

    @javafx.fxml.FXML
    public void selectDirBtnOnAction() {
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("...");
        File showDialog = fc.showDialog(gui.mainStage);
        if (showDialog != null) {
            outputDirField.setText(showDialog.getAbsolutePath());
        }
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
        for (PixivDownload s : dataTable.getSelectionModel().getSelectedValues()) {
            sb.append(PixivFetchUtil.getArtworkPageUrl(s.getArtwork())).append("\n");
        }
        if (!sb.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
            Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.download.copied"), gui.mainPane);
        }
    }

    public void namingRuleHelpBtnOnAction() throws URISyntaxException, IOException {
        if (Locale.getDefault().equals(Locale.CHINA) || Locale.getDefault().equals(Locale.TAIWAN)) {
            Desktop.getDesktop()
                    .browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/命名规则"));
        } else {
            Desktop.getDesktop().browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/Naming-rules"));
        }
    }

    public void restoreConfig() {
        JSONObject pixivConfig = Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject());
        JSONObject downloadConfig = Objects.requireNonNullElse(pixivConfig.getJSONObject("download"), new JSONObject());

        namingRuleField.setText(Objects.requireNonNullElse(downloadConfig.getString("namingRule"), "{$id}{_p$p}"));
        multiPageCombo.selectIndex(Objects.requireNonNullElse(downloadConfig.getInteger("multiPageRule"), 0));
        folderNamingRuleField.setText(Objects.requireNonNullElse(downloadConfig.getString("folderNamingRule"), "{$id}"));
        outputDirField.setText(Objects.requireNonNullElse(downloadConfig.getString("path"), ""));
        threadCountSlider.setValue(Objects.requireNonNullElse(downloadConfig.getInteger("thread"), 5));
        fullResultToggle.setSelected(downloadConfig.getBooleanValue("full"));
    }

    public void saveConfig() {
        JSONObject pixivConfig = Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject());
        JSONObject downloadConfig = new JSONObject();

        downloadConfig.put("namingRule", namingRuleField.getText());
        downloadConfig.put("multiPageRule", multiPageCombo.getSelectedIndex());
        downloadConfig.put("folderNamingRule", folderNamingRuleField.getText());
        downloadConfig.put("path", outputDirField.getText());
        downloadConfig.put("thread", (int) threadCountSlider.getValue());
        downloadConfig.put("full", fullResultToggle.isSelected());

        pixivConfig.put("download", downloadConfig);
        ConfigManager.getConfig().put("pixiv", pixivConfig);

        try {
            ConfigManager.saveConfig();
            Notice.showSuccess(ResourceBundleUtil.getString("gui.fetch.notice.saved"), gui.mainPane);
        } catch (IOException e) {
            gui.showError(e);
            Main.logError(e);
        }
    }
}
