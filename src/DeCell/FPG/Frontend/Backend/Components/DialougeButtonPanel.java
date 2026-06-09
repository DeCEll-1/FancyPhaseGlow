package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.UIElement;
import DeCell.FPG.Frontend.Backend.Components.Charlie.CharlieElement;
import DeCell.FPG.Frontend.Backend.Components.Charlie.Openable;
import DeCell.FPG.Frontend.Backend.Components.Charlie.OpenableListener;
import DeCell.FPG.Frontend.Backend.Plugins.LambdaUIPanelPlugin;
import DeCell.FPG.Frontend.Backend.Plugins.MultiPluginHandler;
import DeCell.FPG.Frontend.Backend.Renderable.BorderRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.FPG.JavaSlop.TriConsumer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import org.lwjgl.input.Keyboard;

import java.util.Dictionary;
import java.util.List;
import java.util.function.Consumer;

import static DeCell.FPG.Helpers.UI.getCurrentTab;

public class DialougeButtonPanel extends UIContainer<DialougeButtonPanel, CustomPanelAPI> implements Openable {

    private MyButton button;
    private MyPanel container;
    private boolean isOpen = false;

    public DialougeButtonPanel(float w, float h, MyButton _1, CharlieElement parent) {
        super(parent.u.createCustomPanel(w, h, null));
        parent.addOpenable(this);
        parent.addElement(this);

        this.button = _1;
        this.button.setOnMouseDown(this::click);
    }

    public DialougeButtonPanel(float w, float h, MyButton _1, MyPanel parent) {
        super(parent.u.createCustomPanel(w, h, null));
        parent.addComponent(this.u);
        parent.addElement(this);

        this.button = _1;
        this.button.setOnMouseDown(this::click);
    }


    private void createContainer() {
        this.container = new MyPanel(this.w(), this.h(), new MultiPluginHandler() // main window
                .add(new RenderableHandlerPlugin()
                        .addBelow(
                                // TODO: make these more modifyable
                                new BorderRenderable(Global.getSettings().getSprite("fpg", "border2"), 32)
                                        .setPadding(-8).setRenderInside(true))
                ).add(new LambdaUIPanelPlugin()
                        .onProcessInput(e ->
                                e.forEach($_ -> {
                                    if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                                        this.click();
                                    $_.consume();
                                })
                        )
                ), this.u).addTo(UIElements).inBL(0, 0);


        MyTooltip exitTooltip = new MyTooltip(24, 24, false, container).addTo(UIElements).inTR(26, 16);
        new MyButton("X", 24, 24, 0, exitTooltip).
                setOnMouseDown(this::click).addTo(UIElements);
    }

    public DialougeButtonPanel(CustomPanelAPI underlying) {
        super(underlying);
    }

    protected TriConsumer<MyPanel, DialougeButtonPanel, List<UIElement<?, ?>>> onUIOpen;
    protected Consumer<Dictionary<String, Object>> onUIClose;

    private void click(MyButton b) {
        click();
    }

    private void click() {

        if (isOpen) {
            this.u.removeComponent(container.u);
            if (onUIClose != null)
                onUIClose.accept(internalData);
        } else {
            createContainer();
            if (onUIOpen != null)
                onUIOpen.accept(container, this, UIElements);
        }
        isOpen = !isOpen;
        if (listener != null)
            listener.onOpenStateChanged(isOpen);
    }

    public DialougeButtonPanel setOnUIOpen(TriConsumer<MyPanel, DialougeButtonPanel, List<UIElement<?, ?>>> onClick) {
        this.onUIOpen = onClick;
        return this;
    }

    public DialougeButtonPanel setOnUIClose(Consumer<Dictionary<String, Object>> onClick) {
        this.onUIClose = onClick;
        return this;
    }

    private OpenableListener listener;

    @Override
    public void setOnOpenClose(OpenableListener _listener) {
        this.listener = _listener;
    }

    public static class Builder {
        private final float w;
        private final float h;
        private final MyButton button;
        private boolean charlie = false;

        public Builder(float width, float height, MyButton button) {
            this.w = width;
            this.h = height;
            this.button = button;
        }

        public Builder withCharlie() {
            charlie = true;
            return this;
        }

        public DialougeButtonPanel popup(List<UIElement<?, ?>> UIElements) {
            UIPanelAPI currentTab = (UIPanelAPI) getCurrentTab();
            MyPanel popupContainer = new MyPanel.Builder(currentTab.getPosition().getWidth(), currentTab.getPosition().getHeight()).build(currentTab).addTo(UIElements);
            if (charlie) {
                CharlieElement charlie = new CharlieElement(popupContainer);
                return new DialougeButtonPanel(w, h, button, charlie).inMid();
            }

            return new DialougeButtonPanel(w, h, button, popupContainer);
        }
    }
}
