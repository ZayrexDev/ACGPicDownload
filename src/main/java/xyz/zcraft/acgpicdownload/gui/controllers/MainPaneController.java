package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.animation.FadeTransition;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.GUI;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPaneController implements Initializable {
    GUI gui;
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private ImageView background;
    @javafx.fxml.FXML
    private VBox initPane;
    @javafx.fxml.FXML
    private ImageView blurImg;
    @javafx.fxml.FXML
    private MFXProgressBar initProgressBar;

    public AnchorPane getMainPane() {
        return mainPane;
    }

    public ImageView getBackground() {
        return background;
    }

    public void setBackground(InputStream stream) {
        background.setImage(new Image(stream));
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public VBox getInitPane() {
        return initPane;
    }

    public ImageView getBlurImg() {
        return blurImg;
    }

    public void setProgress(double p) {
        if (p == 1) {
            initProgressBar.setProgress(-1);
        } else {
            initProgressBar.setProgress(p);
        }
    }

    public void initDone() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(initPane);
        ft.setToValue(0);
        ft.setFromValue(1);
        ft.setDuration(Duration.millis(10));
        ft.setRate(0.1);
        ft.setOnFinished((e) -> initPane.setVisible(false));
        ft.play();

        ft.setNode(blurImg);
        ft.setOnFinished((e) -> blurImg.setVisible(false));
        ft.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        background.fitWidthProperty().bind(mainPane.widthProperty());
        background.fitHeightProperty().bind(mainPane.heightProperty());
        blurImg.fitWidthProperty().bind(mainPane.widthProperty());
        blurImg.fitHeightProperty().bind(mainPane.heightProperty());
        // mainPane.widthProperty().addListener((observable, oldValue, newValue) -> blurImg
        //         .setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight())));
        // mainPane.heightProperty().addListener((observable, oldValue, newValue) -> blurImg
        //         .setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight())));
        blurImg.setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight()));
        background.setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight()));

        WritableImage snapshot = background.snapshot(new SnapshotParameters(), null);
        blurImg.setImage(snapshot);

        initPane.setVisible(true);
    }
}
