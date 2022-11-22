package xyz.zcraft.acgpicdownload.gui.scenes;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadResult;
import xyz.zcraft.acgpicdownload.util.fetchutil.FetchUtil;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;
import xyz.zcraft.acgpicdownload.util.sourceutil.Source;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class FetchSceneController implements Initializable {
    TranslateTransition tt = new TranslateTransition();
    TransformableList<Source> sourceTransformableList;
    FadeTransition ft = new FadeTransition();
    @javafx.fxml.FXML
    private MFXFilterComboBox<Source> sourcesComboBox;
    private ObservableList<Source> sources;
    private GUI gui;
    @javafx.fxml.FXML
    private MFXButton fetchBtn;
    @javafx.fxml.FXML
    private MFXButton downloadBtn;
    @javafx.fxml.FXML
    private MFXButton sourceUpdateBtn;
    @javafx.fxml.FXML
    private AnchorPane lodingPane;
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private Label operationLabel;
    @javafx.fxml.FXML
    private MFXTableView<DownloadResult> dataTable;
    private ObservableList<DownloadResult> data;
    @javafx.fxml.FXML
    private MFXSlider timesSlider;
    @javafx.fxml.FXML
    private MFXTextField outputDirField;
    @javafx.fxml.FXML
    private MFXToggleButton multiThreadToggle;
    @javafx.fxml.FXML
    private MFXTextField argumentField;

    @javafx.fxml.FXML
    public void fetchBtnOnAction() {
        lodingPane.setVisible(true);
        operationLabel.setText("抓取中");
        ft = new FadeTransition();
        ft.setNode(lodingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));
        ft.play();
        new Thread(() -> {
            LinkedList<DownloadResult> r = new LinkedList<>();
            Source s = sourcesComboBox.getSelectedItem();
            HashMap<String, String> arg = new HashMap<>();
            if (argumentField.getText() != null && !argumentField.getText().trim().equalsIgnoreCase(" ")) {
                String[] t = argumentField.getText().split(" ");
                for (String str : t) {
                    int i = str.indexOf("=");
                    if (i == -1) {
                        continue;
                    }
                    String key = str.substring(0, i);
                    String value = str.substring(i + 1);
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    arg.put(key, value);
                }
            }
            FetchUtil.replaceArgument(s, arg);
            ArrayList<Result> r1 = FetchUtil.fetch(
                    s,
                    (int) timesSlider.getValue(),
                    new Logger("GUI", System.out),
                    true,
                    null, 0
            );
            for (Result result : r1) {
                DownloadResult dr = new DownloadResult();
                dr.setResult(result);
                r.add(dr);
            }
            Platform.runLater(() -> {
                ft.setFromValue(1);
                ft.setToValue(0);
                data.addAll(r);
                ft.setOnFinished((e) -> lodingPane.setVisible(false));
                ft.play();
            });
        }).start();
    }

    @javafx.fxml.FXML
    public void downloadBtnOnAction() {
        FetchUtil.startDownloadWithResults(
                new ArrayList<>(data),
                Objects.equals(outputDirField.getText(), "") ? new File("").getAbsolutePath() : outputDirField.getText(),
                new Logger("GUI", System.out),
                false,
                true,
                -1,
                () -> Platform.runLater(() -> dataTable.update())
        );
    }

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

        ft.setNode(lodingPane);
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

        MFXTableColumn<DownloadResult> titleColumn = new MFXTableColumn<>("文件名", true);
        MFXTableColumn<DownloadResult> linkColumn = new MFXTableColumn<>("下载链接", true);
        MFXTableColumn<DownloadResult> statusColumn = new MFXTableColumn<>("状态", true);

        titleColumn.setRowCellFactory(arg0 -> new MFXTableRowCell<>(arg01 -> arg01.getResult().getFileName()));
        linkColumn.setRowCellFactory(arg0 -> new MFXTableRowCell<>(arg01 -> arg01.getResult().getUrl()));
        statusColumn.setRowCellFactory(arg0 -> new MFXTableRowCell<>(DownloadResult::getStatusString));

        titleColumn.setAlignment(Pos.CENTER);
        linkColumn.setAlignment(Pos.CENTER);
        statusColumn.setAlignment(Pos.CENTER);

        titleColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.5));
        linkColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.4));
        statusColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));

        dataTable.getTableColumns().addAll(List.of(titleColumn, linkColumn, statusColumn));

        dataTable.getFilters().addAll(
                List.of(
                        new StringFilter<>("文件名", arg0 -> arg0.getResult().getFileName()),
                        new StringFilter<>("下载链接", arg0 -> arg0.getResult().getUrl()),
                        new StringFilter<>("状态", DownloadResult::getStatusString)
                )
        );

        dataTable.setItems(data);

        data.clear();

        fetchBtn.disableProperty().bind(sourcesComboBox.selectedIndexProperty().isEqualTo(-1));
        data.addListener((ListChangeListener<DownloadResult>) c -> downloadBtn.setDisable(data.size() == 0));
    }

    @javafx.fxml.FXML
    public void sourceUpdateBtnOnAction() {
        updateSource();
    }

    private void updateSource() {
        sourcesComboBox.getItems().clear();

        lodingPane.setVisible(true);
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
            ft.setOnFinished((e) -> lodingPane.setVisible(false));
        }).start();

        ft.setFromValue(1);
        ft.setToValue(0);
    }
}