package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.FancyPhaseGlow;
import DeCell.FPG.Frontend.Backend.BaseBuilder;
import DeCell.FPG.Frontend.Backend.UIElement;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MyButton extends UIElement<MyButton, ButtonAPI> {

    public MyButton(String text, float width, float height, float pad, MyTooltip _parent) {
        super(_parent.addButton(text, null, width, height, pad));
        _parent.addElement(this);
        this.parent = _parent;
    }

    public MyButton(String text, Color base, Color bg,
                    float width, float height, float pad, MyTooltip _parent) {
        super(_parent.addButton(text, null, base, bg, width, height, pad));
        _parent.addElement(this);
        this.parent = _parent;
    }

    public MyButton(String text, Color base, Color bg,
                    Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip _parent) {
        super(_parent.addButton(text, null, base, bg, align, style, width, height, pad));
        _parent.addElement(this);
        this.parent = _parent;
    }

    public MyButton(String text, Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        this(text, Misc.getButtonTextColor(), Misc.getDarkPlayerColor(),
                align, style, width, height, pad, parent);
    }

    public boolean wasClickedLastFrame() {
        return wasClickedLastFrame;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

//#region button functions

    public MyButton setShortcut(int key, boolean putLast) {
        u.setShortcut(key, putLast);
        return this;
    }

    public MyButton setEnabled(boolean enabled) {
        u.setEnabled(enabled);
        return this;
    }

    public MyButton setButtonPressedSound(String buttonPressedSound) {
        u.setButtonPressedSound(buttonPressedSound);
        return this;
    }

    public MyButton setMouseOverSound(String mouseOverSound) {
        u.setMouseOverSound(mouseOverSound);
        return this;
    }

    public MyButton setButtonDisabledPressedSound(String buttonDisabledPressedSound) {
        u.setButtonDisabledPressedSound(buttonDisabledPressedSound);
        return this;
    }

    public MyButton setChecked(boolean checked) {
        u.setChecked(checked);
        return this;
    }

    public MyButton highlight() {
        u.highlight();
        return this;
    }

    public MyButton unhighlight() {
        u.unhighlight();
        return this;
    }

    public MyButton setHighlightBrightness(float highlightBrightness) {
        u.setHighlightBrightness(highlightBrightness);
        return this;
    }

    public MyButton setQuickMode(boolean quickMode) {
        u.setQuickMode(quickMode);
        return this;
    }

    public MyButton setClickable(boolean clickable) {
        u.setClickable(clickable);
        return this;
    }

    public MyButton setGlowBrightness(float glowBrightness) {
        u.setGlowBrightness(glowBrightness);
        return this;
    }

    public MyButton setText(String text) {
        u.setText(text);
        return this;
    }

    public MyButton setSkipPlayingPressedSoundOnce(boolean skipPlayingPressedSoundOnce) {
        u.setSkipPlayingPressedSoundOnce(skipPlayingPressedSoundOnce);
        return this;
    }

    public MyButton setHighlightBounceDown(boolean b) {
        u.setHighlightBounceDown(b);
        return this;
    }

    public MyButton setShowTooltipWhileInactive(boolean showTooltipWhileInactive) {
        u.setShowTooltipWhileInactive(showTooltipWhileInactive);
        return this;
    }

    public MyButton setRightClicksOkWhenDisabled(boolean rightClicksOkWhenDisabled) {
        u.setRightClicksOkWhenDisabled(rightClicksOkWhenDisabled);
        return this;
    }

    public MyButton setFlashBrightness(float flashBrightness) {
        u.setFlashBrightness(flashBrightness);
        return this;
    }

    public MyButton flash(boolean withSound, float in, float out) {
        u.flash(withSound, in, out);
        return this;
    }

    public MyButton flash(boolean withSound) {
        u.flash(withSound);
        return this;
    }

    public MyButton flash() {
        u.flash();
        return this;
    }

    public MyButton setPerformActionWhenDisabled(boolean performActionWhenDisabled) {
        u.setPerformActionWhenDisabled(performActionWhenDisabled);
        return this;
    }

    public MyButton setCustomData(Object customData) {
        u.setCustomData(customData);
        return this;
    }
    //#endregion

    public static class Builder extends BaseBuilder<Builder> {
        private final String text;
        private float w;
        private float h;
        private MyTooltip parent = null;
        private float pad = 0f;
        private Color baseColor = null;
        private Color bgColor = null;
        private Alignment alignment = null;
        private CutStyle cutStyle = null;

        public Builder(String text, float width, float height, MyTooltip parent) {
            this.text = text;
            this.w = width;
            this.h = height;
            this.parent = parent;
        }

        public Builder(String text, float width, float height, MyPanel parent) {
            this.text = text;
            this.w = width;
            this.h = height;
            this.parent = new MyTooltip.Builder(width, height, parent).build();
        }

        public Builder(String text, MyTooltip parent) {
            this(text, 0, 0, parent);
        }

        public Builder(String text, MyPanel parent) {
            this(text, 0, 0, parent);
        }

        public Builder setParent(MyPanel panel) {
            this.parent = new MyTooltip.Builder(this.w, this.h, panel).build();
            return this;
        }

        public boolean havesParent() {
            return parent != null;
        }

        public Builder setParent(MyTooltip panel) {
            this.parent = panel;
            return this;
        }

        @Override
        public Builder position(BiConsumer<UIElement<?, ?>, BaseBuilder<?>> zaza) {
            zaza.accept(parent, this);
            return this;
        }


        public Builder modifyParent(Consumer<MyTooltip> zaza) { // needed for when you input panel and need to update the tooltips properties
            zaza.accept(parent);
            return this;
        }

        public Builder setPad(float pad) {
            this.pad = pad;
            return this;
        }

        public Builder setShape(float w, float h) {
            this.w = w;
            this.h = h;
            return this;
        }

        public Builder setColors(Color baseColor, Color bgColor) {
            this.baseColor = baseColor;
            this.bgColor = bgColor;
            return this;
        }

        public Builder setStyle(Alignment alignment, CutStyle cutStyle) {
            this.alignment = alignment;
            this.cutStyle = cutStyle;
            return this;
        }

        public MyButton build() {
            if (baseColor != null && bgColor != null && alignment != null && cutStyle != null) {
                return new MyButton(text, baseColor, bgColor, alignment, cutStyle, w, h, pad, parent);
            }

            if (baseColor != null && bgColor != null) {
                return new MyButton(text, baseColor, bgColor, w, h, pad, parent);
            }

            if (alignment != null && cutStyle != null) {
                return new MyButton(text, alignment, cutStyle, w, h, pad, parent);
            }

            return new MyButton(text, w, h, pad, parent);
        }

    }
}
