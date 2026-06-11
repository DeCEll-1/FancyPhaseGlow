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
    //#region constants
    public final static float sliderWidth = 160f;
    public final static float previewWidth = 60f;

    public final static float xPadding = 2f;
    public final static float yPadding = 2f;

    public final static float containerWidth = sliderWidth + xPadding + previewWidth;

    public final static float containerHeight = (3 * Slider.sliderHeight) + (2 * xPadding);
    public final static float containerHeightAlpha = (4 * Slider.sliderHeight) + (3 * xPadding);

    public final static float previewHeight = containerHeight;
    public final static float previewHeightAlpha = containerHeightAlpha;

    public final static Color defaultColor = new Color(0xFFFFFF);
    //#endregion

    private final MonoColorRenderable previewRenderer;
    private final Slider redSlider;
    private final Slider greenSlider;
    private final Slider blueSlider;
    private final Slider alphaSlider;

    public ColorPicker(MyPanel container, MyPanel colorPreview, Slider rSlider, Slider gSlider, Slider bSlider, Slider aSlider) {
        super(container.u);
        container.addElement(this);
        this.parent = container;
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
        updateSlidersColors(); // we do it twice because red and green only sees stuff thats updated before them so
        // they dont update properly, doing it twice fixes that
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


        public Builder withAlpha() {
            hasAlpha = true;
            return this;
        }

        public ColorPicker build(MyPanel parent, Color color) {

            MyPanel container = new MyPanel.Builder(
                    containerWidth,
                    hasAlpha ? containerHeightAlpha : containerHeight
            ).build(parent);

            MyPanel colorPreview = new MyPanel.Builder(
                    previewWidth,
                    hasAlpha ? previewHeightAlpha : previewHeight
            ).setPlugin(new RenderableHandlerPlugin()).build(container)
                    .inTR(xPadding, yPadding);

            Slider redSlider = new Slider.Builder(sliderWidth, container)
                    .position((item, builder) -> item.inTL(xPadding, yPadding))
                    .build().setSliderValue(color.getRed() / 255f)
                    .addToSliderBackground(new QuadColorRenderable(color));

            Slider greenSlider = new Slider.Builder(sliderWidth, container).setSibling(redSlider)
                    .position((item, builder) -> item.belowMid(builder.getSibling().u, yPadding))
                    .build().setSliderValue(color.getBlue() / 255f)
                    .addToSliderBackground(new QuadColorRenderable(color));

            Slider blueSlider = new Slider.Builder(sliderWidth, container).setSibling(greenSlider)
                    .position((item, builder) -> item.belowMid(builder.getSibling().u, yPadding))
                    .build().setSliderValue(color.getGreen() / 255f)
                    .addToSliderBackground(new QuadColorRenderable(color));

            if (hasAlpha) {
                alphaSlider = new Slider.Builder(sliderWidth, container).setSibling(blueSlider)
                        .position((item, builder) -> item.belowMid(builder.getSibling().u, yPadding))
                        .build().setSliderValue(color.getAlpha() / 255f)
                        .addToSliderBackground(new QuadColorRenderable(color));
            }

            return new ColorPicker(container, colorPreview, redSlider, greenSlider, blueSlider, alphaSlider);
        }

        public ColorPicker build(MyPanel parent, int col) {
            return this.build(parent, new Color(col, hasAlpha));
        }

        public ColorPicker build(MyPanel parent) {
            return this.build(parent, defaultColor);
        }

    }
}