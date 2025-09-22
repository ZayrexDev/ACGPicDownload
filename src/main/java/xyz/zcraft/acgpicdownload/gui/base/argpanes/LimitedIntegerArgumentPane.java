package xyz.zcraft.acgpicdownload.gui.base.argpanes;

import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import xyz.zcraft.acgpicdownload.gui.ResourceLoader;
import xyz.zcraft.acgpicdownload.util.source.argument.Argument;
import xyz.zcraft.acgpicdownload.util.source.argument.LimitedIntegerArgument;

import java.io.IOException;

@Setter
@Getter
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

    @Override
    public Integer getValueString() {
        return (int) argSlider.getValue();
    }

    @Override
    public String getName() {
        return arg.getName();
    }

    @Override
    public Argument<Integer> getArgument() {
        arg.set((int) argSlider.getValue());
        return arg;
    }

    public void update() {
        argSlider.setValue(arg.getValue());
    }
}
