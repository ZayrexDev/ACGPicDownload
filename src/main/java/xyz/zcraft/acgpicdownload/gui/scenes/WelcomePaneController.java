package xyz.zcraft.acgpicdownload.gui.scenes;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.GUI;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class WelcomePaneController implements Initializable {
    TranslateTransition tt = new TranslateTransition();
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

    public void playAnimation() {
        tt.setFromX(0 - controls.getWidth());
        tt.setToX(0);
        controls.setVisible(true);
        tt.play();
    }

    public void hide() {
        tt.setFromX(0);
        tt.setToX(0 - controls.getWidth());
        tt.setOnFinished((e)->controls.setVisible(false));
        tt.play();
    }

    @javafx.fxml.FXML
    public void fetchBtnOnAction() {
        hide();
        gui.openFetchPane();
    }

    @Deprecated
    public void galleryBtnOnAction() {
        hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tt.setNode(controls);
        tt.setAutoReverse(true);
        tt.setRate(0.008);
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
}
