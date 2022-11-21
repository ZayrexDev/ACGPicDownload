package xyz.zcraft.acgpicdownload.gui.scenes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import xyz.zcraft.acgpicdownload.gui.GUI;

import java.io.InputStream;

public class MainPaneController {
    GUI gui;
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private ImageView background;

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
}
