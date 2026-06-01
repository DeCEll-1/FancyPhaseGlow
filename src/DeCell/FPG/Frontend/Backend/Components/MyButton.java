package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.AUIElement;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.function.Consumer;

public class MyButton extends AUIElement<MyButton, ButtonAPI> {
    public MyButton(ButtonAPI underlying) {
        super(underlying);
    }

    public MyButton(String text, Object data, float width, float height, float pad, MyTooltip parent) {
        super(parent.u.addButton(text, data, width, height, pad));
    }

    public MyButton(String text, Object data, Color base, Color bg,
                    float width, float height, float pad, MyTooltip parent) {
        super(parent.u.addButton(text, data, base, bg, width, height, pad));
    }

    public MyButton(String text, Object data, Color base, Color bg,
                    Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        super(parent.u.addButton(text, data, base, bg, align, style, width, height, pad));
    }

    public MyButton(String text, Object data, Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        this(text, data, Misc.getButtonTextColor(), Misc.getDarkPlayerColor(),
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
}
