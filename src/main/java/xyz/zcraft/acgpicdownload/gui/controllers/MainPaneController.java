package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private VBox stagePane;
    @javafx.fxml.FXML
    private ImageView blurImg;
    @javafx.fxml.FXML
    private MFXProgressBar initProgressBar;
    @javafx.fxml.FXML
    private MFXButton closeBtn;
    @javafx.fxml.FXML
    private MFXButton minimizeBtn;

    @javafx.fxml.FXML
    private HBox titlePane;

    public AnchorPane getMainPane() {
        return mainPane;
    }

    public ImageView getBackground() {
        return background;
    }

    public void setBackground(InputStream stream) {
        background.setImage(new Image(stream));
        WritableImage snapshot = background.snapshot(new SnapshotParameters(), null);
        blurImg.setImage(snapshot);
        blurImg.setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight()));
        background.setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight()));
    }

    private boolean transparent = false;
    public void setTransparent(boolean transparent){
        this.transparent = transparent;

        if(transparent == true){
            blurImg.setVisible(false);
        }
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
        background.fitWidthProperty().bind(gui.mainStage.widthProperty());
        background.fitHeightProperty().bind(gui.mainStage.heightProperty());
        blurImg.fitWidthProperty().bind(gui.mainStage.widthProperty());
        blurImg.fitHeightProperty().bind(gui.mainStage.heightProperty());
        blurImg.setViewport(new Rectangle2D(0, 0, gui.mainStage.getWidth(), gui.mainStage.getHeight()));
        background.setViewport(new Rectangle2D(0, 0, gui.mainStage.getWidth(), gui.mainStage.getHeight()));
        WritableImage snapshot = background.snapshot(new SnapshotParameters(), null);
        blurImg.setImage(snapshot);
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
        // mainPane.widthProperty().addListener((observable, oldValue, newValue) -> blurImg
        //         .setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight())));
        // mainPane.heightProperty().addListener((observable, oldValue, newValue) -> blurImg
        //         .setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight())));

        initPane.setVisible(true);
        closeBtn.setText("");
        closeBtn.setGraphic(new MFXFontIcon("mfx-x",Color.WHITE));
        minimizeBtn.setText("");
        minimizeBtn.setGraphic(new MFXFontIcon("mfx-minus", Color.WHITE));
    }

    @FXML
    private void mouseDragged(MouseEvent e){
        gui.mainStage.setX(e.getScreenX() - origX + origStageX);
        gui.mainStage.setY(e.getScreenY() - origY + origStageY);
    }

    private double origX;
    private double origY;
    private double origStageX;
    private double origStageY;

    @FXML
    private void startMoving(MouseEvent e){
        origStageX = gui.mainStage.getX();
        origStageY = gui.mainStage.getY();
        origX = e.getScreenX();
        origY = e.getScreenY();
    }

    @FXML
    private void minimizeBtnOnAction(){
        gui.mainStage.setIconified(true);
    }

    @FXML
    private void closeBtnOnAction() {
        System.exit(0);
    }

    public HBox getTitlePane() {
        return titlePane;
    }
}
