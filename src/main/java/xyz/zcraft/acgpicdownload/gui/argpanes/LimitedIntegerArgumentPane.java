package xyz.zcraft.acgpicdownload.gui.argpanes;

import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.Argument;
import xyz.zcraft.acgpicdownload.util.sourceutil.argument.LimitedIntegerArgument;

import java.io.IOException;

public class LimitedIntegerArgumentPane implements ArgumentPane<Integer> {
    LimitedIntegerArgument arg;

    @javafx.fxml.FXML
    private Label argNameLabel;
    @javafx.fxml.FXML
    private MFXSlider argSlider;
    @javafx.fxml.FXML
    private HBox pane;

    public LimitedIntegerArgumentPane() {
    }

    public static LimitedIntegerArgumentPane getInstance(LimitedIntegerArgument arg) throws IOException {
        FXMLLoader lo = new FXMLLoader(ResourceLoader.loadURL("fxml/LimitedIntegerArgumentPane.fxml"));
        lo.load();
        LimitedIntegerArgumentPane a = lo.getController();
        a.setArg(arg);
        a.getArgNameLabel().setText(arg.getName());
        if (arg.getLimit().isHasMax()) {
            a.getArgSlider().setMax(arg.getLimit().getMaxValue());
        }
        if (arg.getLimit().isHasMin()) {
            a.getArgSlider().setMin(arg.getLimit().getMinValue());
        }
        if (arg.getLimit().isHasStep()) {
            a.getArgSlider().setTickUnit(arg.getLimit().getStep());
        }
        a.getArgSlider().setValue(arg.getValue());
        return a;
    }

    public LimitedIntegerArgument getArg() {
        return arg;
    }

    public void setArg(LimitedIntegerArgument arg) {
        this.arg = arg;
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

    public MFXSlider getArgSlider() {
        return argSlider;
    }

    public void setArgSlider(MFXSlider argSlider) {
        this.argSlider = argSlider;
    }

    @Override
    public Integer getValueString() {
        return (int) argSlider.getValue();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Argument<Integer> getValue() {
        arg.set((int) argSlider.getValue());
        return arg;
    }
}
