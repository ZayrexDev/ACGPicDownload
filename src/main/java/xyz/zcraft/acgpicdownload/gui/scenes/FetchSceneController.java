package xyz.zcraft.acgpicdownload.gui.scenes;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import xyz.zcraft.acgpicdownload.gui.GUI;
import xyz.zcraft.acgpicdownload.util.sourceutil.Source;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FetchSceneController implements Initializable {
    public MFXFilterComboBox<Source> sourcesComboBox;
    TranslateTransition tt = new TranslateTransition();
    private ObservableList<Source> sources;
    private GUI gui;
    @javafx.fxml.FXML
    private MFXButton sourceUpdateBtn;
    @javafx.fxml.FXML
    private HBox sourceUpdatePane;
    @javafx.fxml.FXML
    private AnchorPane mainPane;

    TransformableList<Source> sourceTransformableList;
    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void show() {
        tt.setFromY(mainPane.getHeight());
        tt.setToY(0);
        mainPane.setVisible(true);
        tt.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.setVisible(false);
        tt.setNode(mainPane);
        tt.setAutoReverse(true);
        tt.setRate(0.01);
        tt.setDuration(Duration.millis(5));
        tt.setInterpolator(Interpolator.EASE_BOTH);

        StringConverter<Source> converter = new StringConverter<>() {
            @Override
            public String toString(Source source) {
                return source == null ? null : source.getName();
            }

            @Override
            public Source fromString(String s) {
                return null;
            }
        };
        sourcesComboBox.setConverter(converter);

        sources = sourcesComboBox.getItems();
        sourceTransformableList = sourcesComboBox.getFilterList();

        sourcesComboBox.setFilterFunction(s -> source -> StringUtils.containsIgnoreCase(converter.toString(source), s));
    }

    @javafx.fxml.FXML
    public void sourceUpdateBtnOnAction() {
        updateSource();
    }

    private void updateSource() {
        sourcesComboBox.getItems().clear();

        FadeTransition ft = new FadeTransition();
        ft.setNode(sourceUpdatePane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.setRate(0.05);
        ft.setDuration(Duration.millis(5));

        sourceUpdatePane.setVisible(true);
        ft.play();

        new Thread(() -> {
            try {
                SourceManager.readConfig();
                sources.clear();
                sources.addAll(SourceManager.getSources());
                Thread.sleep(2000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            ft.play();
            ft.setOnFinished((e) -> sourceUpdatePane.setVisible(false));
        }).start();

        ft.setFromValue(1);
        ft.setToValue(0);
    }
}
