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
    protected TranslateTransition tt = new TranslateTransition();
    @FXML
    protected AnchorPane mainPane;

    public void show() {
        tt.stop();
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
        tt.setFromY(mainPane.getHeight());
        tt.setOnFinished(null);
        tt.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    public void hide() {
        tt.stop();
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01);
        tt.setDuration(Duration.millis(5));
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setInterpolator(Interpolator.EASE_BOTH);
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
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
