package xyz.zcraft.acgpicdownload.gui.base;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownload;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class PixivFetchPane extends MyPane {
    protected final ObservableList<PixivArtwork> data = FXCollections.observableArrayList();
    protected final FadeTransition ft = new FadeTransition();
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
    protected MFXTableView<PixivArtwork> dataTable;

    public static void getRelated(List<PixivArtwork> pixivArtworks, int depth, String cookieString, Label subOperationLabel) throws IOException {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        if (depth > 0) {
            List<PixivArtwork> temp2Artworks = new LinkedList<>();
            List<PixivArtwork> temp = new LinkedList<>(pixivArtworks);
            int[] p = {0, 0, 0};
            for (; p[0] < depth; p[0]++) {
                Platform.runLater(() -> subOperationLabel
                        .setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchRel") + " "
                                + (p[0] + 1) + " / " + depth));
                temp2Artworks.clear();
                p[1] = 0;
                p[2] = 0;
                for (int tempSize = temp.size(); p[1] < tempSize; p[1]++) {
                    PixivArtwork temp2 = temp.get(p[1]);
                    tpe.execute(() -> {
                        List<PixivArtwork> related;
                        try {
                            related = PixivFetchUtil.getRelated(temp2, 18,
                                    cookieString,
                                    ConfigManager.getConfig().getString("proxyHost"),
                                    ConfigManager.getConfig().getInteger("proxyPort"));
                            temp2Artworks.addAll(related);
                            p[2]++;
                        } catch (IOException ignored) {
                        }
                    });
                }
                while (tpe.getActiveCount() != 0) {
                    Platform.runLater(() -> subOperationLabel
                            .setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchRel") + " "
                                    + (p[0] + 1) + " / " + depth + " | "
                                    + (p[2]) + " / " + temp.size()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                temp.clear();
                temp.addAll(temp2Artworks);
                pixivArtworks.addAll(temp2Artworks);
            }
        }
    }

    abstract public void fetchBtnOnAction();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        initTable();
    }

    public void initTable() {
        data.clear();
        data.add(new PixivArtwork());

        MFXTableColumn<PixivArtwork> titleColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.title"), true);
        MFXTableColumn<PixivArtwork> authorColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.menu.column.author"), true);
        MFXTableColumn<PixivArtwork> fromColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.from"), true);
        MFXTableColumn<PixivArtwork> tagColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.tag"), true);
        MFXTableColumn<PixivArtwork> idColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.id"), true);
        MFXTableColumn<PixivArtwork> typeColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.pixiv.download.column.type"), true);

        titleColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTitle));
        authorColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getUserName));
        fromColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getFromString));
        tagColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTagsString));
        idColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getId));
        typeColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTypeString));

        titleColumn.setAlignment(Pos.CENTER);
        authorColumn.setAlignment(Pos.CENTER);
        fromColumn.setAlignment(Pos.CENTER);
        tagColumn.setAlignment(Pos.CENTER);
        idColumn.setAlignment(Pos.CENTER);
        typeColumn.setAlignment(Pos.CENTER);

        dataTable.getTableColumns().addAll(List.of(titleColumn, authorColumn, fromColumn, tagColumn, idColumn,
                typeColumn));

        dataTable.getFilters().addAll(List.of(
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.title"),
                        PixivArtwork::getTitle),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.author"),
                        PixivArtwork::getUserName),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.from"),
                        PixivArtwork::getFromString),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.tag"),
                        PixivArtwork::getTagsString),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.id"),
                        PixivArtwork::getId),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.type"),
                        PixivArtwork::getTypeString))
        );

        dataTable.setItems(data);
        dataTable.getSelectionModel().setAllowsMultipleSelection(true);
        dataTable.features().enableBounceEffect();
        dataTable.features().enableSmoothScrolling(0.7);
        data.clear();
    }

    @javafx.fxml.FXML
    public void backBtnOnAction() {
        hide();
    }

    @javafx.fxml.FXML
    public void backToMenu() {
        super.hide();
        gui.menuPaneController.showMain();
    }

    public void hide() {
        super.hide();
        gui.pixivPaneController.openPixivPane();
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

    public String getCookie() {
        return ConfigManager.getSelectedAccount().getCookie();
    }
}
