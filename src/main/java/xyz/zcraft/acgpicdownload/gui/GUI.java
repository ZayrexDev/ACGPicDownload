package xyz.zcraft.acgpicdownload.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.zcraft.acgpicdownload.gui.scenes.FetchSceneController;
import xyz.zcraft.acgpicdownload.gui.scenes.MainPaneController;
import xyz.zcraft.acgpicdownload.gui.scenes.WelcomeSceneController;

public class GUI extends Application {
    public FetchSceneController fetchSceneController;
    public WelcomeSceneController welcomeSceneController;
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

        FXMLLoader loader = new FXMLLoader(ResourceLoader.loadURL("fxml/WelcomePane.fxml"));
        welcomePane = loader.load();
        welcomeSceneController = loader.getController();
        welcomeSceneController.setGui(gui);

        loader = new FXMLLoader(ResourceLoader.loadURL("fxml/FetchPane.fxml"));
        fetchPane = loader.load();
        fetchSceneController = loader.getController();
        fetchSceneController.setGui(gui);

        loader = new FXMLLoader(ResourceLoader.loadURL("fxml/MainPane.fxml"));
        mainPane = loader.load();
        mainPaneController = loader.getController();
        mainPaneController.setGui(gui);

        mainPaneController.setBackground(ResourceLoader.loadStream("bg.png"));

        mainPane.getChildren().addAll(welcomePane, fetchPane);
        Scene s = new Scene(mainPane);

        stage.setResizable(true);
        stage.setScene(s);

        stage.widthProperty().addListener(observable -> {
            stage.setHeight(stage.getWidth() / 1.66667);
            mainPaneController.getBackground().setFitWidth(stage.getWidth());
            mainPaneController.getBackground().setFitHeight(stage.getHeight());
        });
        stage.heightProperty().addListener(observable -> {
            stage.setWidth(stage.getHeight() * 1.66667);
            mainPaneController.getBackground().setFitWidth(stage.getWidth());
            mainPaneController.getBackground().setFitHeight(stage.getHeight());
        });

        fill(mainPane, welcomePane);

        stage.show();
        welcomeSceneController.playAnimation();
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
        fetchSceneController.show();
    }
}
