package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.apache.log4j.Logger;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.PixivFetchPane;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivAccount;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivFetchUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PixivUserPaneController extends PixivFetchPane {
    @javafx.fxml.FXML
    private MFXTextField uidField;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
    }

    public static final Logger logger = Logger.getLogger(PixivUserPaneController.class);

    @FXML
    @Override
    public void fetchBtnOnAction() {
        if (uidField.getText().startsWith("https://www.pixiv.net/users/"))
            uidField.setText(uidField.getText().substring(uidField.getText().lastIndexOf("/") + 1));
        loadingPane.setVisible(true);
        operationLabel.setText(ResourceBundleUtil.getString(""));
        ft.stop();
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setOnFinished(null);
        ft.play();

        new Thread(() -> {
            try {
                long start = System.currentTimeMillis();
                PixivAccount selectedAccount = ConfigManager.getSelectedAccount();
                logger.info("Fetching uid " + uidField.getText() + " with account " + selectedAccount.getName());

                Platform.runLater(() -> {
                    operationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                    subOperationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                });

                Set<String> artIDs = PixivFetchUtil.fetchUser(
                        uidField.getText(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                );
                List<String> queryString = PixivFetchUtil.buildQueryString(artIDs);

                LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();

                for (String s : queryString) {
                    pixivArtworks.addAll(
                            PixivFetchUtil.getUserArtworks(s, uidField.getText(), ConfigManager.getConfig().getString("proxyHost"),
                                    ConfigManager.getConfig().getInteger("proxyPort")));
                }
                logger.info("Fetched " + pixivArtworks.size() + " artworks, getting related artworks for " + relatedDepthSlider.getValue() + " times");

                getRelated(pixivArtworks, (int) relatedDepthSlider.getValue(), getCookie(), subOperationLabel);

                Platform.runLater(() -> data.addAll(pixivArtworks));
                Notice.showSuccess(
                        String.format(
                                Objects.requireNonNull(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetched"))
                                , pixivArtworks.size()
                        ),
                        gui.mainPane
                );
                logger.info("Fetch completed successfully within " + (System.currentTimeMillis() - start) + " ms");
            } catch (IOException e) {
                logger.error("Fetch user failed", e);
                Main.logError(e);
                gui.showError(e);
            } finally {
                ft.stop();
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished((e) -> loadingPane.setVisible(false));
                ft.play();
            }
        }).start();
    }
}
