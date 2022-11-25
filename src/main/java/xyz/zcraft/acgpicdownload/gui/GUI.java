package xyz.zcraft.acgpicdownload.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.zcraft.acgpicdownload.gui.scenes.ErrorPaneController;
import xyz.zcraft.acgpicdownload.gui.scenes.FetchPaneController;
import xyz.zcraft.acgpicdownload.gui.scenes.MainPaneController;
import xyz.zcraft.acgpicdownload.gui.scenes.WelcomePaneController;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GUI extends Application {
    public FetchPaneController fetchPaneController;
    public WelcomePaneController welcomePaneController;
    public MainPaneController mainPaneController;
    public Stage mainStage;
    public Pane mainPane;
    public Pane fetchPane;
    public Pane welcomePane;

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
//        stage.widthProperty().addListener(observable -> stage.setHeight(stage.getWidth() / rate));
//        stage.heightProperty().addListener(observable -> stage.setWidth(stage.getHeight() * rate));

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

            mainPaneController.setProgress(0.3);

            loader = new FXMLLoader(ResourceLoader.loadURL("fxml/FetchPane.fxml"), ResourceBundleUtil.getResource());
            try {
                fetchPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fetchPaneController = loader.getController();
            fetchPaneController.setGui(gui);

            mainPaneController.setProgress(0.6);

            fill(welcomePane);

            welcomePane.setVisible(false);

            mainPaneController.setProgress(0.9);

            Platform.runLater(() -> mainPane.getChildren().addAll(welcomePane, fetchPane));

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

    public void showError(Exception e){
        ErrorPaneController epc = ErrorPaneController.getInstance();
        Platform.runLater(() -> {
            gui.fill(epc.getErrorPane());
            gui.mainPane.getChildren().addAll(epc.getErrorPane());
            epc.setErrorMessage(e.toString());
            epc.show();
        });
    }
}
