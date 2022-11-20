package xyz.zcraft.acgpicdownload.gui.scenes;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WelcomeSceneController {
    public VBox controls;

    public void playAnimation() {
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(controls);
        tt.setFromX(0 - controls.getWidth());
        tt.setToX(0);
        tt.setAutoReverse(true);
        tt.setRate(0.015);
        tt.setDuration(Duration.millis(20));
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.play();
    }
}
