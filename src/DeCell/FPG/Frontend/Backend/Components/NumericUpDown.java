package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.FancyPhaseGlow;
import DeCell.FPG.Frontend.Backend.BaseBuilder;
import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Helpers.ElapsingInterval;
import DeCell.FPG.Misc;
import Kryz.Tweening.EasingFunctions;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Keyboard;

import java.util.List;

import static DeCell.FPG.FancyPhaseGlow.Patterns.decimalWithMaxDecimalPlaces;
import static DeCell.FPG.Frontend.Backend.DataPair.pair;

public class NumericUpDown extends UIContainer<NumericUpDown, UIComponentAPI> {

    private final MyTextBox tb;
    private final MyButton up;
    private final MyButton down;

    public NumericUpDown(MyPanel _container, MyTextBox _tb, MyButton _up, MyButton _down) {
        super(_container.u);
        _container.addElement(this);
        this.parent = _container;
        this.tb = _tb;
        this.up = _up;
        this.down = _down;

        // TODO: handle null stirng properly
        tb.setValidationRegex(FancyPhaseGlow.Patterns.DECIMAL_ONLY);
        this.setValue(0);

        up.setOnMouseDown(this::buttonUpdate).addToInternalData(pair("type", ButtonType.UP));
        down.setOnMouseDown(this::buttonUpdate).addToInternalData(pair("type", ButtonType.DOWN));

        setAmountOfDecimalPlaces(2);
    }

    private void buttonUpdate(MyButton button) {
        ButtonType type = button.getFromInternal("type");

        switch (type) {
            case UP -> up();
            case DOWN -> down();
        }
    }

    private void up() {
        this.setValue(this.getValue() + stepSize);
    }

    private void down() {
        this.setValue(this.getValue() - stepSize);
    }


    private static final float initialDelay = 0.4f;
    private static final float intervalMin = 0;
    private static final float intervalMax = 0.4f;
    private final ElapsingInterval keyPressInterval = new ElapsingInterval(intervalMin, intervalMax);
    private float holdTime = 0;

    @Override
    public void processInput(List<InputEventAPI> events) {
        if (!tb.u.hasFocus()) {
            keyPressInterval.setElapsed(0);
            return;
        }

        boolean upPressed = Keyboard.isKeyDown(Keyboard.KEY_UP);
        boolean downPressed = Keyboard.isKeyDown(Keyboard.KEY_DOWN);
        if (upPressed || downPressed) {
            float deltaTime = Global.getCombatEngine().getElapsedInLastFrame();

            if (holdTime == 0)
                handleArrowKeys(upPressed);
            holdTime += deltaTime;

            if (holdTime < initialDelay)
                return;


            keyPressInterval.advance(deltaTime);

            keyPressInterval.setInterval(intervalMin,
                    intervalMax - Misc.clamp(EasingFunctions.Linear(holdTime * 0.1f), 0, intervalMax - 0.05f));

            if (keyPressInterval.isElapsed()) {
                handleArrowKeys(upPressed);
            }
        } else {
            keyPressInterval.setInterval(intervalMin, intervalMax);
            keyPressInterval.setElapsed(0);
            holdTime = 0;
        }
    }

    private void handleArrowKeys(boolean upPressed) {
        if (upPressed) up();
        else down();
    }

    private float stepSize = 1f;

    public NumericUpDown setStepSize(float _stepSize) {
        this.stepSize = _stepSize;
        return this;
    }

    public MyTextBox getTextBox() {
        return this.tb;
    }

    private int amountOfDecimalPlaces;

    public NumericUpDown setAmountOfDecimalPlaces(int zaza) {
        this.amountOfDecimalPlaces = zaza;
        this.tb.setValidationRegex(decimalWithMaxDecimalPlaces(amountOfDecimalPlaces));
        return this;
    }

    public NumericUpDown setValue(double val) {
        String text = String.format("%." + amountOfDecimalPlaces + "f", val);
        this.tb.setText(text);
        return this;
    }

    public double getValue() {
        String text = this.tb.getText();
        if (text == null || text.isEmpty())
            text = "0";
        return Double.parseDouble(text);
    }

    private enum ButtonType {
        UP,
        DOWN
    }

    // this wwill be a regular ass winforms numeric up down, it must have 2 buttons for up down
    // properties such as:
    // min, max, step size, end floating points, value changed event, if the button is getting hold keep increase/decreasing it
    // custom regex match, shift arrow keys bla bla allat jazz
    // requred components sis a text box and a button
    public static class Builder extends BaseBuilder<Builder> {

        //#region constants
        public final static float xPadding = 2f;
        public final static float yPadding = 2f;

        public final static float buttonWidth = 16;
        public final static float buttonHeight = MyTextBox.defaultHeight / 2f;


        //#endregion

        // width and height includes the buttons
        public NumericUpDown build(float w, MyPanel parent) {
            final float tbWidth = w - (xPadding + buttonWidth);
            final float tbHeight = MyTextBox.defaultHeight;

            MyPanel container = new MyPanel.Builder(w, tbHeight).build(parent);

            MyTextBox tb = new MyTextBox.Builder(tbWidth, tbHeight, container)
                    .position((item, builder) -> item.inTL(0, 0)).build();

            MyButton upButton = new MyButton.Builder("", buttonWidth, buttonHeight, container)
                    .position((i, b) -> i.inTR(0, 0))
                    .setStyle(Alignment.MID, CutStyle.TOP)
                    .build();

            MyButton downButton = new MyButton.Builder("", buttonWidth, buttonHeight, container)
                    .position((i, b) -> i.inBR(0, 0))
                    .setStyle(Alignment.MID, CutStyle.BOTTOM)
                    .build();

            return new NumericUpDown(container, tb, upButton, downButton);
        }

    }
}
