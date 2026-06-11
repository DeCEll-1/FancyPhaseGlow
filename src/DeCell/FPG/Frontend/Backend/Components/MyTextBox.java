package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.BaseBuilder;
import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.UIElement;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static DeCell.FPG.Reflections.invokeMethod;

public class MyTextBox extends UIContainer<MyTextBox, TextFieldAPI> {
    //#region constants
    public static final float defaultHeight = 28f;
    public static final String defaultFont = "graphics/fonts/insignia21LTaa.fnt";
    //#endregion

    public MyTextBox(float w, float pad, MyTooltip _parent) {
        super(_parent.addTextField(w, pad));
        _parent.addElement(this);
        this.parent = _parent;
    }

    public MyTextBox(float w, String font, float pad, MyTooltip _parent) {
        super(_parent.addTextField(w, font, pad));
        _parent.addElement(this);
        this.parent = _parent;
    }

    public MyTextBox(float w, float h, String font, float pad, MyTooltip _parent) {
        super(_parent.addTextField(w, h, font, pad));
        _parent.addElement(this);
        this.parent = _parent;
    }

    protected boolean hadFocusLastFrame = false;
    protected String textLastFrame = "";

    @Override
    public void processInput(List<InputEventAPI> events) {
        super.processInput(events);

        boolean currentlyHavesFocus = this.u.hasFocus();

        if (currentlyHavesFocus && !hadFocusLastFrame)
            // we captured focus
            if (onFocusEnter != null)
                onFocusEnter.accept(this);

        if (!currentlyHavesFocus && hadFocusLastFrame)
            // we lost focus
            if (onFocusExit != null)
                onFocusExit.accept(this);

        hadFocusLastFrame = currentlyHavesFocus;

        String currentText = this.u.getText();

        if (!Objects.equals(currentText, textLastFrame)) {
            if (validationPattern != null && currentText != null) {
                Matcher matcher = validationPattern.matcher(currentText);

                if (!matcher.matches()) {
                    this.u.setText(textLastFrame);

                    // TODO: if we manage to get sound setting for text fields update this
                    Global.getSoundPlayer().playUISound("ui_typer_buzz", 1, 2);
                    return;
                }
            }

            if (onTextChange != null)
                onTextChange.accept(this);
        }

        textLastFrame = currentText;

    }

    private Consumer<MyTextBox> onFocusEnter;
    private Consumer<MyTextBox> onFocusExit;
    private Consumer<MyTextBox> onTextChange;
    protected Pattern validationPattern = null;

    public MyTextBox setOnFocusEnter(Consumer<MyTextBox> onFocusEnter) {
        this.onFocusEnter = onFocusEnter;
        return this;
    }

    public MyTextBox setOnFocusExit(Consumer<MyTextBox> onFocusExit) {
        this.onFocusExit = onFocusExit;
        return this;
    }

    public MyTextBox setOnTextChange(Consumer<MyTextBox> onTextChange) {
        this.onTextChange = onTextChange;
        return this;
    }

    public MyTextBox setValidationRegex(String regex) {
        if (regex == null || regex.isEmpty())
            this.validationPattern = null;
        else
            this.validationPattern = Pattern.compile(regex);
        return this;
    }

    public MyTextBox setValidationRegex(Pattern regex) {
        this.validationPattern = regex;
        return this;
    }


    //#region tb functions


    public boolean isMinimalMode() {
        return (boolean) invokeMethod("isMinimalMode", this.u);
    }

    public MyTextBox setMinimalMode(boolean isMinimal) {
        invokeMethod("setMinimalMode", this.u, new Class[]{boolean.class}, isMinimal);
        return this;
    }

    public MyTextBox setPad(float pad) {
        this.u.setPad(pad);
        return this;
    }

    public MyTextBox setMidAlignment() {
        this.u.setMidAlignment();
        return this;
    }

    public MyTextBox setColor(Color color) {
        this.u.setColor(color);
        return this;
    }

    public MyTextBox setBgColor(Color bgColor) {
        this.u.setBgColor(bgColor);
        return this;
    }

    public String getText() {
        return this.u.getText();
    }

    public MyTextBox setText(String string) {
        this.u.setText(string);
        return this;
    }

    public MyTextBox setLimitByStringWidth(boolean limitByStringWidth) {
        this.u.setLimitByStringWidth(limitByStringWidth);
        return this;
    }

    public MyTextBox setMaxChars(int maxChars) {
        this.u.setMaxChars(maxChars);
        return this;
    }

    public MyTextBox deleteAll() {
        this.u.deleteAll();
        return this;
    }

    public MyTextBox deleteAll(boolean withSound) {
        this.u.deleteAll(withSound);
        return this;
    }

    public MyTextBox deleteLastWord() {
        this.u.deleteLastWord();
        return this;
    }

    public MyTextBox grabFocus() {
        this.u.grabFocus();
        return this;
    }

    public MyTextBox grabFocus(boolean playSound) {
        this.u.grabFocus(playSound);
        return this;
    }

    public MyTextBox setUndoOnEscape(boolean undoOnEscape) {
        this.u.setUndoOnEscape(undoOnEscape);
        return this;
    }

    public MyTextBox setHandleCtrlV(boolean handleCtrlV) {
        this.u.setHandleCtrlV(handleCtrlV);
        return this;
    }

    public MyTextBox setBorderColor(Color borderColor) {
        this.u.setBorderColor(borderColor);
        return this;
    }

    public MyTextBox setVerticalCursor(boolean verticalCursor) {
        this.u.setVerticalCursor(verticalCursor);
        return this;
    }

    public MyTextBox hideCursor() {
        this.u.hideCursor();
        return this;
    }

    public MyTextBox showCursor() {
        this.u.showCursor();
        return this;
    }

    //#endregion

    // the textbox will need:
    // on text change
    public static class Builder extends BaseBuilder<Builder> {


        private final MyTooltip parent;
        private final float w;
        private final float h;
        private float pad = 0;
        private String font = defaultFont;

        // Width is required, so we pass it into the constructor
        public Builder(float w, float h, MyPanel parent) {
            this.parent = new MyTooltip.Builder(w, h, parent).build();
            this.h = h;
            this.w = w;
        }

        public Builder(float w, MyPanel parent) {
            this(w, defaultHeight, parent);
        }

        public Builder setPad(float pad) {
            this.pad = pad;
            return this;
        }

        public Builder setFont(String font) {
            this.font = font;
            return this;
        }

        @Override
        public Builder position(BiConsumer<UIElement<?, ?>, BaseBuilder<?>> zaza) {
            zaza.accept(parent, this);
            return this;
        }

        public MyTextBox build() {
            return new MyTextBox(w, h, font, pad, parent);
        }

    }
}
