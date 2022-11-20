package xyz.zcraft.acgpicdownload.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import xyz.zcraft.acgpicdownload.gui.scenes.WelcomeSceneController;

import java.util.Objects;

public class GUI extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WelcomeSceneController.class.getResource("WelcomeScene.fxml")));

        AnchorPane pane = loader.load();
        Scene s = new Scene(pane);
        stage.setScene(s);

        WelcomeSceneController controller = loader.getController();

        stage.show();
        controller.playAnimation();
    }
}
