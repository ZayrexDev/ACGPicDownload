package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import xyz.zcraft.acgpicdownload.gui.GUI;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPaneController implements Initializable {
    public MFXButton maximizeBtn;
    @Setter
    @Getter
    GUI gui;
    double w = 800;
    double h = 500 - 30;
    double origMouseX;
    double origMouseY;
    double origStageW;
    double origStageH;
    @Getter
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @Getter
    @javafx.fxml.FXML
    private ImageView background;
    @javafx.fxml.FXML
    private Label titleLbl;
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
    @Getter
    @javafx.fxml.FXML
    private HBox titlePane;
    @Getter
    private boolean transparent = false;
    private double origStageX;
    private double origStageY;
    private Image image;

    public void setBackground(InputStream stream) {
        image = new Image(stream);
        fitBackground();
    }

    private void fitBackground() {
        if (image == null) return;
        Rectangle2D vp;

        if (image.getWidth() / image.getHeight() > w / h) {
            background.setFitHeight(h);
            blurImg.setFitHeight(h);
            background.setFitWidth(0);
            blurImg.setFitWidth(0);
            vp = new Rectangle2D((image.getWidth() - (image.getHeight() / h * w)) / 2, 0, image.getWidth(), image.getHeight());
        } else {
            background.setFitWidth(w);
            blurImg.setFitWidth(w);
            background.setFitHeight(0);
            blurImg.setFitHeight(0);
            vp = new Rectangle2D(0, (image.getHeight() - image.getWidth() / w * h) / 2, image.getWidth(), image.getHeight());
        }

        background.setImage(image);
        background.setViewport(vp);
        WritableImage snapshot = background.snapshot(new SnapshotParameters(), null);
        blurImg.setImage(snapshot);
    }

    public void setTransparent() {
        transparent = true;
        blurImg.setVisible(false);
        background.setVisible(false);
        titleLbl.setTextFill(Color.BLACK);
        closeBtn.setGraphic(new MFXFontIcon("fas-xmark", Color.BLACK));
        minimizeBtn.setGraphic(new MFXFontIcon("fas-minus", Color.BLACK));
        maximizeBtn.setGraphic(new MFXFontIcon("fas-expand", Color.BLACK));
    }

    public void setProgress(double p) {
        initProgressBar.setProgress(p == 1 ? -1 : p);
    }

    public void loadDone() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(initPane);
        ft.setToValue(0);
        ft.setFromValue(1);
        ft.setDuration(Duration.millis(10));
        ft.setRate(0.1);
        ft.setOnFinished((a) -> initPane.setVisible(false));
        ft.play();

        ft.setNode(blurImg);
        ft.setOnFinished((a) -> blurImg.setVisible(false));
        ft.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeBtn.setText("");
        closeBtn.setGraphic(new MFXFontIcon("fas-xmark", Color.WHITE));
        minimizeBtn.setText("");
        minimizeBtn.setGraphic(new MFXFontIcon("fas-minus", Color.WHITE));
        maximizeBtn.setText("");
        maximizeBtn.setGraphic(new MFXFontIcon("fas-expand", Color.WHITE));
    }

    public void reload() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(initPane);
        ft.setToValue(1);
        ft.setFromValue(0);
        ft.setDuration(Duration.millis(10));
        ft.setRate(0.1);
        ft.setOnFinished((a) -> initPane.setVisible(false));
        ft.play();

        ft.setNode(blurImg);
        ft.setOnFinished((a) -> blurImg.setVisible(false));
        ft.play();
    }

    public void init() {
        initPane.setVisible(true);
    }

    @FXML
    private void mouseDragged(MouseEvent e) {
        gui.mainStage.setX(e.getScreenX() - origMouseX + origStageX);
        gui.mainStage.setY(e.getScreenY() - origMouseY + origStageY);
    }

    @FXML
    private void startMoving(MouseEvent e) {
        origStageX = gui.mainStage.getX();
        origStageY = gui.mainStage.getY();
        origMouseX = e.getScreenX();
        origMouseY = e.getScreenY();
    }
    @FXML
    private void minimizeBtnOnAction() {
        gui.mainStage.setIconified(true);
    }
    @FXML
    private void closeBtnOnAction() {
        System.exit(0);
    }

    @FXML
    public void resizeStart(MouseEvent event) {
        origMouseX = event.getScreenX();
        origMouseY = event.getScreenY();
        origStageW = gui.mainStage.getWidth();
        origStageH = gui.mainStage.getHeight();
        origStageX = gui.mainStage.getX();
        origStageY = gui.mainStage.getY();
    }

    @FXML
    public void resizeES(MouseEvent event) {
        resizeE(event);
        resizeS(event);
    }

    @FXML
    private void resizeS(MouseEvent event) {
        double tempH = origStageH + event.getScreenY() - origMouseY - 30;
        if (tempH > 398) {
            gui.mainStage.setHeight(tempH + 30);
            this.h = tempH;
        }
        fitBackground();
    }

    @FXML
    private void resizeE(MouseEvent event) {
        double tempW = origStageW + event.getScreenX() - origMouseX;
        if (tempW > 625) {
            gui.mainStage.setWidth(tempW);
            this.w = tempW;
        }
        fitBackground();
    }

    public void maximizeBtnOnAction() {
        gui.mainStage.setMaximized(!gui.mainStage.isMaximized());
        this.w = gui.mainStage.getWidth();
        this.h = gui.mainStage.getHeight() - 30;
        fitBackground();
    }

    public void resizeW(MouseEvent mouseEvent) {
        double tempW = origStageW + origMouseX - mouseEvent.getScreenX();
        if (tempW > 625) {
            gui.mainStage.setWidth(tempW);
            gui.mainStage.setX(origStageX - tempW + origStageW);
            this.w = tempW;
        }
        fitBackground();
    }

    public void resizeWN(MouseEvent mouseEvent) {
        resizeS(mouseEvent);
        resizeW(mouseEvent);
    }
}
