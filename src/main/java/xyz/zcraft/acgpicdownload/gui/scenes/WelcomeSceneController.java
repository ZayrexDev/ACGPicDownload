package xyz.zcraft.acgpicdownload.gui.scenes;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.GUI;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeSceneController implements Initializable {
    @javafx.fxml.FXML
    public AnchorPane mainPane;
    TranslateTransition tt = new TranslateTransition();
    @javafx.fxml.FXML
    private VBox controls;
    @javafx.fxml.FXML
    private MFXButton fetchBtn;
    @javafx.fxml.FXML
    private MFXButton galleryBtn;
    private GUI gui;

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void playAnimation() {
        tt.setFromX(0 - controls.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void hide() {
        tt.setFromX(0);
        tt.setToX(0 - controls.getWidth());
        tt.play();
    }

    @javafx.fxml.FXML
    public void fetchBtnOnAction(ActionEvent actionEvent) {
        hide();
        gui.openFetchPane();
    }

    @javafx.fxml.FXML
    public void galleryBtnOnAction(ActionEvent actionEvent) {
        hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tt.setNode(controls);
        tt.setAutoReverse(true);
        tt.setRate(0.008);
        tt.setDuration(Duration.millis(3));
        tt.setInterpolator(Interpolator.EASE_BOTH);
    }
}
