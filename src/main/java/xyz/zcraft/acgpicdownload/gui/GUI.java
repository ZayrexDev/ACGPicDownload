package xyz.zcraft.acgpicdownload.gui;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import xyz.zcraft.acgpicdownload.gui.controllers.*;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

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
    public Pane settingsPane;
    public Pane fetchPane;
    public Pane welcomePane;
    public Pane pixivMenuPane;
    public Pane pixivDownloadPane;
    public Pane pixivDiscoveryPane;
    public Pane pixivUserPane;
    public Pane pixivRelatedPane;

    public GUI gui;

    public static void start(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ConfigManager.readConfig();
        gui = this;
        mainStage = stage;

        stage.setTitle("ACGPicDownload");

        FXMLLoader mainLoader = new FXMLLoader(ResourceLoader.loadURL("fxml/MainPane.fxml"), ResourceBundleUtil.getResource());
        mainPane = mainLoader.load();
        mainPaneController = mainLoader.getController();
        mainPaneController.setGui(gui);

        mainPaneController.setBackground(ResourceLoader.loadStream("bg.png"));

        Scene s = new Scene(mainPane);

        stage.setScene(s);

        BufferedImage read = ImageIO.read(ResourceLoader.loadStream("bg.png"));
        double rate = (double) read.getWidth() / (double) read.getHeight();

        stage.setWidth(800);
        stage.setHeight(stage.getWidth() / rate + 29);
        stage.setResizable(false);

        stage.show();

        Thread initThread = new Thread(() -> {
            try {
                FXMLLoader loader;
                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/WelcomePane.fxml"), ResourceBundleUtil.getResource());
                welcomePane = loader.load();
                welcomePaneController = loader.getController();
                welcomePaneController.setGui(gui);
                fill(welcomePane);
                Platform.runLater(() -> mainPane.getChildren().add(welcomePane));
                mainPaneController.setProgress(0.1);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/FetchPane.fxml"), ResourceBundleUtil.getResource());
                fetchPane = loader.load();
                fetchPaneController = loader.getController();
                fetchPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(fetchPane));
                mainPaneController.setProgress(0.2);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivMenuPane.fxml"), ResourceBundleUtil.getResource());
                pixivMenuPane = loader.load();
                pixivMenuPaneController = loader.getController();
                pixivMenuPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(pixivMenuPane));
                mainPaneController.setProgress(0.3);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivUserPane.fxml"), ResourceBundleUtil.getResource());
                pixivUserPane = loader.load();
                pixivUserPaneController = loader.getController();
                pixivUserPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(pixivUserPane));
                mainPaneController.setProgress(0.4);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivRelatedPane.fxml"), ResourceBundleUtil.getResource());
                pixivRelatedPane = loader.load();
                pixivRelatedPaneController = loader.getController();
                pixivRelatedPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(pixivRelatedPane));
                mainPaneController.setProgress(0.5);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivDownloadPane.fxml"),
                        ResourceBundleUtil.getResource());
                pixivDownloadPane = loader.load();
                pixivDownloadPaneController = loader.getController();
                pixivDownloadPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(pixivDownloadPane));
                mainPaneController.setProgress(0.6);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivDiscoveryPane.fxml"),
                        ResourceBundleUtil.getResource());
                pixivDiscoveryPane = loader.load();
                pixivDiscoveryPaneController = loader.getController();
                pixivDiscoveryPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(pixivDiscoveryPane));
                mainPaneController.setProgress(0.7);

                loader = new FXMLLoader(ResourceLoader.loadURL("fxml/SettingsPane.fxml"), ResourceBundleUtil.getResource());
                settingsPane = loader.load();
                settingsPaneController = loader.getController();
                settingsPaneController.setGui(gui);
                Platform.runLater(() -> mainPane.getChildren().add(settingsPane));
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
                    MFXGenericDialog content = MFXGenericDialogBuilder.build()
                            .setContentText(ResourceBundleUtil.getString("gui.seriousERR") + "\n" + e.getMessage())
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
                });
            }
        });

        initThread.setPriority(1);
        initThread.start();
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

    public void showError(Exception e){
        ErrorPaneController epc = ErrorPaneController.getInstance(mainPane);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        Platform.runLater(() -> {
            gui.fill(epc.getErrorPane());
            gui.mainPane.getChildren().addAll(epc.getErrorPane());
            epc.setErrorMessage(sw.toString());
            epc.setBlur(mainPane.snapshot(new SnapshotParameters(), null));
            epc.show();
        });
    }

    public SettingsPaneController getSettingsPaneController() {
        return settingsPaneController;
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
}
