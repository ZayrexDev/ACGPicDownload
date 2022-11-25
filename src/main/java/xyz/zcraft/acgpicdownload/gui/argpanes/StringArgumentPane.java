package xyz.zcraft.acgpicdownload.gui.argpanes;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.StringArgument;

import java.io.IOException;

public class StringArgumentPane implements ArgumentPane<String> {
    protected StringArgument arg;
    @javafx.fxml.FXML
    protected MFXTextField argField;
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

    public VBox getPane() {
        return pane;
    }

    protected void setPane(VBox pane) {
        this.pane = pane;
    }

    public MFXTextField getArgField() {
        return argField;
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
