package xyz.zcraft.acgpicdownload.gui;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.zcraft.acgpicdownload.gui.controllers.*;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class GUI extends Application {
    public FetchPaneController fetchPaneController;
    public WelcomePaneController welcomePaneController;
    public MainPaneController mainPaneController;
    public PixivMenuPaneController pixivMenuPaneController;
    public SettingsPaneController settingsPaneController;
    public PixivDownloadPaneController pixivDownloadPaneController;
    public PixivDiscoveryPaneController pixivDiscoveryPaneController;
    public PixivUserPaneController pixivUserPaneController;
    public PixivRelatedPaneController pixivRelatedPaneController;

    public Stage mainStage;
    public Pane mainPane;
    public Pane stagePane;
    public Pane welcomePane;

    public GUI gui;

    public static void start(String[] args) {
        launch(args);
    }

    public static String conductException(Exception e) {
        if (e instanceof org.jsoup.HttpStatusException ex) {
            switch (ex.getStatusCode()) {
                case 400, 401 -> {
                    return ResourceBundleUtil.getString("err.status.401");
                }
                case 404 -> {
                    return ResourceBundleUtil.getString("err.status.404");
                }
                case 427 -> {
                    return ResourceBundleUtil.getString("err.status.427");
                }
            }
        } else if (e instanceof java.net.SocketTimeoutException) {
            return ResourceBundleUtil.getString("err.status.timeout");
        } else if ((e instanceof java.net.ConnectException ex && ex.getMessage().contains("Connection refused"))
                || (e instanceof java.net.SocketException ex1 && ex1.getMessage().contains("Network is unreachable: no further information"))
                || (e instanceof java.net.UnknownHostException)
        ) {
            String h = ConfigManager.getConfig().getString("proxyHost");
            Integer p = ConfigManager.getConfig().getInteger("proxyPort");
            String s = "";
            if (h != null && p != null) {
                s = h + ":" + p;
            }
            return String.format(Objects.requireNonNull(ResourceBundleUtil.getString("err.status.invalidProxy")), s);
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            return ResourceBundleUtil.getString("err.status.ssl");
        }

        return null;
    }

    public static void initTable(ObservableList<PixivArtwork> data, MFXTableView<PixivArtwork> dataTable) {
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
        fromColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getFrom));
        tagColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTagsString));
        idColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getId));
        typeColumn.setRowCellFactory(e -> new MFXTableRowCell<>(PixivArtwork::getTypeString));

        titleColumn.setAlignment(Pos.CENTER);
        authorColumn.setAlignment(Pos.CENTER);
        fromColumn.setAlignment(Pos.CENTER);
        tagColumn.setAlignment(Pos.CENTER);
        idColumn.setAlignment(Pos.CENTER);
        typeColumn.setAlignment(Pos.CENTER);

        // titleColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.4).get());
        // authorColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.2).get());
        // fromColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());
        // tagColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.2).get());
        // idColumn.prefWidthProperty().set(dataTable.widthProperty().multiply(0.1).get());

        dataTable.getTableColumns().addAll(List.of(titleColumn, authorColumn, fromColumn, tagColumn, idColumn,
                typeColumn));

        dataTable.getFilters().addAll(List.of(
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.title"), PixivArtwork::getTitle),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.author"), PixivArtwork::getUserName),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.from"), o -> o.getFrom().toString()),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.tag"), PixivArtwork::getTagsString),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.menu.column.id"), PixivArtwork::getId),
                new StringFilter<>(ResourceBundleUtil.getString("gui.pixiv.download.column.type"), PixivArtwork::getTypeString)
        ));

        dataTable.setItems(data);
        dataTable.getSelectionModel().setAllowsMultipleSelection(true);
        data.clear();
    }

    public void openPixivMenuPane() {
        pixivMenuPaneController.show();
    }

    public void fill(Node node) {
        AnchorPane.setTopAnchor(node, 0d);
        AnchorPane.setBottomAnchor(node, 0d);
        AnchorPane.setLeftAnchor(node, 0d);
        AnchorPane.setRightAnchor(node, 0d);
    }

    public void fill(Node... node) {
        for (Node node1 : node) {
            fill(node1);
        }
    }

    public void openFetchPane() {
        fetchPaneController.show();
    }

    public void openPixivDiscPane() {
        pixivDiscoveryPaneController.show();
    }

    public void openPixivDownloadPane() {
        pixivDownloadPaneController.show();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ConfigManager.readConfig();
        if (ConfigManager.getConfig().containsKey("lang"))
            ResourceBundleUtil.load(ConfigManager.getConfig().getString("lang"));
        gui = this;
        mainStage = stage;

        stage.setTitle("ACGPicDownload");

        FXMLLoader mainLoader = new FXMLLoader(ResourceLoader.loadURL("fxml/MainPane.fxml"),
                ResourceBundleUtil.getResource());
        stagePane = mainLoader.load();
        mainPaneController = mainLoader.getController();
        mainPane = mainPaneController.getMainPane();
        mainPaneController.setGui(gui);

        readBackground();

        mainPaneController.fitBackground();

        stage.setOnCloseRequest(windowEvent -> {
            stage.hide();
            System.exit(0);
        });

        stage.show();

        Thread initThread = new Thread(() -> {
            try {
                FXMLLoader loader;
                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/WelcomePane.fxml"),
                        ResourceBundleUtil.getResource());
                welcomePane = loader.load();
                welcomePaneController = loader.getController();
                welcomePaneController.setGui(gui);
                fill(welcomePane);
                Platform.runLater(() -> mainPane.getChildren().add(welcomePane));
                mainPaneController.setProgress(0.1);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/FetchPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                fetchPaneController = loader.getController();
                fetchPaneController.setGui(gui);
                fill(fetchPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(fetchPaneController.getMainPane()));
                mainPaneController.setProgress(0.2);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivMenuPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                pixivMenuPaneController = loader.getController();
                pixivMenuPaneController.setGui(gui);
                fill(pixivMenuPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(pixivMenuPaneController.getMainPane()));
                mainPaneController.setProgress(0.3);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivUserPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                pixivUserPaneController = loader.getController();
                pixivUserPaneController.setGui(gui);
                fill(pixivUserPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(pixivUserPaneController.getMainPane()));
                mainPaneController.setProgress(0.4);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivRelatedPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                pixivRelatedPaneController = loader.getController();
                pixivRelatedPaneController.setGui(gui);
                fill(pixivUserPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(pixivRelatedPaneController.getMainPane()));
                mainPaneController.setProgress(0.5);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivDownloadPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                pixivDownloadPaneController = loader.getController();
                pixivDownloadPaneController.setGui(gui);
                fill(pixivDownloadPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(pixivDownloadPaneController.getMainPane()));
                mainPaneController.setProgress(0.6);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivDiscoveryPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                pixivDiscoveryPaneController = loader.getController();
                pixivDiscoveryPaneController.setGui(gui);
                fill(pixivDiscoveryPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(pixivDiscoveryPaneController.getMainPane()));
                mainPaneController.setProgress(0.7);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/SettingsPane.fxml"),
                        ResourceBundleUtil.getResource());
                loader.load();
                settingsPaneController = loader.getController();
                settingsPaneController.setGui(gui);
                fill(settingsPaneController.getMainPane());
                Platform.runLater(() -> mainPane.getChildren().add(settingsPaneController.getMainPane()));
                mainPaneController.setProgress(0.8);

                // Load done
                welcomePane.setVisible(false);
                mainPaneController.setProgress(1);

                Thread.sleep(1000);

                Platform.runLater(() -> {
                    mainPaneController.initDone();
                    welcomePane.setVisible(true);
                    welcomePaneController.showMain();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    MFXGenericDialog content = MFXGenericDialogBuilder.build()
                            .setContentText(ResourceBundleUtil.getString("gui.seriousERR") + "\n" + sw)
                            .setShowClose(true)
                            .setHeaderText(ResourceBundleUtil.getString("cli.fetch.err")).get();
                    content.addActions(Map.entry(new MFXButton("OK"), event -> System.exit(1)));
                    MFXGenericDialogBuilder.build(content)
                            .toStageDialogBuilder()
                            .initOwner(stage)
                            .initModality(Modality.APPLICATION_MODAL)
                            .setDraggable(true)
                            .setTitle("Dialogs Preview")
                            .setScrimPriority(ScrimPriority.WINDOW)
                            .setScrimOwner(true)
                            .get()
                            .showDialog();

                    pw.close();
                    try {
                        sw.close();
                    } catch (IOException ignored) {
                    }
                });
            }
        });

        initThread.setPriority(1);
        initThread.start();
    }

    private void readBackground() throws IOException {
        Scene s = new Scene(stagePane);
        String bg = ConfigManager.getConfig().getString("bg");
        InputStream imgMain = null;
        BufferedImage read = null;
        mainStage.setScene(s);
        if (bg != null && bg.equals("transparent")) {
            s.setFill(null);
            mainStage.initStyle(StageStyle.TRANSPARENT);
            mainPane.setStyle("-fx-background: rgba(255,255,255,0.5);");
            mainPaneController.getTitlePane().setStyle("-fx-background: rgba(255,255,255,0.5);");
            stagePane.setStyle("-fx-background: rgba(255,255,255,0.5);");
            mainStage.setWidth(800);
            mainStage.setHeight(500);
            mainStage.setResizable(true);
            mainPaneController.setTransparent();
        } else {
            mainStage.initStyle(StageStyle.UNDECORATED);
            if (bg != null && !bg.isEmpty()) {
                File bgFolder = new File(bg);
                if (!bgFolder.exists())
                    bgFolder.mkdirs();
                List<File> fl = new ArrayList<>(Stream.of(Objects.requireNonNull(bgFolder.listFiles()))
                        .filter((f) -> f.getName().endsWith(".png") || f.getName().endsWith(".jpg"))
                        .toList());
                while (read == null || imgMain == null) {
                    if (fl.size() > 0) {
                        File file = fl.get(new Random().nextInt(fl.size()));
                        imgMain = new FileInputStream(file);
                        read = ImageIO.read(new FileInputStream(file));
                        double rate = (double) read.getWidth() / (double) read.getHeight();
                        if (800 / rate > 500 || 800 / rate < 250) {
                            fl.remove(file);
                            imgMain.close();
                            imgMain = null;
                            read = null;
                        }
                    } else {
                        if (imgMain != null)
                            imgMain.close();
                        imgMain = ResourceLoader.loadStream("bg.png");
                        read = ImageIO.read(ResourceLoader.loadStream("bg.png"));
                    }
                }
            } else {
                imgMain = ResourceLoader.loadStream("bg.png");
                read = ImageIO.read(ResourceLoader.loadStream("bg.png"));
            }

            mainPaneController.setBackground(imgMain);
            double rate = (double) read.getWidth() / (double) read.getHeight();
            mainStage.setWidth(800);
            mainStage.setHeight(mainStage.getWidth() / rate + 29);
            mainStage.setResizable(false);
        }
    }

    public void openSettingsPane() {
        settingsPaneController.show();
    }

    public void openPixivUserPane() {
        pixivUserPaneController.show();
    }

    public void openPixivRelatedPane() {
        pixivRelatedPaneController.show();
    }

    public void showError(Exception e) {
        ErrorPaneController epc = ErrorPaneController.getInstance(mainPane);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            String msg = conductException(e);
            if (msg != null) {
                sb.append(ResourceBundleUtil.getString("err.conduct"));
                sb.append(msg);
                sb.append("\n");
            }
            sb.append(sw);
            gui.fill(epc.getErrorPane());
            gui.mainPane.getChildren().addAll(epc.getErrorPane());
            epc.setErrorMessage(sb.toString());
            epc.setBlur(mainPaneController.isTransparent() ? null : mainPane.snapshot(new SnapshotParameters(), null));
            epc.show();
            pw.close();
            try {
                sw.close();
            } catch (IOException ignored) {
            }
        });
    }
}
