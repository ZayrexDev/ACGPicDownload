package xyz.zcraft.acgpicdownload.gui.scenes;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadResult;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;
import xyz.zcraft.acgpicdownload.util.sourceutil.Source;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceManager;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class FetchSceneController implements Initializable {
    public MFXFilterComboBox<Source> sourcesComboBox;
    TranslateTransition tt = new TranslateTransition();
    private ObservableList<Source> sources;
    private GUI gui;
    @javafx.fxml.FXML
    private MFXButton sourceUpdateBtn;
    @javafx.fxml.FXML
    private HBox sourceUpdatePane;
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private Label operationLabel;
    @javafx.fxml.FXML
    private MFXTableView<DownloadResult> dataTable;
    private ObservableList<DownloadResult> data;
    private MFXTableColumn<DownloadResult> titleColumn;
    private MFXTableColumn<DownloadResult> linkColumn;
    private MFXTableColumn<DownloadResult> statusColumn;

    TransformableList<Source> sourceTransformableList;

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void show() {
        tt.setFromY(mainPane.getHeight());
        tt.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.setVisible(false);
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01);
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        ft.setNode(sourceUpdatePane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        StringConverter<Source> converter = new StringConverter<>() {
            @Override
            public String toString(Source source) {
                return source == null ? null : source.getName();
            }

            @Override
            public Source fromString(String s) {
                return null;
            }
        };
        sourcesComboBox.setConverter(converter);

        sources = sourcesComboBox.getItems();
        sourceTransformableList = sourcesComboBox.getFilterList();

        sourcesComboBox.setFilterFunction(s -> source -> StringUtils.containsIgnoreCase(converter.toString(source), s));

        data = FXCollections.observableArrayList(new DownloadResult());

        titleColumn = new MFXTableColumn<>("文件名", true);
        linkColumn = new MFXTableColumn<>("下载链接", true);
        statusColumn = new MFXTableColumn<>("状态", true);

        titleColumn.setRowCellFactory(arg0 -> new MFXTableRowCell<>(arg01 -> arg01.getResult().getFileName()));
        linkColumn.setRowCellFactory(arg0 -> new MFXTableRowCell<>(arg01 -> arg01.getResult().getUrl()));
        statusColumn.setRowCellFactory(arg0 -> new MFXTableRowCell<>(arg01 -> arg01.getStatus()));

        titleColumn.setAlignment(Pos.CENTER);
        linkColumn.setAlignment(Pos.CENTER);
        statusColumn.setAlignment(Pos.CENTER);

        titleColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.5));
        linkColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.4));
        statusColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));

        dataTable.getTableColumns().addAll(List.of(titleColumn,linkColumn,statusColumn));

        dataTable.getFilters().addAll(List.of(
            new StringFilter<>("文件名", arg0 -> arg0.getResult().getFileName()),
            new StringFilter<>("下载链接", arg0 -> arg0.getResult().getUrl()),
            new StringFilter<>("状态", arg0 -> arg0.getStatus().toString())
        ));

        dataTable.setItems(data);

        data.clear();
    }

    @javafx.fxml.FXML
    public void sourceUpdateBtnOnAction() {
        updateSource();
    }

    FadeTransition ft = new FadeTransition();

    private void updateSource() {
        sourcesComboBox.getItems().clear();

        sourceUpdatePane.setVisible(true);
        ft.play();

        new Thread(() -> {
            try {
                SourceManager.readConfig();
                sources.clear();
                sources.addAll(SourceManager.getSources());
                Thread.sleep(2000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            ft.play();
            ft.setOnFinished((e) -> sourceUpdatePane.setVisible(false));
        }).start();

        ft.setFromValue(1);
        ft.setToValue(0);
    }
}
