package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.BaseBuilder;
import DeCell.FPG.Frontend.Backend.Plugins.MultiPluginHandler;
import DeCell.FPG.Frontend.Backend.Renderable.PluginRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.UIElement;
import DeCell.FPG.Misc;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Mouse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Slider extends UIContainer<Slider, UIComponentAPI> {

    // so the slider will need:
    // a panel for rendering (aka the slider section)
    // a handle
    // onChange method that will give from 0-1

    private static final float handleHeight = 22;
    private static final float handleWidth = 18;
    private static final float halfHandleWidth = handleWidth / 2;
    private static final float sliderHeight = 16;

    // for the button to be centered
    private static final float handleTLPadding = (handleHeight - sliderHeight) / 2;

    public final MyPanel slider;
    public final MyButton handle;
    private final RenderableHandlerPlugin renderableHandlerPlugin;
    private float handleRelativeX = 0;

    public Slider(MyPanel sldr, MyButton hndl, RenderableHandlerPlugin renderPlugin) {
        super(sldr.u);
        this.slider = sldr;
        slider.addElement(this);
        slider.setOnHover(this::sliderOnHover);
        slider.setOnMouseDown(this::onSliderClick);

        handle = hndl;
        handle.inTL(0 - halfHandleWidth, handleTLPadding);

        renderableHandlerPlugin = renderPlugin;

    }


    public Slider addToSliderBackground(PluginRenderable backgroundRenderable) {
        renderableHandlerPlugin.addBelow(backgroundRenderable);
        return this;
    }

    public RenderableHandlerPlugin getRenderHandler() {
        return renderableHandlerPlugin;
    }

    //#region handle stuff
    @Override
    public void advance(float amount) {
        super.advance(amount);
        // java is so good!!!!! it allows protected fields and whatnot to be accessable in the same package!!!
        // very good languag design
        // ;kms
        if (!handle.isDragging())
            return;
        // the button is currently getting held down
        float parentX = slider.x();

        float mouseX = Mouse.getX() + halfHandleWidth;

        handleRelativeX = Mouse.getX() - slider.x();

        updateHandle();
    }

    public void updateHandle() {
        handleRelativeX = Misc.clamp(handleRelativeX, 0, slider.w());
        handle.inTL(handleRelativeX - halfHandleWidth, handleTLPadding);
        if (onChange != null)
            onChange.accept(this);
    }

    private Consumer<Slider> onChange;

    public Slider setOnChange(Consumer<Slider> onChange) {
        this.onChange = onChange;
        return this;
    }

    // from 0-1, 0 at the start of the slider and 1 at the end of the slider
    public float getSliderValue() {
        return Misc.clamp(handleRelativeX / slider.w(), 0, 1);
    }

    // zaza must be from 0-1
    public Slider setSliderValue(float zaza) {
        this.handleRelativeX = slider.w() * zaza;
        updateHandle();
        return this;
    }
    //#endregion

    //#region scroll
    private static final float scrollDelta = 2f;
    private static final float maxAccelerationMultiplier = 8f;
    private static final long accelerationThresholdMS = 100;

    private long lastScrollEventNanos = 0;
    private long lastScrollTimeMs = 0; // Tracks the absolute system time of the last scroll action

    private void sliderOnHover(MyPanel DISCARD_THIS_THING_BRUH) {
        if (!slider.rect().containsMouse())
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

        if (wheelDelta > 0) {
            handleRelativeX += currentScrollDelta;
        } else {
            handleRelativeX -= currentScrollDelta;
        }
        updateHandle();
    }

    //#endregion

    //#region panel

    private void onSliderClick(MyPanel __) {
        float parentX = slider.x();

        float mouseX = Mouse.getX() + halfHandleWidth;

        handleRelativeX = Mouse.getX() - slider.x();
        updateHandle();
    }

    //#endregion


    public static class Builder extends BaseBuilder<Builder> {

        //        private final float h;
        private final RenderableHandlerPlugin renderableHandlerPlugin = new RenderableHandlerPlugin();
        private final MyPanel slider;
        private final MyButton.Builder handle;

        public Builder(float w, MyPanel parent) {

            this.slider = new MyPanel.Builder(w, sliderHeight).
                    setPlugin(renderableHandlerPlugin).build(parent);

            this.handle = new MyButton.Builder("", handleWidth, handleHeight, slider);
        }

        @Override
        public Builder position(BiConsumer<UIElement<?, ?>, BaseBuilder<Builder>> zaza) {
            zaza.accept(slider, this);
            // the position of the handle will be handled by the slider
            // slide manhandling handle lmao
            return this;
        }

        public Slider build() {
            return new Slider(slider, handle.build(), renderableHandlerPlugin);
        }
    }
}
