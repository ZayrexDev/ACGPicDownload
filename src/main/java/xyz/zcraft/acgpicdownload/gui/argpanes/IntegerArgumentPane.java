package xyz.zcraft.acgpicdownload.gui.argpanes;

import io.github.palexdev.materialfx.controls.MFXSpinner;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.IntegerArgument;

import java.io.IOException;

public class IntegerArgumentPane implements ArgumentPane<Integer> {
    HBox pane;
    IntegerArgument arg;
    @javafx.fxml.FXML
    private Label argNameLabel;
    @javafx.fxml.FXML
    private MFXSpinner<Integer> argSpinner;

    public IntegerArgumentPane() {
    }

    // TODO Finish this
    public static IntegerArgumentPane getInstance(IntegerArgument arg) throws IOException {
        FXMLLoader lo = new FXMLLoader(ResourceLoader.loadURL("fxml/StringArgumentPane.fxml"));
        lo.load();
        IntegerArgumentPane a = lo.getController();
        a.setArg(arg);
//        a.getArgSpinner().getSpinnerModel().
        return a;
    }

    public HBox getPane() {
        return pane;
    }

    public void setPane(HBox pane) {
        this.pane = pane;
    }

    public Label getArgNameLabel() {
        return argNameLabel;
    }

    public void setArgNameLabel(Label argNameLabel) {
        this.argNameLabel = argNameLabel;
    }

    public MFXSpinner<Integer> getArgSpinner() {
        return argSpinner;
    }

    public void setArgSpinner(MFXSpinner<Integer> argSpinner) {
        this.argSpinner = argSpinner;
    }

    public IntegerArgument getArg() {
        return arg;
    }

    public void setArg(IntegerArgument arg) {
        this.arg = arg;
    }

    @Override
    public Integer getValueString() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Argument<Integer> getArgument() {
        return null;
    }

    @Override
    public void update() {

    }
}
