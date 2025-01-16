package xyz.zcraft.acgpicdownload.gui.base.argpanes;

import io.github.palexdev.materialfx.controls.MFXSpinner;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.source.argument.Argument;
import xyz.zcraft.acgpicdownload.util.source.argument.IntegerArgument;

import java.io.IOException;

@Setter
@Getter
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
