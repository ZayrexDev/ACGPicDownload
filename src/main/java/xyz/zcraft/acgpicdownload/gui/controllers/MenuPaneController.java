package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class MenuPaneController implements Initializable {
    final TranslateTransition tt = new TranslateTransition();
    @javafx.fxml.FXML
    private AnchorPane mainPane;

    public AnchorPane getMainPane() {
        return mainPane;
    }

    @javafx.fxml.FXML
    private VBox controls;
    @javafx.fxml.FXML
    private MFXButton fetchBtn;
    private GUI gui;
    @javafx.fxml.FXML
    private Label welcomeLabel;

    public GUI getGui() {
        return gui;
    }
    public void setGui(GUI gui) {
        this.gui = gui;
    }
    public void showMain() {
        tt.stop();
        tt.setFromX(0 - controls.getWidth());
        tt.setToX(0);
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setOnFinished(null);
        mainPane.setVisible(true);
        controls.setVisible(true);
        tt.play();
    }

    public void hideMain() {
        tt.stop();
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setFromX(0);
        tt.setToX(0 - controls.getWidth());
        tt.setOnFinished((e) -> mainPane.setVisible(false));
        tt.play();
    }

    @javafx.fxml.FXML
    public void fetchBtnOnAction() {
        hideMain();
        gui.openFetchPane();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controls.setVisible(false);
        mainPane.setVisible(false);

        tt.setNode(controls);
        tt.setAutoReverse(false);
        tt.setRate(0.008 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setDuration(Duration.millis(3));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        Calendar c = Calendar.getInstance();
        int i = c.get(Calendar.HOUR_OF_DAY);
        if (7 < i && i <= 12) {
            welcomeLabel.setText(resourceBundle.getString("gui.welcome.greet.morn"));
        } else if (12 < i && i <= 15) {
            welcomeLabel.setText(resourceBundle.getString("gui.welcome.greet.noon"));
        } else if (15 < i && i <= 20) {
            welcomeLabel.setText(resourceBundle.getString("gui.welcome.greet.afternoon"));
        } else if (20 < i || i < 7) {
            welcomeLabel.setText(resourceBundle.getString("gui.welcome.greet.night"));
        }
    }

    public void openSettingsPane() {
        hideMain();
        gui.openSettingsPane();
    }

    public void openPixivPane() {
        hideMain();
        gui.pixivPaneController.openPixivPane();
    }
}
