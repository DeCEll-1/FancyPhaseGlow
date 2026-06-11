package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.BaseBuilder;
import DeCell.FPG.Frontend.Backend.DataPair;
import DeCell.FPG.Frontend.Backend.Renderable.MonoColorRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.QuadColorRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.FPG.Frontend.Backend.UIContainer;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.awt.*;
import java.util.function.Consumer;

import static DeCell.FPG.Frontend.Backend.DataPair.pair;

public class ColorPicker extends UIContainer<ColorPicker, UIComponentAPI> {
    private final MonoColorRenderable previewRenderer;
    private final Slider redSlider;
    private final Slider greenSlider;
    private final Slider blueSlider;
    private final Slider alphaSlider;

    public ColorPicker(MyPanel container, MyPanel colorPreview, Slider rSlider, Slider gSlider, Slider bSlider, Slider aSlider) {
        super(container.u);
        container.addElement(this);
        previewRenderer = new MonoColorRenderable(new Color(0x000000));
        colorPreview.<RenderableHandlerPlugin>getPlugin().addBelow(previewRenderer);

        DataPair<MonoColorRenderable> rendererPair = pair("preview_renderer", previewRenderer);

        this.redSlider = rSlider.addToInternalData(rendererPair, pair("type", SliderType.RED)).setOnChange(this::sliderUpdate);
        this.greenSlider = gSlider.addToInternalData(rendererPair, pair("type", SliderType.GREEN)).setOnChange(this::sliderUpdate);
        this.blueSlider = bSlider.addToInternalData(rendererPair, pair("type", SliderType.BLUE)).setOnChange(this::sliderUpdate);
        if (aSlider == null)
            this.alphaSlider = null;
        else {
            this.alphaSlider = aSlider.addToInternalData(rendererPair, pair("type", SliderType.ALPHA)).setOnChange(this::sliderUpdate);
        }

        updateSlidersColors();
    }


    private void sliderUpdate(Slider slider) {
        updateSlidersColors();
    }

    private void updateSlidersColors() {
        updateSliderBackground(redSlider);
        updateSliderBackground(greenSlider);
        updateSliderBackground(blueSlider);
        if (alphaSlider != null)
            updateSliderBackground(alphaSlider);
        if (onColorChange != null)
            onColorChange.accept(this);
    }

    private Consumer<ColorPicker> onColorChange;

    public ColorPicker setOnColorChange(Consumer<ColorPicker> zaza) {
        this.onColorChange = zaza;
        return this;
    }

    public Color getColor() {
        return previewRenderer.c;
    }

    private void updateSliderBackground(Slider slider) {
        MonoColorRenderable rendererPair = slider.getFromInternal("preview_renderer");
        SliderType type = slider.getFromInternal("type");
        QuadColorRenderable sliderBackground = (QuadColorRenderable) slider.getRenderHandler().getRenderBelows().get(0);

        Color currentPreviewColor = previewRenderer.c;
        int currentRed = currentPreviewColor.getRed();
        int currentGreen = currentPreviewColor.getGreen();
        int currentBlue = currentPreviewColor.getBlue();
        int currentAlpha = currentPreviewColor.getAlpha();

        int newValue = (int) (slider.getSliderValue() * 255);

        Color leftColor;
        Color rightColor;

        switch (type) {
            case RED -> {
                previewRenderer.c = new Color(newValue, currentGreen, currentBlue, currentAlpha);
                leftColor = new Color(0, currentGreen, currentBlue);
                rightColor = new Color(255, currentGreen, currentBlue);
            }
            case GREEN -> {
                previewRenderer.c = new Color(currentRed, newValue, currentBlue, currentAlpha);
                leftColor = new Color(currentRed, 0, currentBlue);
                rightColor = new Color(currentRed, 255, currentBlue);
            }
            case BLUE -> {
                previewRenderer.c = new Color(currentRed, currentGreen, newValue, currentAlpha);
                leftColor = new Color(currentRed, currentGreen, 0);
                rightColor = new Color(currentRed, currentGreen, 255);
            }
            case ALPHA -> {
                previewRenderer.c = new Color(currentRed, currentGreen, currentBlue, newValue);
                leftColor = new Color(currentRed, currentGreen, currentBlue, 0);
                rightColor = new Color(currentRed, currentGreen, currentBlue, 255);
            }
            default -> {
                return; // Handle potential ALPHA types here later if needed!
            }
        }

        previewRenderer.updateColors();

        sliderBackground.CTL(leftColor);
        sliderBackground.CBL(leftColor);
        sliderBackground.CTR(rightColor);
        sliderBackground.CBR(rightColor);
        sliderBackground.updateColors();
    }

