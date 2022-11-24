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
import xyz.zcraft.acgpicdownload.gui.argpanes.ArgumentPane;
import xyz.zcraft.acgpicdownload.gui.argpanes.LimitedIntegerArgumentPane;
import xyz.zcraft.acgpicdownload.gui.argpanes.LimitedStringArgumentPane;
import xyz.zcraft.acgpicdownload.gui.argpanes.StringArgumentPane;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadManager;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadResult;
import xyz.zcraft.acgpicdownload.util.downloadutil.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.fetchutil.FetchUtil;
import xyz.zcraft.acgpicdownload.util.fetchutil.Result;
import xyz.zcraft.acgpicdownload.util.sourceutil.Source;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceManager;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.LimitedIntegerArgument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.LimitedStringArgument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.StringArgument;

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
    private AnchorPane loadingPane;
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
    private MFXButton delCompletedBtn;
    @javafx.fxml.FXML
    private MFXProgressBar progressBar;
    @javafx.fxml.FXML
    private Label statusLabel;

    private boolean downloading;
    private DownloadManager dm;
    @javafx.fxml.FXML
    private MFXButton backBtn;
    private final LinkedList<ArgumentPane<?>> arguments = new LinkedList<>();
    @javafx.fxml.FXML
    private HBox argumentsPane;

    @javafx.fxml.FXML
    public void downloadBtnOnAction() {
        downloading = true;
        FetchUtil.startDownloadWithResults(dm, new ArrayList<>(data), Objects.equals(outputDirField.getText(), "") ? new File("").getAbsolutePath() : outputDirField.getText(), new Logger("GUI", System.out), false, true, -1, () -> Platform.runLater(this::updateStatus));
        downloading = false;
    }

    private void updateStatus() {
        dm.update();

        downloading = !dm.isDone();

        progressBar.setProgress(dm.getPercentComplete());
        if (downloading) {
            String sb = "创建:" + dm.getCreated() + " 下载中:" + dm.getStarted() + " 已完成:" + dm.getCompleted() + " 失败:" + dm.getFailed() + " " + dm.getSpeed();
            Platform.runLater(() -> statusLabel.setText(sb));
        } else {
            Platform.runLater(() -> statusLabel.setText("完成！"));
        }

        dataTable.update();
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void show() {
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
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

        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        initSourceCombo();

        initTable();

        fetchBtn.disableProperty().bind(sourcesComboBox.selectedIndexProperty().isEqualTo(-1));
        data.addListener((ListChangeListener<DownloadResult>) c -> downloadBtn.setDisable(data.size() == 0));
    }

    private void initTable() {
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

        dataTable.getFilters().addAll(List.of(new StringFilter<>("文件名", arg0 -> arg0.getResult().getFileName()), new StringFilter<>("下载链接", arg0 -> arg0.getResult().getUrl()), new StringFilter<>("状态", DownloadResult::getStatusString)));

        dataTable.setItems(data);

        data.clear();
    }

    private void initSourceCombo() {
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
    }

    @javafx.fxml.FXML
    public void sourceUpdateBtnOnAction() {
        updateSource();
    }

    @javafx.fxml.FXML
    public void fetchBtnOnAction() {
        loadingPane.setVisible(true);
        operationLabel.setText("抓取中");
        ft = new FadeTransition();
        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));
        ft.play();
        new Thread(() -> {
            LinkedList<DownloadResult> r = new LinkedList<>();
            Source s = null;
            try {
                s = (Source) sourcesComboBox.getSelectedItem().clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            LinkedList<Argument<?>> args = new LinkedList<>();

            for (ArgumentPane<?> argument : arguments) {
                args.add(argument.getValue());
            }

            FetchUtil.replaceArgument(s, args);
            ArrayList<Result> r1 = FetchUtil.fetch(s, (int) timesSlider.getValue(), new Logger("GUI", System.out), true, null, 0);
            LinkedList<DownloadResult> drs = new LinkedList<>();
            for (Result result : r1) {
                DownloadResult dr = new DownloadResult();
                dr.setResult(result);
                r.add(dr);
                drs.add(dr);
            }
            dm = new DownloadManager(drs.toArray(new DownloadResult[]{}));

            Platform.runLater(() -> {
                ft.setFromValue(1);
                ft.setToValue(0);
                data.addAll(r);
                ft.setOnFinished((e) -> loadingPane.setVisible(false));
                ft.play();
            });
        }).start();
    }

    @javafx.fxml.FXML
    public void delCompletedBtnOnAction() {
        data.removeIf(datum -> datum.getStatus() == DownloadStatus.COMPLETED);
    }

    @javafx.fxml.FXML
    public void backBtnOnAction() {
        TranslateTransition tt = new TranslateTransition();
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
        gui.welcomeSceneController.playAnimation();
    }

    private void updateSource() {
        sourcesComboBox.getItems().clear();

        loadingPane.setVisible(true);
        ft.play();

        new Thread(() -> {
            try {
                SourceManager.readConfig();
                sources.clear();
                sources.addAll(SourceManager.getSources());
            } catch (IOException e) {
                //TODO Handle exception
                throw new RuntimeException(e);
            }
            ft.play();
            ft.setOnFinished((e) -> loadingPane.setVisible(false));
        }).start();

        ft.setFromValue(1);
        ft.setToValue(0);
    }

    @javafx.fxml.FXML
    public void onSourceSelected() {
        try {
            argumentsPane.getChildren().clear();
            arguments.clear();
            for (Argument<?> argument : sourcesComboBox.getSelectedItem().getArguments()) {
                if (argument instanceof LimitedStringArgument arg) {
                    LimitedStringArgumentPane pane = LimitedStringArgumentPane.getInstance(arg);
                    argumentsPane.getChildren().add(pane.getPane());
                    arguments.add(pane);
                } else if (argument instanceof StringArgument arg) {
                    StringArgumentPane pane = StringArgumentPane.getInstance(arg);
                    argumentsPane.getChildren().add(pane.getPane());
                    arguments.add(pane);
                } else if (argument instanceof LimitedIntegerArgument arg) {
                    LimitedIntegerArgumentPane pane = LimitedIntegerArgumentPane.getInstance(arg);
                    argumentsPane.getChildren().add(pane.getPane());
                    arguments.add(pane);
                }
            }
        } catch (IOException e) {
            //TODO Handle exceptions
            throw new RuntimeException(e);
        }
    }
}
