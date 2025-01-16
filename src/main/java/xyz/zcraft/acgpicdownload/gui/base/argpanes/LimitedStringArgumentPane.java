package xyz.zcraft.acgpicdownload.gui.base.argpanes;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import lombok.Getter;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.source.argument.Argument;
import xyz.zcraft.acgpicdownload.util.source.argument.LimitedStringArgument;

import java.io.IOException;

@Getter
public class LimitedStringArgumentPane implements ArgumentPane<String> {
    protected LimitedStringArgument arg;
    @javafx.fxml.FXML
    protected VBox pane;
    @javafx.fxml.FXML
    protected MFXComboBox<String> argCombo;

    public LimitedStringArgumentPane() {
    }

    public static LimitedStringArgumentPane getInstance(LimitedStringArgument arg) throws IOException {
        FXMLLoader lo = new FXMLLoader(ResourceLoader.loadURL("fxml/LimitedStringArgumentPane.fxml"));
        lo.load();
        LimitedStringArgumentPane a = lo.getController();
        a.setArg(arg);
        a.getArgCombo().setFloatingText(arg.getName());
        a.getArgCombo().getItems().addAll(arg.getValidValues());
        a.getArgCombo().getSelectionModel().selectItem(arg.getValue());
        return a;
    }

    protected void setArg(LimitedStringArgument arg) {
        this.arg = arg;
    }

    protected void setPane(VBox pane) {
        this.pane = pane;
    }

    protected void setArgCombo(MFXComboBox<String> argCombo) {
        this.argCombo = argCombo;
    }

    @Override
    public String getValueString() {
        return argCombo.getValue();
    }

    @Override
    public String getName() {
        return arg.getName();
    }

    @Override
    public Argument<String> getArgument() {
        if (argCombo.getValue() == null) {
            arg.set(arg.getValue());
        } else {
            arg.set(argCombo.getValue());
        }

        return arg;
    }

    public void update() {
        argCombo.getSelectionModel().selectItem(arg.getValue());
    }
}
