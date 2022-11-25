package xyz.zcraft.acgpicdownload.gui;

import java.io.IOException;
import java.util.Objects;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Notice {
    @javafx.fxml.FXML
    AnchorPane pane;

    @javafx.fxml.FXML
    Label msgLbl;

    Pane parent;

    public static Notice getInstance(String message, Pane parent){
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ResourceLoader.loadURL("fxml/Notice.fxml")));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Notice controller = loader.getController();
        controller.parent = parent;
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

    public void show(){
        pane.setVisible(true);
        pane.setTranslateY(-100);
        pane.setOpacity(0);
        KeyValue kv1 = new KeyValue(pane.translateYProperty(), 0,Interpolator.EASE_OUT);
        KeyValue kv2 = new KeyValue(pane.opacityProperty(), 1, Interpolator.EASE_OUT);
        KeyFrame kf1 = new KeyFrame(Duration.millis(200), kv1, kv2);
        KeyFrame kf12 = new KeyFrame(Duration.millis(1600), kv1, kv2);

        KeyValue kv12 = new KeyValue(pane.opacityProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf11 = new KeyFrame(Duration.millis(1800), kv12);

        Timeline tl = new Timeline(kf1,kf12,kf11);
        tl.setOnFinished((e)->parent.getChildren().remove(pane));
        tl.play();
    }
}
