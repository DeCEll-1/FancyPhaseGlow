package DeCell.CPG.Frontend.Backend.Components.Gears;

import DeCell.CPG.Helpers.ElapsingInterval;
import DeCell.CPG.Misc;
import Kryz.Tweening.EasingFunctions;
import com.fs.starfarer.api.Global;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UpDownArrow {
    private final float intervalMin;
    private final float intervalMax;
    private final float initialDelay;

    public UpDownArrow(float _intervalMin, float _intervalMax, float _initialDelay) {
        this.intervalMin = _intervalMin;
        this.intervalMax = _intervalMax;
        this.initialDelay = _initialDelay;
        keyPressInterval = new ElapsingInterval(intervalMin, intervalMax);
    }

    private final ElapsingInterval keyPressInterval;
    private float holdTime = 0;

    private final List<Consumer<ButtonType>> listeners = new ArrayList<>();

    public void addUpDownListener(Consumer<ButtonType> listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    // TODO: make these use the new event system
    public void advance() {
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
        for (Consumer<ButtonType> listener : listeners) {
            listener.accept(upPressed ? ButtonType.UP : ButtonType.DOWN);
        }

    }


    public enum ButtonType {
        UP,
        DOWN
    }
}
