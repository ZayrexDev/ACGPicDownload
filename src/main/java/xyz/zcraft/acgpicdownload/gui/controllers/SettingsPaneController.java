package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsPaneController implements Initializable {
    @javafx.fxml.FXML
    private AnchorPane mainPane;

    @javafx.fxml.FXML
    private MFXButton backBtn;

    @javafx.fxml.FXML
    private MFXTextField proxyField;

    @javafx.fxml.FXML
    private MFXSlider aniSpeedSlider;

    private String proxyHost;
    private int proxyPort;

    GUI gui;
    TranslateTransition tt = new TranslateTransition();

    public void show() {
        tt.stop();
        AnchorPane.setTopAnchor(mainPane, 0d);
        AnchorPane.setBottomAnchor(mainPane, 0d);
        AnchorPane.setLeftAnchor(mainPane, 0d);
        AnchorPane.setRightAnchor(mainPane, 0d);
        mainPane.maxWidthProperty().bind(gui.mainStage.widthProperty());
        mainPane.maxHeightProperty().bind(gui.mainStage.heightProperty());
        tt.setFromY(mainPane.getHeight());
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setOnFinished(null);
        tt.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    public void hide() {
        tt.stop();
        tt.setFromY(0);
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setToY(mainPane.getHeight());
        mainPane.setVisible(true);
        tt.setOnFinished((e) -> Platform.runLater(() -> mainPane.setVisible(false)));
        tt.play();
        gui.welcomePaneController.showMain();
    }

    @javafx.fxml.FXML
    public void backBtnOnAction() {
        hide();
    }

    @javafx.fxml.FXML
    public void saveConfigBtnOnAction() {
        saveConfig();
    }

    public String getProxyHost() {
        return "127.0.0.1";
    }

    public int getProxyPort() {
        return 7890;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setVisible(false);
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        proxyField.textProperty().addListener(this::verifyProxy);
        restoreConfig();

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
    }

    private void verifyProxy(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!newValue.isEmpty()) {
            String[] str = newValue.split(":");
            if (str.length == 2) {
                proxyHost = str[0];
                try {
                    proxyPort = Integer.parseInt(str[1]);
                } catch (NumberFormatException ignored) {
                    proxyHost = null;
                    proxyPort = 0;
                    proxyField.setTextFill(Color.RED);
                    return;
                }
            }
            if (proxyHost != null && proxyPort != 0) {
                System.getProperties().put("proxySet", "true");
                System.getProperties().put("proxyHost", proxyHost);
                System.getProperties().put("proxyPort", String.valueOf(proxyPort));
                proxyField.setTextFill(MFXTextField.DEFAULT_TEXT_COLOR);
            } else {
                proxyField.setTextFill(Color.RED);
            }
        } else {
            proxyHost = null;
            proxyPort = 0;
            proxyField.setTextFill(MFXTextField.DEFAULT_TEXT_COLOR);
        }
    }

    public void saveConfig() {
        JSONObject obj = ConfigManager.getConfig();

        if (proxyPort != 0 && proxyHost != null) {
            obj.put("proxyHost", proxyHost);
            obj.put("proxyPort", proxyPort);
            obj.put("aniSpeed", aniSpeedSlider.getValue());
        }
        try {
            ConfigManager.saveConfig();
            Notice.showSuccess(ResourceBundleUtil.getString("gui.fetch.notice.saved"), gui.mainPane);
        } catch (IOException e) {
            gui.showError(e);
            Main.logError(e);
        }
    }

    public void restoreConfig() {
        JSONObject json = ConfigManager.getConfig();
        if (json.getString("proxyHost") != null && json.getInteger("proxyPort") != 0) {
            proxyField.setText(json.getString("proxyHost") + ":" + json.getInteger("proxyPort"));
        }
        aniSpeedSlider.setValue(ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
