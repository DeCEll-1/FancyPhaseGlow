package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.FancyPhaseGlow;
import DeCell.FPG.Frontend.Backend.UIElement;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class MyButton extends UIElement<MyButton, ButtonAPI> {

    public MyButton(String text, float width, float height, float pad, MyTooltip parent) {
        super(parent.addButton(text, null, width, height, pad));
        parent.addElement(this);
        this.parent = parent;
    }

    public MyButton(String text, Color base, Color bg,
                    float width, float height, float pad, MyTooltip parent) {
        super(parent.addButton(text, null, base, bg, width, height, pad));
        parent.addElement(this);
        this.parent = parent;
    }

    public MyButton(String text, Color base, Color bg,
                    Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        super(parent.addButton(text, null, base, bg, align, style, width, height, pad));
        parent.addElement(this);
        this.parent = parent;
    }

    public MyButton(String text, Alignment align, CutStyle style,
                    float width, float height, float pad, MyTooltip parent) {
        this(text, Misc.getButtonTextColor(), Misc.getDarkPlayerColor(),
                align, style, width, height, pad, parent);
    }

    protected Consumer<MyButton> onMouseDown;
    protected Consumer<MyButton> onMouseUp;
    protected boolean wasClickedLastFrame = false;
    protected boolean isDragging = false;
    protected MyTooltip parent;

    public MyTooltip getParent() {
        return parent;
    }

    public boolean wasClickedLastFrame() {
        return wasClickedLastFrame;
    }

    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        super.processInput(events);

        boolean isMouseOver = this.rect().containsMouse();
        boolean isLeftMouseDown = Mouse.isButtonDown(0);

        if (isLeftMouseDown && isMouseOver && !wasClickedLastFrame) {
            isDragging = true;
            if (onMouseDown != null)
                onMouseDown.accept(this);
        }

        if (!isLeftMouseDown && isDragging) {
            isDragging = false;
            if (onMouseUp != null)
                onMouseUp.accept(this);
        }

        wasClickedLastFrame = isLeftMouseDown;
    }

    public MyButton setOnMouseDown(Consumer<MyButton> onMouseDown) {
        this.onMouseDown = onMouseDown;
        return this;
    }

    public MyButton setOnMouseUp(Consumer<MyButton> onMouseUp) {
        this.onMouseUp = onMouseUp;
        return this;
    }

    public MyButton setCustomData(Object data) {
        u.setCustomData(data);
        return this;
    }

    public static class Builder {
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

        public Builder(String text, float width, float height) {
            this.text = text;
            this.w = width;
            this.h = height;
        }

        public Builder(String text) {
            this(text, 0, 0);
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

        public Builder position(Consumer<UIElement<?, ?>> zaza) {
            zaza.accept(parent);
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
