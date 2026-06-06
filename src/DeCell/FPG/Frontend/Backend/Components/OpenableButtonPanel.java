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
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import org.lwjgl.input.Keyboard;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

public class OpenableButtonPanel extends UIContainer<OpenableButtonPanel, CustomPanelAPI> implements Openable {

    private MyButton button;
    private MyPanel container;
    private boolean isOpen = false;
    private Dictionary<String, Object> internalData = new Hashtable<>();

    public OpenableButtonPanel(float w, float h, MyButton _1, CharlieElement parent) {
        super(parent.u.createCustomPanel(w, h, null));
        parent.addOpenable(this);

        this.button = _1;
        this.button.setOnClick(this::click);
    }

    public OpenableButtonPanel(float w, float h, MyButton _1, CustomPanelAPI parent) {
        super(parent.createCustomPanel(w, h, null));
        parent.addComponent(this.u);

        this.button = _1;
        this.button.setOnClick(this::click);
    }

    public OpenableButtonPanel(float w, float h, MyButton _1, MyPanel parent) {
        super(parent.u.createCustomPanel(w, h, null));
        parent.addComponent(this.u);

        this.button = _1;
        this.button.setOnClick(this::click);
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
                setOnClick(this::click).addTo(UIElements);
    }

    public OpenableButtonPanel(CustomPanelAPI underlying) {
        super(underlying);
    }

    protected TriConsumer<MyPanel, Dictionary<String, Object>, List<UIElement<?, ?>>> onUIOpen;
    protected Consumer<Dictionary<String, Object>> onUIClose;

    private void click(ButtonAPI b) {
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
                onUIOpen.accept(container, internalData, UIElements);
        }
        isOpen = !isOpen;
        if (listener != null)
            listener.onOpenStateChanged(isOpen);
    }


    public OpenableButtonPanel setOnUIOpen(TriConsumer<MyPanel, Dictionary<String, Object>, List<UIElement<?, ?>>> onClick) {
        this.onUIOpen = onClick;
        return this;
    }

    public OpenableButtonPanel setOnUIClose(Consumer<Dictionary<String, Object>> onClick) {
        this.onUIClose = onClick;
        return this;
    }


    private OpenableListener listener;

    @Override
    public void setOnOpenClose(OpenableListener _listener) {
        this.listener = _listener;
    }
}