    // this will need:
    // 3 sliders (will be created on builder, not taken by outside)
    // a panel to contain it all
    // a panel to render the output

    private enum SliderType {
        RED,
        GREEN,
        BLUE,
        ALPHA
    }

    public static class Builder extends BaseBuilder<Builder> {
        private Slider alphaSlider;
        private boolean hasAlpha = false;

        // --- Layout Constants ---
        private final static float SLIDER_WIDTH = 160f;
        private final static float SLIDER_WIDTH = 160f;
        private final static float PREVIEW_WIDTH = 40f;

        // Padding configurations
        private final static float Y_PADDING = 2f;
        private final static float X_PADDING = 2f;

        // Container dimensions with alpha
        private final static float containerWidthWithAlpha = 206; // 2 padding from sides
        private final static float containerHeightWithAlpha = 78; // 4 sliders * 16 + 20 for 4 padding between sliders

        // Container dimensions without alpha
        private final static float CONTAINER_WIDTH = SLIDER_WIDTH + X_PADDING + PREVIEW_WIDTH + X_PADDING;
        private final static float CONTAINER_HEIGHT = 56;

        public Builder withAlpha() {
            hasAlpha = true;
            return this;
        }

        public ColorPicker build(MyPanel parent) {

            MyPanel container = new MyPanel.Builder(
                    hasAlpha ? containerWidthWithAlpha : CONTAINER_WIDTH,
                    hasAlpha ? containerHeightWithAlpha : CONTAINER_HEIGHT
            ).build(parent);

            MyPanel colorPreview = new MyPanel.Builder(
                    PREVIEW_WIDTH,
                    hasAlpha ? containerHeightWithAlpha - Y_PADDING : CONTAINER_HEIGHT - Y_PADDING
            ).setPlugin(new RenderableHandlerPlugin()).build(container)
                    .inTR(X_PADDING, Y_PADDING);

            Slider redSlider = new Slider.Builder(SLIDER_WIDTH, container)
                    .position((item, builder) -> item.inTL(X_PADDING, Y_PADDING))
                    .build()
                    .addToSliderBackground(new QuadColorRenderable(new Color(0)));

            Slider greenSlider = new Slider.Builder(SLIDER_WIDTH, container).addSibling(redSlider)
                    .position((item, builder) -> item.belowMid(builder.getSibling().u, Y_PADDING))
                    .build()
                    .addToSliderBackground(new QuadColorRenderable(new Color(0)));

            Slider blueSlider = new Slider.Builder(SLIDER_WIDTH, container).addSibling(greenSlider)
                    .position((item, builder) -> item.belowMid(builder.getSibling().u, Y_PADDING))
                    .build()
                    .addToSliderBackground(new QuadColorRenderable(new Color(0)));

            if (hasAlpha) {
                alphaSlider = new Slider.Builder(SLIDER_WIDTH, container).addSibling(blueSlider)
                        .position((item, builder) -> item.belowMid(builder.getSibling().u, Y_PADDING))
                        .build().setSliderValue(1)
                        .addToSliderBackground(new QuadColorRenderable(new Color(0)));
            }

            return new ColorPicker(container, colorPreview, redSlider, greenSlider, blueSlider, alphaSlider);
        }
    }
}