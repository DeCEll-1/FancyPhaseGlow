package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.UIElement;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.function.Consumer;

public class MyButton extends UIElement<MyButton, ButtonAPI> {
    public MyButton(ButtonAPI underlying) {
        super(underlying);
    }

    public MyButton(String text, float width, float height, float pad, MyTooltip parent) {
        super(parent.addButton(text, null, width, height, pad));
        parent.addElement(this);
    }

    public MyButton(String text, Color base, Color bg,
                    float width, float height, float pad, MyTooltip parent) {
        super(parent.addButton(text, null, base, bg, width, height, pad));
        parent.addElement(this);
    }

    public MyButton(String text, Color base, Color bg,
                    Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        super(parent.addButton(text, null, base, bg, align, style, width, height, pad));
        parent.addElement(this);
    }

    public MyButton(String text, Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        this(text, Misc.getButtonTextColor(), Misc.getDarkPlayerColor(),
                align, style, width, height, pad, parent);
    }

    protected Consumer<ButtonAPI> onClick;
    protected boolean wasChecked = false;

    @Override
    public void advance(float amount) {
        if (u == null) return;

        boolean currentlyChecked = u.isChecked();
        if (currentlyChecked && !wasChecked) {
            if (onClick != null) {
                onClick.accept(u);
            }
            u.setChecked(false);
        }
        wasChecked = currentlyChecked;
    }


    public MyButton setOnClick(Consumer<ButtonAPI> onClick) {
        this.onClick = onClick;
        return this;
    }

    public MyButton setCustomData(Object data) {
        u.setCustomData(data);
        return this;
    }
}
