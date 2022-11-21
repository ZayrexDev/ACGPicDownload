package xyz.zcraft.acgpicdownload.gui.scenes;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class ErrorPaneController {
    @javafx.fxml.FXML
    private AnchorPane errorPane;
    @javafx.fxml.FXML
    private TextArea errorArea;
    @javafx.fxml.FXML
    private MFXButton errorOkBtn;

    public static ErrorPaneController getInstance() {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WelcomeSceneController.class.getResource("WelcomePane.fxml")));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loader.getController();
    }

    public AnchorPane getErrorPane() {
        return errorPane;
    }

    public void hide() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(errorPane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setAutoReverse(false);
        ft.setRate(0.001);
        ft.setDuration(Duration.millis(5));
        ft.setOnFinished(actionEvent -> Platform.runLater(() -> errorPane.setVisible(false)));

        ft.play();
    }

    public void show() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(errorPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.001);
        ft.setDuration(Duration.millis(5));

        errorPane.setVisible(true);
        ft.play();
    }

    public void setErrorMessage(String message) {
        errorArea.setText(message);
    }

    public void errorOkBtnOnAction(ActionEvent actionEvent) {
        hide();
    }
}
