package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.Components.Combobox.MyCombobox;
import DeCell.FPG.Frontend.Backend.Plugins.MultiPluginHandler;
import DeCell.FPG.Frontend.Backend.Renderable.BackgroundRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.UIElement;
import DeCell.FPG.Misc;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Mouse;

import java.util.function.Consumer;

public class Slider extends UIContainer<MyCombobox, UIComponentAPI> {

    // so the slider will need:
    // a panel for rendering (aka the slider section)
    // a handle
    // onChange method that will give from 0-1

    private static final float handleHeight = 22;
    private static final float handleWidth = 12;
    private static final float sliderHeight = 16;

    // for the button to be centered
    private final float handleTLPadding;
    public final MyPanel slider;
    public final MyButton handle;

    public Slider(MyPanel sldr, MyButton hndl) {
        super(sldr.u);
        this.slider = sldr;
        slider.addElement(this);
        this.handle = hndl;

        handleTLPadding = (handleHeight - sliderHeight) / 2;

        this.handle.inTL(0, handleTLPadding);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        // java is so good!!!!! it allows protected fields and whatnot to be accessable in the same package!!!
        // very good languag design
        // ;kms
        if (!handle.isDragging())
            return;
        // the button is currently getting held down
        float handleX = handle.x();
        float parentX = slider.x();

        float mouseX = Mouse.getX();

        float relativeX = -((parentX - mouseX) + (handleWidth / 2));
        relativeX = Misc.clamp(relativeX, 0, slider.w() - handleWidth);

        handle.inTL(relativeX, handleTLPadding);
    }

    public static class Builder {

        private final float w;
        //        private final float h;
        private final RenderableHandlerPlugin renderableHandlerPlugin = new RenderableHandlerPlugin();
        private final MyPanel slider;
        private final MyButton.Builder handle;

        public Builder(float w, MyPanel parent) {
            this.w = w;

            this.slider = new MyPanel.Builder(w, sliderHeight).
                    setPlugin(new MultiPluginHandler().add(
                            renderableHandlerPlugin
                    )).build(parent);

            this.handle = new MyButton.Builder("", handleWidth, handleHeight, slider);
        }

        public Builder position(Consumer<UIElement<?, ?>> zaza) {
            zaza.accept(slider);
            // the position of the handle will be handled by the slider
            // slide manhandling handle lmao
            return this;
        }

        public Builder addToSliderBackground(BackgroundRenderable backgroundRenderable) {
            renderableHandlerPlugin.addBelow(backgroundRenderable);
            return this;
        }

        public Slider build() {
            return new Slider(slider, handle.build());
        }
    }
}
