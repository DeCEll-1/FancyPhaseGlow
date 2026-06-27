package DeCell.CPG.Frontend.Backend.Components.Gears;

import DeCell.CPG.Frontend.Backend.UIElement;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Scroll {
    private static final float scrollDelta = 2f;
    private static final float maxAccelerationMultiplier = 8f;
    private static final long accelerationThresholdMS = 100;

    private long lastScrollEventNanos = 0;
    private long lastScrollTimeMs = 0; // Tracks the absolute system time of the last scroll action

    private final List<Consumer<Float>> listeners = new ArrayList<>();

    public void addScrollListener(Consumer<Float> listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    // TODO: make these use the new event system
    public void onHover(UIElement<?, ?> el) {
        if (!el.rect().containsMouse())
            return;

        int wheelDelta = Mouse.getEventDWheel();
        if (wheelDelta == 0)
            return;

        long currentEventNanos = Mouse.getEventNanoseconds();
        if (currentEventNanos == lastScrollEventNanos)
            return;
        lastScrollEventNanos = currentEventNanos;

        long currentTimeMs = System.currentTimeMillis();
        long timeSinceLastScroll = currentTimeMs - lastScrollTimeMs;
        lastScrollTimeMs = currentTimeMs;

        float currentScrollDelta = scrollDelta;

        if (timeSinceLastScroll < accelerationThresholdMS && timeSinceLastScroll > 0) {
            float speedFactor = (float) accelerationThresholdMS / timeSinceLastScroll;

            float multiplier = Math.min(speedFactor, maxAccelerationMultiplier);

            currentScrollDelta *= multiplier;
        }

        if (wheelDelta < 0)
            currentScrollDelta = -currentScrollDelta;

        for (Consumer<Float> listener : listeners) {
            listener.accept(currentScrollDelta);
        }
    }

}
