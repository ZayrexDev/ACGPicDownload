package xyz.zcraft.acgpicdownload.gui.controllers;

import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PixivMenuPaneController extends PixivFetchPane {
    @javafx.fxml.FXML
    private MFXSlider maxCountSlider;
    @javafx.fxml.FXML
    private MFXSlider relatedDepthSlider;
    @javafx.fxml.FXML
    private MFXToggleButton fromFollowToggle;
    @javafx.fxml.FXML
    private MFXToggleButton fromRecToggle;
    @javafx.fxml.FXML
    private MFXToggleButton fromRecTagToggle;
    @javafx.fxml.FXML
    private MFXToggleButton fromRecUserToggle;
    @javafx.fxml.FXML
    private MFXToggleButton fromOtherToggle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("fas-angle-down"));
    }

    public static final Logger logger = Logger.getLogger(PixivMenuPaneController.class);

    @FXML
    @Override
    public void fetchBtnOnAction() {
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
                logger.info("Fetching menu with account " + selectedAccount.getName());

                Platform.runLater(() -> {
                    operationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                    subOperationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                });

                List<PixivArtwork> pixivArtworks = PixivFetchUtil.selectArtworks(
                        PixivFetchUtil.fetchMenu(
                                getCookie(),
                                ConfigManager.getConfig().getString("proxyHost"),
                                ConfigManager.getConfig().getInteger("proxyPort")
                        ),
                        (int) maxCountSlider.getValue(),
                        fromFollowToggle.isSelected(),
                        fromRecToggle.isSelected(),
                        fromRecTagToggle.isSelected(),
                        fromRecUserToggle.isSelected(),
                        fromOtherToggle.isSelected()
                );

                logger.info("Fetched " + pixivArtworks.size() + " artworks, getting related artworks for " + relatedDepthSlider.getValue() + " times");

                getRelated(pixivArtworks, (int) relatedDepthSlider.getValue(), getCookie(), subOperationLabel);

                Platform.runLater(() -> data.addAll(pixivArtworks));
                Notice.showSuccess(String.format(Objects.requireNonNull(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetched")), pixivArtworks.size()), gui.mainPane);

                logger.info("Fetch completed successfully within " + (System.currentTimeMillis() - start) + " ms");
            } catch (IOException e) {
                logger.error("Fetch menu failed", e);
                Main.logError(e);
                gui.showError(e);
            } finally {
                ft.stop();
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished((a) -> loadingPane.setVisible(false));
                ft.play();
            }
        }).start();
    }
}
