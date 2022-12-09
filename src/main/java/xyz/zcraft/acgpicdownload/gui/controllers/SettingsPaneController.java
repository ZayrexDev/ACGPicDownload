package xyz.zcraft.acgpicdownload.gui.controllers;

import com.alibaba.fastjson2.JSONObject;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;
import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.gui.ConfigManager;
import xyz.zcraft.acgpicdownload.gui.Notice;
import xyz.zcraft.acgpicdownload.gui.base.MyPane;
import xyz.zcraft.acgpicdownload.util.ResourceBundleUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsPaneController extends MyPane {
    @javafx.fxml.FXML
    private AnchorPane mainPane;
    @javafx.fxml.FXML
    private MFXButton backBtn;
    @javafx.fxml.FXML
    private MFXTextField proxyField;
    @javafx.fxml.FXML
    private MFXSlider aniSpeedSlider;
    @FXML
    private MFXComboBox<Locale> languageCombo;
    private String proxyHost;
    private int proxyPort;

    @javafx.fxml.FXML
    private MFXRadioButton fetchPLCOCopy;
    @javafx.fxml.FXML
    private ToggleGroup fetchPaneLClickOperation;
    @javafx.fxml.FXML
    private MFXRadioButton fetchPLCOOpen;
    @FXML
    private MFXRadioButton bgFromDefault;
    @FXML
    private ToggleGroup bgFrom;
    @FXML
    private MFXRadioButton bgFromFolder;
    @FXML
    private MFXRadioButton bgTransparent;
    @FXML
    private MFXTextField bgFolderField;
    @FXML
    private MFXButton bgChooseFolderBtn;

    public void show() {
        super.show();
    }

    public void hide() {
        super.hide();
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

    @FXML
    private void bgChooseFolder() {
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("...");
        File showDialog = fc.showDialog(gui.mainStage);
        if (showDialog != null) {
            bgFolderField.setText(showDialog.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        proxyField.textProperty().addListener(this::verifyProxy);

        languageCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale l) {
                if (l == null) return null;
                if (l.equals(Locale.CHINA)) return "中文";
                if (l.equals(Locale.ENGLISH)) return "English";
                return l.getDisplayLanguage();
            }

            @Override
            public Locale fromString(String s) {
                return Locale.forLanguageTag(s);
            }
        });

        languageCombo.getItems().clear();
        languageCombo.getItems().addAll(Locale.CHINA, Locale.ENGLISH);

        restoreConfig();

        backBtn.setText("");
        backBtn.setGraphic(new MFXFontIcon("mfx-angle-down"));
        bgChooseFolderBtn.setText("");
        bgChooseFolderBtn.setGraphic(new MFXFontIcon("mfx-folder"));
    }

    private void verifyProxy(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        proxyHost = null;
        proxyPort = 0;
        System.getProperties().put("proxySet", "false");
        if (!newValue.isEmpty()) {
            String[] str = newValue.split(":");
            if (str.length == 2) {
                proxyHost = str[0];
                try {
                    proxyPort = Integer.parseInt(str[1]);
                } catch (NumberFormatException ignored) {
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
            proxyField.setTextFill(MFXTextField.DEFAULT_TEXT_COLOR);
        }
    }

    public void saveConfig() {
        JSONObject obj = ConfigManager.getConfig();

        if (proxyPort != 0 && proxyHost != null) {
            obj.put("proxyHost", proxyHost);
            obj.put("proxyPort", proxyPort);
        } else {
            obj.remove("proxyHost");
            obj.remove("proxyPort");
        }
        obj.put("aniSpeed", aniSpeedSlider.getValue());
        obj.put("fetchPLCOCopy", fetchPLCOCopy.isSelected());
        obj.put("lang", languageCombo.getSelectedItem().toLanguageTag());

        if (bgFromFolder.isSelected()) {
            obj.put("bg", bgFolderField.getText());
        } else if (bgTransparent.isSelected()) {
            obj.put("bg", "transparent");
        } else {
            obj.remove("bg");
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
        fetchPLCOCopy.setSelected(ConfigManager.getConfig().getBooleanValue("fetchPLCOCopy"));
        fetchPLCOOpen.setSelected(!ConfigManager.getConfig().getBooleanValue("fetchPLCOCopy"));

        String lang = ConfigManager.getConfig().getString("lang");
        Locale locale;
        if (lang != null) locale = Locale.forLanguageTag(lang);
        else locale = Locale.getDefault();
        if (languageCombo.getItems().contains(locale)) languageCombo.getSelectionModel().selectItem(locale);
        else languageCombo.getSelectionModel().selectFirst();

        if (json.containsKey("bg")) {
            if (json.getString("bg").equals("transparent")) {
                bgTransparent.setSelected(true);
            } else {
                bgFolderField.setText(json.getString("bg"));
                bgFromFolder.setSelected(true);
            }
        }
    }
}
