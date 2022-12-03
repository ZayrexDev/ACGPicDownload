package xyz.zcraft.acgpicdownload.gui.base;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;

public class MyPane implements Initializable{
    protected GUI gui;
    private TranslateTransition tt = new TranslateTransition();
    private TranslateTransition tt2 = new TranslateTransition();
    @FXML
    protected AnchorPane mainPane;

    public void show() {
        tt.stop();
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setFromY(mainPane.getHeight());
        tt.setOnFinished((e)->tt2.play());
        tt.setToY(-10);
        tt2.setFromY(-10);
        tt2.setOnFinished(null);
        tt2.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    public void hide() {
        tt.stop();
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setFromY(0);
        tt.setToY(mainPane.getHeight());
        mainPane.setVisible(true);
        tt.setOnFinished((e) -> Platform.runLater(() -> mainPane.setVisible(false)));
        tt.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.setVisible(false);

        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        tt2.setNode(mainPane);
        tt2.setAutoReverse(true);
        tt2.setRate(0.05 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt2.setDuration(Duration.millis(5));
        tt2.setInterpolator(Interpolator.EASE_BOTH);
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
    }

    public AnchorPane getMainPane() {
        return mainPane;
    }
}
