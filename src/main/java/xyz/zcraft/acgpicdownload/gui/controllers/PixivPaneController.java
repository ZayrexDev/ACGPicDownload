package xyz.zcraft.acgpicdownload.gui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivAccount;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivDownloadUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

public class PixivPaneController implements Initializable {
    final TranslateTransition ttP = new TranslateTransition();
    @FXML
    private VBox fetchBtns;
    @FXML
    private ImageView userImg;
    @FXML
    private Label userNameLabel;
    @FXML
    private VBox controls;
    @Getter
    @javafx.fxml.FXML
    private AnchorPane mainPane;

    @Getter
    @Setter
    private GUI gui;

    public void pixivMenuBtnOnAction() {
        closePixivPane();
        gui.openPixivMenuPane();
    }

    public void pixivDownloadBtnOnAction() {
        closePixivPane();
        gui.openPixivDownloadPane();
    }

    public void pixivBackBtnOnAction() {
        gui.menuPaneController.showMain();
        closePixivPane();
    }

    private void closePixivPane() {
        ttP.stop();
        ttP.setFromX(0);
        ttP.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        ttP.setToX(0 - controls.getWidth());
        ttP.setOnFinished((e) -> mainPane.setVisible(false));
        ttP.play();
    }

    @javafx.fxml.FXML
    private void pixivDiscBtnOnAction() {
        closePixivPane();
        gui.openPixivDiscPane();
    }

    @FXML
    public void pixivUserBtnOnAction() {
        closePixivPane();
        gui.openPixivUserPane();
    }

    @FXML
    public void pixivRelatedBtnOnAction() {
        closePixivPane();
        gui.openPixivRelatedPane();
    }

    public void pixivRankingBtnOnAction() {
        closePixivPane();
        gui.openPixivRankingPane();
    }

    public void pixivSearchBtnOnAction() {
        closePixivPane();
        gui.openPixivSearchPane();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controls.setVisible(false);
        mainPane.setVisible(false);

        ttP.setNode(controls);
        ttP.setAutoReverse(false);
        ttP.setRate(0.008 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        ttP.setDuration(Duration.millis(3));
        ttP.setInterpolator(Interpolator.EASE_BOTH);
    }

    public void openPixivPane() {
        reloadAccount();
        ttP.stop();
        ttP.setFromX(0 - controls.getWidth());
        ttP.setRate(0.01 * ConfigManager.getDoubleIfExist("aniSpeed", 1.0));
        ttP.setToX(0);
        ttP.setOnFinished(null);
        mainPane.setVisible(true);
        controls.setVisible(true);
        ttP.play();
    }

    public void openAccountManager() {
        gui.openPixivAccountPane();
    }

    public void reloadAccount() {
        new Thread(() -> {
            PixivAccount selectedAccount = ConfigManager.getSelectedAccount();
            if (selectedAccount != null) {
                Platform.runLater(() -> {
                    userNameLabel.setText(selectedAccount.getName());
                    fetchBtns.setDisable(false);
                });
                try {
                    URLConnection urlConnection = new URL(selectedAccount.getProfileImg()).openConnection();
                    urlConnection.setRequestProperty("Referer", PixivDownloadUtil.REFERER);
                    Image image = new Image(urlConnection.getInputStream());
                    Platform.runLater(() -> userImg.setImage(image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Platform.runLater(() -> {
                    userNameLabel.setText(ResourceBundleUtil.getString("gui.pixiv.account.notLogin"));
                    userImg.setImage(null);
                    fetchBtns.setDisable(true);
                });
            }
        }).start();

    }
}
