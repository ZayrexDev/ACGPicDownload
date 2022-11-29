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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PixivMenuPaneController implements Initializable {
    GUI gui;
    TranslateTransition tt = new TranslateTransition();
    @javafx.fxml.FXML
    private MFXSlider maxCountSlider;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;
    @javafx.fxml.FXML
    private MFXToggleButton fromFollowToggle;
    @javafx.fxml.FXML
    private MFXToggleButton fromRecToggle;
    @javafx.fxml.FXML
    private MFXToggleButton fromOtherToggle;
    @javafx.fxml.FXML
    private AnchorPane loadingPane;
    @javafx.fxml.FXML
    private Label operationLabel;
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private MFXButton backBtn;
    @javafx.fxml.FXML
    private MFXButton cookieHelpBtn;
    @javafx.fxml.FXML
    private MFXTableView<PixivArtwork> dataTable;
    private ObservableList<PixivArtwork> data;
    @javafx.fxml.FXML
    private MFXTextField cookieField;


    @javafx.fxml.FXML
    public void backBtnOnAction() {
        hide();
    }

    @javafx.fxml.FXML
    public void cookieHelpBtnOnAction() {

    }

    @javafx.fxml.FXML
    public void submitCookie() {

    }

    public void show() {
        tt.stop();
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
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
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.setFromY(0);
        tt.setToY(mainPane.getHeight());
        mainPane.setVisible(true);
        tt.setOnFinished((e) -> Platform.runLater(() -> mainPane.setVisible(false)));
        tt.play();
        gui.welcomePaneController.playAnimation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.setVisible(false);
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01);
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        initTable();

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("mfx-info-circle"));
    }

    private void initTable() {
        data = FXCollections.observableArrayList(new PixivArtwork());

        MFXTableColumn<PixivArtwork> titleColumn = new MFXTableColumn<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.title"), true);
        MFXTableColumn<PixivArtwork> fromColumn = new MFXTableColumn<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.from"), true);
        MFXTableColumn<PixivArtwork> tagColumn = new MFXTableColumn<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.tag"), true);

        titleColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTitle));
        fromColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getFrom));
        tagColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTagsString));

        titleColumn.setAlignment(Pos.CENTER);
        fromColumn.setAlignment(Pos.CENTER);
        tagColumn.setAlignment(Pos.CENTER);

        titleColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.5).get());
        fromColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.4).get());
        tagColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());

        dataTable.getTableColumns().addAll(List.of(titleColumn, fromColumn, tagColumn));

        dataTable.getFilters().addAll(List.of(
                new StringFilter<>(ResourceBundleUtil.getString("gui.fetch.table.column.fileName"), PixivArtwork::getTitle),
                new StringFilter<>(ResourceBundleUtil.getString("gui.fetch.table.column.link"), o -> o.getFrom().toString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.fetch.table.column.status"), PixivArtwork::getTagsString))
        );

        dataTable.setItems(data);

        dataTable.getSelectionModel().setAllowsMultipleSelection(false);

        data.clear();
    }


    public void fetchBtnOnAction() {
        try {
            List<PixivArtwork> pixivArtworks = PixivFetchUtil.selectArtworks(
                    PixivFetchUtil.fetchMenu(cookieField.getText(), gui.getSettingsPaneController().getProxyHost(), gui.getSettingsPaneController().getProxyPort()),
                    (int) maxCountSlider.getValue(),
                    fromFollowToggle.isSelected(),
                    fromRecToggle.isSelected(),
                    fromOtherToggle.isSelected()
            );

            data.addAll(pixivArtworks);
        } catch (IOException e) {
            Main.logError(e);
            gui.showError(e);
        }
    }

    public MFXSlider getMaxCountSlider() {
        return maxCountSlider;
    }

    public void setMaxCountSlider(MFXSlider maxCountSlider) {
        this.maxCountSlider = maxCountSlider;
    }

    public MFXSlider getRelatedDepthSlider() {
        return relatedDepthSlider;
    }

    public void setRelatedDepthSlider(MFXSlider relatedDepthSlider) {
        this.relatedDepthSlider = relatedDepthSlider;
    }

    public MFXToggleButton getFromFollowToggle() {
        return fromFollowToggle;
    }

    public void setFromFollowToggle(MFXToggleButton fromFollowToggle) {
        this.fromFollowToggle = fromFollowToggle;
    }

    public MFXToggleButton getFromRecToggle() {
        return fromRecToggle;
    }

    public void setFromRecToggle(MFXToggleButton fromRecToggle) {
        this.fromRecToggle = fromRecToggle;
    }

    public MFXToggleButton getFromOtherToggle() {
        return fromOtherToggle;
    }

    public void setFromOtherToggle(MFXToggleButton fromOtherToggle) {
        this.fromOtherToggle = fromOtherToggle;
    }

    public AnchorPane getLoadingPane() {
        return loadingPane;
    }

    public void setLoadingPane(AnchorPane loadingPane) {
        this.loadingPane = loadingPane;
    }

    public Label getOperationLabel() {
        return operationLabel;
    }

    public void setOperationLabel(Label operationLabel) {
        this.operationLabel = operationLabel;
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void sendToDownloadBtnOnAction() {
    }
}
