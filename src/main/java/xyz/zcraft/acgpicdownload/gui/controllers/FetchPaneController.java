package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.MyPane;
import xyz.zcraft.acgpicdownload.gui.base.argpanes.ArgumentPane;
import xyz.zcraft.acgpicdownload.gui.base.argpanes.LimitedIntegerArgumentPane;
import xyz.zcraft.acgpicdownload.gui.base.argpanes.LimitedStringArgumentPane;
import xyz.zcraft.acgpicdownload.gui.base.argpanes.StringArgumentPane;
import xyz.zcraft.acgpicdownload.util.ExceptionHandler;
import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.dl.DownloadManager;
import xyz.zcraft.acgpicdownload.util.dl.DownloadResult;
import xyz.zcraft.acgpicdownload.util.dl.DownloadStatus;
import xyz.zcraft.acgpicdownload.util.fetch.FetchUtil;
import xyz.zcraft.acgpicdownload.util.fetch.Result;
import xyz.zcraft.acgpicdownload.util.source.Source;
import xyz.zcraft.acgpicdownload.util.source.SourceManager;
import xyz.zcraft.acgpicdownload.util.source.argument.Argument;
import xyz.zcraft.acgpicdownload.util.source.argument.LimitedIntegerArgument;
import xyz.zcraft.acgpicdownload.util.source.argument.LimitedStringArgument;
import xyz.zcraft.acgpicdownload.util.source.argument.StringArgument;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class FetchPaneController extends MyPane {
    private final LinkedList<ArgumentPane<?>> arguments = new LinkedList<>();
    TransformableList<Source> sourceTransformableList;
    FadeTransition ft = new FadeTransition();
    @javafx.fxml.FXML
    private MFXFilterComboBox<Source> sourcesComboBox;
    private ObservableList<Source> sources;
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
    private MFXToggleButton fullResultToggle;
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
    @javafx.fxml.FXML
    private HBox argumentsPane;
    @javafx.fxml.FXML
    private MFXButton selectDirBtn;
    @javafx.fxml.FXML
    private MFXSlider threadCountSlider;
    @javafx.fxml.FXML
    private MFXButton updateFromGithubBtn;

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FetchPaneController.class);
    @javafx.fxml.FXML
    public void downloadBtnOnAction() {
        logger.info("Starting download");
        downloading = true;
        FetchUtil.startDownloadWithResults(dm, new ArrayList<>(data),
                Objects.equals(outputDirField.getText(), "") ? new File("").getAbsolutePath()
                        : outputDirField.getText(),
                new Logger("GUI", System.out, Main.log), fullResultToggle.isSelected(), true,
                (int) threadCountSlider.getValue(), () -> Platform.runLater(this::updateStatus), true);
        downloading = false;
    }

    private void updateStatus() {
        dm.update();

        downloading = !dm.isDone();

        progressBar.setProgress(dm.getPercentComplete());
        if (downloading) {
            String sb = " " + ResourceBundleUtil.getString("gui.download.status.created") + dm.getCreated() + " "
                    + ResourceBundleUtil.getString("gui.download.status.started") + dm.getStarted() + " "
                    + ResourceBundleUtil.getString("gui.download.status.completed") + dm.getCompleted() + " "
                    + ResourceBundleUtil.getString("gui.download.status.failed") + dm.getFailed() + " " + dm.getSpeed();
            Platform.runLater(() -> statusLabel.setText(sb));
        } else {
            Platform.runLater(() -> statusLabel.setText(ResourceBundleUtil.getString("gui.download.status.completed")));
        }

        dataTable.update();
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

        initSourceCombo();

        initTable();

        fetchBtn.disableProperty().bind(sourcesComboBox.selectedIndexProperty().isEqualTo(-1));
        data.addListener((ListChangeListener<DownloadResult>) _ -> downloadBtn.setDisable(data.isEmpty()));

        try {
            ConfigManager.readConfig();
            restoreConfig();
        } catch (IOException e) {
            Main.logError(e);
        }

        sourceUpdateBtn.setGraphic(new MFXFontIcon("fas-arrows-rotate"));
        backBtn.setGraphic(new MFXFontIcon("fas-angle-down"));
        delCompletedBtn.setGraphic(new MFXFontIcon("fas-trash"));
        selectDirBtn.setGraphic(new MFXFontIcon("fas-folder"));
    }

    private void initTable() {
        data = FXCollections.observableArrayList(new DownloadResult());

        MFXTableColumn<DownloadResult> titleColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.fetch.table.column.fileName"), true);
        MFXTableColumn<DownloadResult> linkColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.fetch.table.column.link"), true);
        MFXTableColumn<DownloadResult> statusColumn = new MFXTableColumn<>(
                ResourceBundleUtil.getString("gui.fetch.table.column.status"), true);

        titleColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(arg01 -> arg01.getResult().getFileName()));
        linkColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(arg01 -> arg01.getResult().getUrl()));
        statusColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(DownloadResult::getStatusString));

        titleColumn.setAlignment(Pos.CENTER);
        linkColumn.setAlignment(Pos.CENTER);
        statusColumn.setAlignment(Pos.CENTER);

        titleColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.5));
        linkColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.4));
        statusColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));

        dataTable.getTableColumns().addAll(List.of(titleColumn, linkColumn, statusColumn));

        dataTable.getFilters()
                .addAll(List.of(
                        new StringFilter<>(ResourceBundleUtil.getString("gui.fetch.table.column.fileName"),
                                arg0 -> arg0.getResult().getFileName()),
                        new StringFilter<>(ResourceBundleUtil.getString("gui.fetch.table.column.link"),
                                arg0 -> arg0.getResult().getUrl()),
                        new StringFilter<>(ResourceBundleUtil.getString("gui.fetch.table.column.status"),
                                DownloadResult::getStatusString)));

        dataTable.setItems(data);

        dataTable.getSelectionModel().setAllowsMultipleSelection(false);
        dataTable.getSelectionModel().selectionProperty()
                .addListener((_, _, _) -> {
                    List<DownloadResult> selectedValues = dataTable.getSelectionModel().getSelectedValues();
                    if (!selectedValues.isEmpty()) {
                        if (selectedValues.getFirst().getStatus() == DownloadStatus.COMPLETED &&
                                ConfigManager.getConfig().getBooleanValue("fetchPLCOCopy")) {
                            try {
                                Desktop.getDesktop().open(new File(outputDirField.getText(),
                                        selectedValues.getFirst().getResult().getFileName()));
                            } catch (IOException e) {
                                Notice.showError(ResourceBundleUtil.getString("gui.fetch.table.cantOpen"),
                                        gui.mainPane);
                            }
                        } else {
                            Toolkit.getDefaultToolkit().getSystemClipboard()
                                    .setContents(new StringSelection(selectedValues.getFirst().getResult().getUrl()), null);
                            dataTable.getSelectionModel().clearSelection();
                            Notice.showSuccess(ResourceBundleUtil.getString("gui.fetch.table.copy"), gui.mainPane);
                        }
                    }
                });

        dataTable.features().enableBounceEffect();
        dataTable.features().enableSmoothScrolling(0.7);
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

        try {
            SourceManager.readConfig();
            sources.addAll(SourceManager.getSources());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void sourceUpdateBtnOnAction() {
        updateSource();
    }

    public void updateFromGithub() {
        ft.setFromValue(0);
        ft.setToValue(1);
        operationLabel.setText(ResourceBundleUtil.getString("gui.fetch.updateFromGithub"));
        loadingPane.setVisible(true);
        ft.play();
        new Thread(() -> {
            logger.info("Updating from Github");
            try {
                SourceManager.updateFromGithub();
                logger.info("Update successfully");
            } catch (IOException e) {
                logger.error("Failed to update from Github", e);
                Main.logError(e);
                gui.showError(e);
            } finally {
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.play();
                ft.setOnFinished((_) -> loadingPane.setVisible(false));
            }
        }).start();
    }

    @javafx.fxml.FXML
    public void fetchBtnOnAction() {
        loadingPane.setVisible(true);
        operationLabel.setText(ResourceBundleUtil.getString("gui.fetch.loading.fetch"));
        ft = new FadeTransition();
        ft.setNode(loadingPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));
        ft.play();
        new Thread(() -> {
            long start = System.currentTimeMillis();
            logger.info("Starting fetching");

            LinkedList<DownloadResult> r = new LinkedList<>();
            Source s;
            try {
                s = (Source) sourcesComboBox.getSelectedItem().clone();
            } catch (CloneNotSupportedException ignored) {
                return;
            }

            LinkedList<Argument<?>> args = new LinkedList<>();

            for (ArgumentPane<?> argument : arguments) {
                args.add(argument.getArgument());
            }

            FetchUtil.replaceArgument(s, args);

            ArrayList<Result> r1 = FetchUtil.fetch(s, (int) timesSlider.getValue(),
                    new Logger("GUI", System.out, Main.log),
                    true,
                    ConfigManager.getConfig().getString("proxyHost"),
                    Objects.requireNonNullElse(ConfigManager.getConfig().getInteger("proxyPort"), 0),
                    new ExceptionHandler() {
                        @Override
                        public void handle(Exception e) {
                            gui.showError(e);
                            Main.logError(e);
                        }
                    });
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
                ft.setOnFinished((_) -> loadingPane.setVisible(false));
                ft.play();
            });

            logger.info("Finished fetching in" + (System.currentTimeMillis() - start) + "ms");
        }).start();
    }

    @javafx.fxml.FXML
    public void delCompletedBtnOnAction() {
        int a = data.size();
        data.removeIf(datum -> datum.getStatus() == DownloadStatus.COMPLETED);
        Notice.showSuccess(
                String.format(Objects.requireNonNull(ResourceBundleUtil.getString("gui.fetch.notice.removeCompleted")),
                        a - data.size()),
                gui.mainPane);
    }

    @javafx.fxml.FXML
    public void backBtnOnAction() {
        super.hide();
        gui.menuPaneController.showMain();
    }

    private void updateSource() {
        sourcesComboBox.getItems().clear();
        ft.setFromValue(0);
        ft.setToValue(1);
        operationLabel.setText(ResourceBundleUtil.getString("gui.fetch.loading.readSource"));
        loadingPane.setVisible(true);
        ft.play();

        new Thread(() -> {
            try {
                SourceManager.readConfig();
                sources.clear();
                sources.addAll(SourceManager.getSources());
            } catch (Exception e) {
                gui.showError(e);
                Main.logError(e);
            }
            ft.play();
            ft.setOnFinished((_) -> loadingPane.setVisible(false));
        }).start();

        ft.setFromValue(1);
        ft.setToValue(0);
    }

    @javafx.fxml.FXML
    public void onSourceSelected() {
        try {
            argumentsPane.getChildren().clear();
            arguments.clear();
            if (sourcesComboBox.getSelectedItem() == null) {
                return;
            }
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
            restoreSourceConfig();
        } catch (IOException e) {
            gui.showError(e);
            Main.logError(e);
        }
    }

    @javafx.fxml.FXML
    public void updateFromGithubBtnOnAction() {
        updateFromGithub();
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

    public void saveConfig() {
        JSONObject obj = ConfigManager.getConfig();
        obj = obj.getJSONObject("fetch");
        if (obj == null) obj = new JSONObject();
        obj.put("times", (int) timesSlider.getValue());
        obj.put("output", outputDirField.getText());
        obj.put("threadCount", (int) threadCountSlider.getValue());
        obj.put("full", fullResultToggle.isSelected());
        if (sourcesComboBox.getSelectedItem() != null) {
            obj.put("source", sourcesComboBox.getSelectedItem().getName());
        }

        JSONObject sourceObj = Objects.requireNonNullElse(obj.getJSONObject("sources"), new JSONObject());
        if (sourcesComboBox.getSelectedItem() != null) {
            JSONObject sourceCfg = new JSONObject();
            for (ArgumentPane<?> argPane : arguments) {
                sourceCfg.put(argPane.getName(), argPane.getArgument().getValue());
            }
            sourceObj.put(sourcesComboBox.getSelectedItem().getName(), sourceCfg);
        }
        obj.put("sources", sourceObj);
        ConfigManager.getConfig().put("fetch", obj);

        try {
            ConfigManager.saveConfig();
            Notice.showSuccess(ResourceBundleUtil.getString("gui.fetch.notice.saved"), gui.mainPane);
        } catch (IOException e) {
            gui.showError(e);
            Main.logError(e);
        }
    }

    public void restoreConfig() {
        JSONObject json = ConfigManager.getConfig();
        if (json != null) {
            json = json.getJSONObject("fetch");
            if (json != null) {
                timesSlider.setValue(Objects.requireNonNullElse(json.getInteger("times"), 1));
                outputDirField.setText(Objects.requireNonNullElse(json.getString("output"), ""));
                threadCountSlider.setValue(Objects.requireNonNullElse(json.getInteger("threadCount"), 10));
                fullResultToggle.setSelected(json.getBooleanValue("full"));

                String name = json.getString("source");
                if (name != null) {
                    ObservableList<Source> items = sourcesComboBox.getItems();
                    for (Source i : items) {
                        if (i.getName().equals(name)) {
                            sourcesComboBox.getSelectionModel().selectItem(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void restoreSourceConfig() {
        JSONObject json = ConfigManager.getConfig().getJSONObject("fetch");
        if (json == null) return;
        json = json.getJSONObject("sources");
        if (json == null) return;
        String name = sourcesComboBox.getSelectedItem().getName();
        json = json.getJSONObject(name);
        if (json == null)
            return;
        for (ArgumentPane<?> argumentPane : arguments) {
            Object t = json.get(argumentPane.getArgument().getName());
            if (t == null)
                continue;
            Argument<?> argument = argumentPane.getArgument();
            if (argument instanceof LimitedStringArgument arg) {
                if (t instanceof String v) {
                    arg.set(v);
                }
            } else if (argument instanceof StringArgument arg) {
                if (t instanceof String v) {
                    arg.set(v);
                }
            } else if (argument instanceof LimitedIntegerArgument arg) {
                if (t instanceof Integer v) {
                    arg.set(v);
                }
            }
            argumentPane.update();
        }
    }
}
