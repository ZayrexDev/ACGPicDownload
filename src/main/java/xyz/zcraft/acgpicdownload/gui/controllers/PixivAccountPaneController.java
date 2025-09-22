package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivAccount;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class PixivAccountPaneController implements Initializable {
    public MFXComboBox<PixivAccount> accountCombo;
    public MFXTextField cookieField;
    public MFXButton cookieHelpBtn;
    @Setter
    @Getter
    GUI gui;
    @javafx.fxml.FXML
    private ImageView bg;
    @Setter
    @Getter
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private VBox accountsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setVisible(false);
        accountCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(PixivAccount object) {
                return object == null ? null : object.getName();
            }

            @Override
            public PixivAccount fromString(String string) {
                return accountCombo.getItems().filtered((o) -> o.getName().equals(string)).get(0);
            }
        });

        accountCombo.getItems().clear();
        accountCombo.getItems().addAll(ConfigManager.getAccounts());
        accountCombo.selectedIndexProperty().addListener(observable -> {
            ConfigManager.setSelectedAccount(accountCombo.getSelectedItem());
            if (gui != null) gui.pixivPaneController.reloadAccount();
        });

        accountCombo.selectFirst();

        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("mfx-info-circle"));
    }

    public void hide() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(mainPane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));
        ft.setOnFinished(actionEvent -> Platform.runLater(() -> mainPane.setVisible(false)));

        ft.play();
    }

    public void setBlur(Image img) {
        bg.setImage(img);
    }

    public void show() {
        setBlur(gui.mainPaneController.isTransparent() ? null : gui.mainPane.snapshot(new SnapshotParameters(), null));
        bg.fitWidthProperty().bind(mainPane.widthProperty());
        bg.fitHeightProperty().bind(mainPane.heightProperty());
        bg.setViewport(new Rectangle2D(0, 0, mainPane.getWidth(), mainPane.getHeight()));

        FadeTransition ft = new FadeTransition();
        ft.setNode(mainPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        mainPane.setVisible(true);
        ft.play();
    }

    public static final Logger logger = Logger.getLogger(PixivAccountPaneController.class);

    public void addAccount() throws IOException {
        HashMap<String, String> stringStringHashMap = PixivFetchUtil.parseCookie(cookieField.getText());
        String cookie = "PHPSESSID" + "=" + stringStringHashMap.get("PHPSESSID");
        PixivAccount account = PixivFetchUtil.getAccount(
                cookie,
                ConfigManager.getConfig().getString("proxyHost"),
                ConfigManager.getConfig().getInteger("proxyPort")
        );
        if (account != null) {
            account.setCookie(cookie);
            ConfigManager.getAccounts().add(account);
            accountCombo.getItems().add(account);
            Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.account.added"), gui.mainPane);
            cookieField.setText("");
            accountCombo.selectItem(account);
            ConfigManager.saveConfig();
            gui.pixivPaneController.reloadAccount();

            logger.info("Account " + account.getName() + " added");
        } else {
            logger.error("Failed to add account");
            Notice.showError(ResourceBundleUtil.getString("gui.pixiv.account.addFailed"), gui.mainPane);
        }
    }

    public void delAccount() throws IOException {
        if (accountCombo.getSelectedIndex() != -1) {
            ConfigManager.getAccounts().remove(accountCombo.getSelectionModel().getSelectedItem());
            accountCombo.getItems().remove(accountCombo.getSelectedIndex());
            ConfigManager.saveConfig();
            gui.pixivPaneController.reloadAccount();
        }
    }

    public void refreshAccount() throws IOException {
        PixivAccount account = PixivFetchUtil.getAccount(
                accountCombo.getSelectedItem().getCookie(),
                ConfigManager.getConfig().getString("proxyHost"),
                ConfigManager.getConfig().getInteger("proxyPort")
        );
        if (account != null) {
            account.setCookie(accountCombo.getSelectedItem().getCookie());
            ConfigManager.getAccounts().remove(accountCombo.getSelectedItem());
            accountCombo.getItems().remove(accountCombo.getSelectedItem());
            ConfigManager.getAccounts().add(account);
            accountCombo.getItems().add(account);
            Notice.showSuccess(ResourceBundleUtil.getString("gui.pixiv.account.added"), gui.mainPane);
            accountCombo.selectItem(account);
            ConfigManager.saveConfig();
            gui.pixivPaneController.reloadAccount();

            logger.info("Refreshed account " + account.getName());
        } else {
            Notice.showError(ResourceBundleUtil.getString("gui.pixiv.account.addFailed"), gui.mainPane);
        }
    }

    @FXML
    public void cookieHelp() throws URISyntaxException, IOException {
        if (Locale.getDefault().equals(Locale.CHINA) || Locale.getDefault().equals(Locale.TAIWAN)) {
            java.awt.Desktop.getDesktop()
                    .browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/%E8%8E%B7%E5%8F%96Cookie"));
        } else {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/zxzxy/ACGPicDownload/wiki/Get-cookie"));
        }
    }
}
