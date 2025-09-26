package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivAccount;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class PixivAccountPaneController implements Initializable {
    public static final Logger logger = Logger.getLogger(PixivAccountPaneController.class);
    public MFXComboBox<PixivAccount> accountCombo;
    public MFXTextField cookieField;
    public MFXButton cookieHelpBtn;
    public MFXButton browserLoginBtn;
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
                return accountCombo.getItems().filtered((o) -> o.getName().equals(string)).getFirst();
            }
        });

        accountCombo.getItems().clear();
        accountCombo.getItems().addAll(ConfigManager.getAccounts());
        accountCombo.selectedIndexProperty().addListener(_ -> {
            ConfigManager.setSelectedAccount(accountCombo.getSelectedItem());
            if (gui != null) gui.pixivPaneController.reloadAccount();
        });

        accountCombo.selectFirst();

        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("fas-circle-info"));
        browserLoginBtn.setGraphic(new MFXFontIcon("fas-globe"));
    }

    public void hide() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(mainPane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));
        ft.setOnFinished(_ -> Platform.runLater(() -> mainPane.setVisible(false)));

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

    public void addAccount(String cookieOrig) throws IOException {
        HashMap<String, String> stringStringHashMap = PixivFetchUtil.parseCookie(cookieOrig);
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

    public void addAccount() throws IOException {
        addAccount(cookieField.getText());
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
                    .browse(new URI("https://github.com/ZayrexDev/ACGPicDownload/wiki/%E8%8E%B7%E5%8F%96Cookie"));
        } else {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/ZayrexDev/ACGPicDownload/wiki/Get-cookie"));
        }
    }

    public void browserLoginBtnOnAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ResourceBundleUtil.getString("gui.pixiv.account.add.browser.alert.title"));
        alert.setHeaderText(ResourceBundleUtil.getString("gui.pixiv.account.add.browser.alert.header"));
        alert.setContentText(ResourceBundleUtil.getString("gui.pixiv.account.add.browser.alert.content"));
        alert.showAndWait();

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        if (!alert.getResult().equals(ButtonType.OK)) {
            return;
        }

        Stage browserStage = new Stage();
        browserStage.setTitle("Pixiv Login - ACGPicDownload");
        browserStage.setWidth(1000);
        browserStage.setHeight(600);

        final FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(ResourceLoader.loadURL("fxml/PixivLoginPane.fxml")));
        final Parent load;
        try {
            load = fxmlLoader.load();
            final PixivLoginPaneController controller = fxmlLoader.getController();
            final Scene scene = new Scene(load);
            browserStage.setScene(scene);

            controller.reload();

            browserStage.showAndWait();

            CookieStore cs = manager.getCookieStore();
            final List<HttpCookie> httpCookies = cs.get(new URI("https://pixiv.net"));

            httpCookies.stream().filter(e -> e.getName().equalsIgnoreCase("PHPSESSID")).findFirst()
                    .ifPresentOrElse(e -> {
                        try {
                            addAccount("PHPSESSID=" + e.getValue());
                        } catch (IOException ex) {
                            Notice.showError(ResourceBundleUtil.getString("gui.pixiv.account.addFailed"), gui.mainPane);
                        }
                    }, () -> Notice.showError(ResourceBundleUtil.getString("gui.pixiv.account.addFailed"), gui.mainPane));
        } catch (IOException e) {
            Notice.showError(ResourceBundleUtil.getString("gui.pixiv.account.addFailed"), gui.mainPane);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
