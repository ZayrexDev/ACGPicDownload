package xyz.zcraft.acgpicdownload.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class Notice {
    @javafx.fxml.FXML
    AnchorPane pane;

    @javafx.fxml.FXML
    Label msgLbl;

    public static Notice getInstance(String message, Pane parent){
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ResourceLoader.loadURL("fxml/Notice.fxml")));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Notice controller = loader.getController();
        double d = (parent.getWidth() / 2) - message.length() * 20;

        controller.getLabel().setText(message);

        AnchorPane.setTopAnchor(controller.getPane(), 10.0);
        AnchorPane.setLeftAnchor(controller.getPane(), d);
        AnchorPane.setRightAnchor(controller.getPane(), d);
        parent.getChildren().addAll(controller.getPane());

        controller.getPane().setVisible(false);

        return controller;
    }

    private AnchorPane getPane() {
        return pane;
    }

    private Label getLabel() {
        return msgLbl;
    }

    private static final Color SU_BG = Color.rgb(212, 237, 218);
    private static final Color SU_TEXT = Color.rgb(46, 107, 60);
    private static final Color ERR_BG = Color.rgb(248, 215, 218);
    private static final Color ERR_TEXT = Color.rgb(127, 46, 53);

    public static void showSuccess(String message, Pane parent) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ResourceLoader.loadURL("fxml/Notice.fxml")));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Notice controller = loader.getController();
        controller.setColors(SU_BG, SU_TEXT);
        AnchorPane p = controller.getPane();
        double d = (parent.getWidth() / 2) - message.length() * 20;
        controller.getLabel().setText(message);
        AnchorPane.setTopAnchor(p, 10.0);
        AnchorPane.setLeftAnchor(p, d);
        AnchorPane.setRightAnchor(p, d);
        parent.getChildren().addAll(p);

        p.setVisible(true);
        p.setTranslateY(-100);
        p.setOpacity(0);
        KeyValue kv1 = new KeyValue(p.translateYProperty(), 0, Interpolator.EASE_OUT);
        KeyValue kv2 = new KeyValue(p.opacityProperty(), 1, Interpolator.EASE_OUT);
        KeyFrame kf1 = new KeyFrame(Duration.millis(200), kv1, kv2);
        KeyFrame kf12 = new KeyFrame(Duration.millis(1600), kv1, kv2);

        KeyValue kv12 = new KeyValue(p.opacityProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf11 = new KeyFrame(Duration.millis(1800), kv12);

        Timeline tl = new Timeline(kf1, kf12, kf11);
        tl.setOnFinished((e) -> parent.getChildren().remove(p));
        tl.play();
    }

    public static void showError(String message, Pane parent) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ResourceLoader.loadURL("fxml/Notice.fxml")));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Notice controller = loader.getController();
        controller.setColors(ERR_BG, ERR_TEXT);
        AnchorPane p = controller.getPane();
        double d = (parent.getWidth() / 2) - message.length() * 20;
        controller.getLabel().setText(message);
        AnchorPane.setTopAnchor(p, 10.0);
        AnchorPane.setLeftAnchor(p, d);
        AnchorPane.setRightAnchor(p, d);
        parent.getChildren().addAll(p);

        p.setVisible(true);
        p.setTranslateY(-100);
        p.setOpacity(0);
        KeyValue kv1 = new KeyValue(p.translateYProperty(), 0, Interpolator.EASE_OUT);
        KeyValue kv2 = new KeyValue(p.opacityProperty(), 1, Interpolator.EASE_OUT);
        KeyFrame kf1 = new KeyFrame(Duration.millis(200), kv1, kv2);
        KeyFrame kf12 = new KeyFrame(Duration.millis(1600), kv1, kv2);

        KeyValue kv12 = new KeyValue(p.opacityProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf11 = new KeyFrame(Duration.millis(1800), kv12);

        Timeline tl = new Timeline(kf1, kf12, kf11);
        tl.setOnFinished((e) -> parent.getChildren().remove(p));
        tl.play();
    }

    public void setColors(Color backColor, Color textColor) {
        pane.setBackground(new Background(new BackgroundFill(backColor, null, null)));
        msgLbl.setTextFill(textColor);
    }
}
