package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownload;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownloadUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PixivDownloadPaneController implements Initializable {
    public MFXButton backBtn;
    GUI gui;
    TranslateTransition tt = new TranslateTransition();
    @FXML
    private MFXTableView<PixivDownload> dataTable;
    @FXML
    private AnchorPane mainPane;
    private ObservableList<PixivDownload> data;
    @FXML
    private MFXTextField outputDirField;
    @FXML
    private MFXButton selectDirBtn;
    @FXML
    private MFXSlider threadCountSlider;

    public void startDownload() {
        PixivDownloadUtil.startDownload(
                data,
                outputDirField.getText(),
                new Logger("GUI", System.out, Main.log),
                (int) threadCountSlider.getValue(),
                this::updateStatus,
                gui.pixivMenuPaneController.getCookie(),
                ConfigManager.getConfig().getString("proxyHost"),
                ConfigManager.getConfig().getInteger("proxyPort")
        );
    }

    private void updateStatus() {
        dataTable.update();
    }

    public void delCompleted() {
        int a = data.size();
        data.removeIf(datum -> datum.getStatus() == DownloadStatus.COMPLETED);
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
        Notice.showSuccess(String.format(Objects.requireNonNull(ResourceBundleUtil.getString("gui.fetch.notice.removeCompleted")), a - data.size()), gui.mainPane);
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public ObservableList<PixivDownload> getData() {
        return data;
    }

    public void show() {
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
        tt.stop();
        tt.setFromY(mainPane.getHeight());
        tt.setOnFinished(null);
        tt.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    public void backBtnOnAction() {
        tt.stop();
        tt.setNode(mainPane);
        tt.setFromY(0);
        tt.setToY(mainPane.getHeight());
        mainPane.setVisible(true);
        tt.setOnFinished((e) -> Platform.runLater(() -> mainPane.setVisible(false)));
        tt.play();
        gui.welcomePaneController.playAnimation();
    }

    private void initTable() {
        data = FXCollections.observableArrayList(new PixivDownload(new PixivArtwork()));

        MFXTableColumn<PixivDownload> titleColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.title"), true);
        MFXTableColumn<PixivDownload> fromColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.from"), true);
        MFXTableColumn<PixivDownload> tagColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.tag"), true);
        MFXTableColumn<PixivDownload> idColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.id"), true);
        MFXTableColumn<PixivDownload> statusColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.status"), true);

        titleColumn.setRowCellFactory(e -> new MFXTableRowCell<>(o -> o.getArtwork().getTitle()));
        fromColumn.setRowCellFactory(e -> new MFXTableRowCell<>(o -> o.getArtwork().getFrom().toString()));
        tagColumn.setRowCellFactory(e -> new MFXTableRowCell<>(o -> o.getArtwork().getTagsString()));
        idColumn.setRowCellFactory(e -> new MFXTableRowCell<>(o -> o.getArtwork().getId()));
        statusColumn.setRowCellFactory(e -> new MFXTableRowCell<>(o -> o.getStatus().toString()));

        titleColumn.setAlignment(Pos.CENTER);
        fromColumn.setAlignment(Pos.CENTER);
        tagColumn.setAlignment(Pos.CENTER);
        idColumn.setAlignment(Pos.CENTER);
        statusColumn.setAlignment(Pos.CENTER);

        titleColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.4).get());
        fromColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());
        tagColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.3).get());
        idColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());
        statusColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());

        dataTable.getTableColumns().addAll(List.of(titleColumn, fromColumn, tagColumn, idColumn, statusColumn));

        dataTable.getFilters().addAll(List.of(
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.title"), o -> o.getArtwork()
                        .getTitle()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.from"),
                        o -> o.getArtwork().getFrom()
                                .toString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.tag"),
                        o -> o.getArtwork()
                                .getTagsString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.id"),
                        o -> o.getArtwork().getId()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.status"),
                        o -> o.getStatus().toString())));

        dataTable.setItems(data);

        dataTable.getSelectionModel().selectionProperty()
                .addListener((observableValue, integerDownloadResultObservableMap, t1) -> {
                    List<PixivDownload> selectedValues = dataTable.getSelectionModel().getSelectedValues();
                    if (selectedValues.size() > 0) {
                        Toolkit.getDefaultToolkit().getSystemClipboard()
                                .setContents(
                                        new StringSelection(PixivFetchUtil.getArtworkPageUrl(selectedValues.get(0).getArtwork())),
                                        null);
                        dataTable.getSelectionModel().clearSelection();
                        Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.download.copied"), gui.mainPane);
                    }
                });

        dataTable.getSelectionModel().setAllowsMultipleSelection(false);

        data.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.setVisible(false);
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01);
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        initTable();

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        selectDirBtn.setText("");
        selectDirBtn.setGraphic(new MFXFontIcon("mfx-folder"));
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
}
