package xyz.zcraft.acgpicdownload.gui.base.argpanes;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import lombok.Getter;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.source.argument.Argument;
import xyz.zcraft.acgpicdownload.util.source.argument.StringArgument;

import java.io.IOException;

public class StringArgumentPane implements ArgumentPane<String> {
    protected StringArgument arg;
    @Getter
    @javafx.fxml.FXML
    protected MFXTextField argField;
    @Getter
    @javafx.fxml.FXML
    protected VBox pane;

    public StringArgumentPane() {
    }

    public static StringArgumentPane getInstance(StringArgument arg) throws IOException {
        FXMLLoader lo = new FXMLLoader(ResourceLoader.loadURL("fxml/StringArgumentPane.fxml"));
        lo.load();
        StringArgumentPane a = lo.getController();
        a.setArg(arg);
        a.getArgField().setFloatingText(arg.getName());
        return a;
    }

    protected StringArgument getArg() {
        return arg;
    }

    protected void setArg(StringArgument arg) {
        this.arg = arg;
    }

    protected void setPane(VBox pane) {
        this.pane = pane;
    }

    protected void setArgField(MFXTextField argField) {
        this.argField = argField;
    }

    @Override
    public String getValueString() {
        return argField.getText();
    }

    @Override
    public String getName() {
        return arg.getName();
    }

    @Override
    public Argument<String> getArgument() {
        arg.set(argField.getText());
        return arg;
    }

    public void update() {
        argField.setText(arg.getValue());
    }
}
