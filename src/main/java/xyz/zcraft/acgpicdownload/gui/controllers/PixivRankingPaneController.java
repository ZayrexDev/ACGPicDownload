package xyz.zcraft.acgpicdownload.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;

import com.alibaba.fastjson2.JSONObject;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.PixivFetchPane;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;
import xyz.zcraft.acgpicdownload.util.pixivutils.From;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivArtwork;
import xyz.zcraft.acgpicdownload.util.pixivutils.PixivFetchUtil;

public class PixivRankingPaneController extends PixivFetchPane {
    private static final String[][] MAJORS = {
            {"daily", "daily_r18"}, {"weekly", "weekly_r18"},
            {"monthly"}, {"rookie"}, {"original"},
            {"daily_ai", "daily_r18_ai"}, {"male", "male_r18"}, {"female", "female_r18"}
    };
    private final LinkedList<String> minors = new LinkedList<>();
    public MFXComboBox<String> majorCombo;
    public MFXComboBox<String> minorCombo;
    public MFXToggleButton resToggle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        cookieHelpBtn.setText("");
        cookieHelpBtn.setGraphic(new MFXFontIcon("mfx-info-circle"));

        cookieField.textProperty().addListener((observableValue, s, t1) -> ConfigManager.getTempConfig().put("cookie", t1));
        cookieField.setText(Objects.requireNonNullElse(ConfigManager.getConfig().getJSONObject("pixiv"), new JSONObject()).getString("cookie"));

        majorCombo.getItems().addAll(
                ResourceBundleUtil.getString("gui.pixiv.ranking.daily"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.weekly"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.monthly"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.rookie"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.original"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.daily_ai"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.male"),
                ResourceBundleUtil.getString("gui.pixiv.ranking.female")
        );

        majorCombo.selectedIndexProperty().addListener((observableValue, number, t1) -> {
            int i = t1.intValue();
            if(i == 0 || i == 1 || i == 5 || i == 6 || i == 7){
                resToggle.setDisable(false);
            }else{
                resToggle.setSelected(false);
                resToggle.setDisable(true);
            }
            minorCombo.getItems().clear();
            minors.clear();
            minors.add("");
            minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.all"));
            if (i == 0 || i == 1 || i == 2 || i == 3) {
                minors.add("illust");
                minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.illust"));
            }
            if (i == 0 || i == 1) {
                minors.add("ugoira");
                minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.ugoira"));
            }
            if (i == 0 || i == 1 || i == 2 || i == 3) {
                minors.add("manga");
                minorCombo.getItems().add(ResourceBundleUtil.getString("gui.pixiv.ranking.minor.manga"));
            }

            minorCombo.selectFirst();
        });

        majorCombo.selectFirst();
        minorCombo.selectFirst();
    }

    @Override
    public void fetchBtnOnAction() {
        var major = MAJORS[majorCombo.getSelectedIndex()][resToggle.isSelected() ? 1 : 0];
        var minor = minors.get(minorCombo.getSelectedIndex());

        loadingPane.setVisible(true);
        operationLabel.setText(ResourceBundleUtil.getString(""));
        ft.stop();
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setOnFinished(null);
        ft.play();

        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    operationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                    subOperationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetchMain"));
                });

                LinkedList<PixivArtwork> pixivArtworks = new LinkedList<>();

                LinkedList<String> ids = PixivFetchUtil.getRankingIDs(
                        major,
                        minor,
                        cookieField.getText(),
                        ConfigManager.getConfig().getString("proxyHost"),
                        ConfigManager.getConfig().getInteger("proxyPort")
                );

                int[] i = {0,0};
                for (; i[0] < ids.size(); i[0]++) {
                    Platform.runLater(()->subOperationLabel.setText(ResourceBundleUtil.getString("gui.pixiv.ranking.notice.getting") + " " + (i[0] + 1) + "/" + ids.size() + " | " + ResourceBundleUtil.getString("gui.pixiv.ranking.retries") + " " + (i[1]+1)));
                    try{
                        PixivArtwork a =  PixivFetchUtil.getArtwork(
                                        ids.get(i[0]),
                                        cookieField.getText(),
                                        ConfigManager.getConfig().getString("proxyHost"),
                                        ConfigManager.getConfig().getInteger("proxyPort")
                                );
                        a.setFrom(From.Ranking);
                        String rankingInfo = majorCombo.getSelectedItem() + (resToggle.isSelected()?"*":"") + "-" + minorCombo.getSelectedItem() + "#" + (i[0] + 1);
                        a.setRanking(rankingInfo);
                        pixivArtworks.add(a);
                        i[1] = 0;
                    }catch(Exception e){
                        i[1]++;
                        if(i[1] <= 5)
                            i[0]--;
                    }
                }

                Platform.runLater(() -> data.addAll(pixivArtworks));

                Notice.showSuccess(String.format(
                        Objects.requireNonNull(ResourceBundleUtil.getString("gui.pixiv.menu.notice.fetched")),
                        pixivArtworks.size()), gui.mainPane);
            } catch (IOException e) {
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
