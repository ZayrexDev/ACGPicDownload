package xyz.zcraft.acgpicdownload.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.zcraft.acgpicdownload.gui.scenes.FetchSceneController;
import xyz.zcraft.acgpicdownload.gui.scenes.MainPaneController;
import xyz.zcraft.acgpicdownload.gui.scenes.WelcomeSceneController;

public class GUI extends Application {
    public FetchSceneController fetchSceneController;
    public WelcomeSceneController welcomeSceneController;
    public MainPaneController mainPaneController;

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

        FXMLLoader loader = new FXMLLoader(ResourceLoader.loadURL("fxml/welcomePane.fxml"));
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

        stage.setResizable(false);
        stage.setScene(s);

        stage.show();
        welcomeSceneController.playAnimation();
    }

    public void openFetchPane() {
        fetchSceneController.show();
    }
}
