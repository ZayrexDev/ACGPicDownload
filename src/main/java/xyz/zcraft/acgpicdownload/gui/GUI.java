package xyz.zcraft.acgpicdownload.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.zcraft.acgpicdownload.gui.controllers.*;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GUI extends Application {
    public FetchPaneController fetchPaneController;
    public WelcomePaneController welcomePaneController;
    public MainPaneController mainPaneController;
    public PixivMenuPaneController pixivMenuPaneController;
    public SettingsPaneController settingsPaneController;
    public PixivDownloadPaneController pixivDownloadPaneController;

    public Stage mainStage;
    public Pane mainPane;
    public Pane settingsPane;
    public Pane fetchPane;
    public Pane welcomePane;
    public Pane pixivMenuPane;
    public Pane pixivDownloadPane;

    public GUI gui;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        gui = this;
        mainStage = stage;

        stage.setTitle("ACGPicDownload");

        FXMLLoader mainLoader = new FXMLLoader(ResourceLoader.loadURL("fxml/MainPane.fxml"), ResourceBundleUtil.getResource());
        mainPane = mainLoader.load();
        mainPaneController = mainLoader.getController();
        mainPaneController.setGui(gui);

        mainPaneController.setBackground(ResourceLoader.loadStream("bg.png"));

        Scene s = new Scene(mainPane);

        stage.setResizable(true);
        stage.setScene(s);

        BufferedImage read = ImageIO.read(ResourceLoader.loadStream("bg.png"));
        double rate = (double) read.getWidth() / (double) read.getHeight();

        stage.setWidth(800);
        stage.setHeight(stage.getWidth() / rate + 29);
        stage.setResizable(false);

        stage.show();

        Thread initThread = new Thread(() -> {
            FXMLLoader loader;
            loader = new FXMLLoader(ResourceLoader.loadURL("fxml/WelcomePane.fxml"), ResourceBundleUtil.getResource());
            try {
                welcomePane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            welcomePaneController = loader.getController();
            welcomePaneController.setGui(gui);

            fill(welcomePane);
            Platform.runLater(() -> mainPane.getChildren().add(welcomePane));

            mainPaneController.setProgress(0.2);

            loader = new FXMLLoader(ResourceLoader.loadURL("fxml/FetchPane.fxml"), ResourceBundleUtil.getResource());
            try {
                fetchPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fetchPaneController = loader.getController();
            fetchPaneController.setGui(gui);

            Platform.runLater(() -> mainPane.getChildren().add(fetchPane));

            mainPaneController.setProgress(0.4);

            loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivMenuPane.fxml"), ResourceBundleUtil.getResource());
            try {
                pixivMenuPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pixivMenuPaneController = loader.getController();
            pixivMenuPaneController.setGui(gui);

            Platform.runLater(() -> mainPane.getChildren().add(pixivMenuPane));

            loader = new FXMLLoader(ResourceLoader.loadURL("fxml/PixivDownloadPane.fxml"),
                    ResourceBundleUtil.getResource());
            try {
                pixivDownloadPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pixivDownloadPaneController = loader.getController();
            pixivDownloadPaneController.setGui(gui);

            Platform.runLater(() -> mainPane.getChildren().add(pixivDownloadPane));

            mainPaneController.setProgress(0.8);

            loader = new FXMLLoader(ResourceLoader.loadURL("fxml/SettingsPane.fxml"), ResourceBundleUtil.getResource());
            try {
                settingsPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            settingsPaneController = loader.getController();
            settingsPaneController.setGui(gui);

            Platform.runLater(() -> mainPane.getChildren().add(settingsPane));

            mainPaneController.setProgress(0.9);

            welcomePane.setVisible(false);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mainPaneController.setProgress(1);
            Platform.runLater(() -> {
                mainPaneController.initDone();
                welcomePane.setVisible(true);
                welcomePaneController.playAnimation();
            });
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

    public void openPixivDownloadPane() {
        pixivDownloadPaneController.show();
    }

    public void showError(Exception e){
        ErrorPaneController epc = ErrorPaneController.getInstance();
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
}
