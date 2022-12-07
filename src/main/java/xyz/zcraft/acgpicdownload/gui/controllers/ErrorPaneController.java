package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;

public class ErrorPaneController {
    @javafx.fxml.FXML
    private AnchorPane errorPane;
    @javafx.fxml.FXML
    private Label errorLabel;
    @javafx.fxml.FXML
    private MFXButton errorOkBtn;
    @javafx.fxml.FXML
    private ImageView bg;
    private Pane parent;

    public static ErrorPaneController getInstance(Pane parent) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ResourceLoader.loadURL("fxml/ErrorPane.fxml")), ResourceBundleUtil.getResource());
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ((ErrorPaneController) loader.getController()).setParent(parent);
    }

    public ErrorPaneController setParent(Pane parent) {
        this.parent = parent;
        return this;
    }

    public AnchorPane getErrorPane() {
        return errorPane;
    }

    @javafx.fxml.FXML
    private void copyErrorMsg() {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(errorLabel.getText()), null);
        Notice.showSuccess(ResourceBundleUtil.getString("gui.fetch.table.copy"), parent);
    }

    public void hide() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(errorPane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));
        ft.setOnFinished(actionEvent -> Platform.runLater(() -> errorPane.setVisible(false)));

        ft.play();
    }

    public void setBlur(Image img) {
        bg.setImage(img);
    }

    public void show() {
        bg.fitWidthProperty().bind(errorPane.widthProperty());
        bg.fitHeightProperty().bind(errorPane.heightProperty());
        bg.setViewport(new Rectangle2D(0, 0, errorPane.getWidth(), errorPane.getHeight()));

        FadeTransition ft = new FadeTransition();
        ft.setNode(errorPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        errorPane.setVisible(true);
        ft.play();
    }

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    public void errorOkBtnOnAction() {
        hide();
    }

    @FXML
    public void openFAQ() throws IOException, URISyntaxException {
        if (Locale.getDefault().equals(Locale.CHINA) || Locale.getDefault().equals(Locale.TAIWAN)) {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/常见问题"));
        } else {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/FAQ"));
        }
    }
}
