package DeCell.FPG.Frontend.Backend.Components.Charlie;

import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import DeCell.FPG.Frontend.Backend.Plugins.MultiPluginHandler;
import DeCell.FPG.Frontend.Backend.Renderable.MonoColorRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.awt.*;

// heres charlie, his job is to make the behind of the panels darker
public class CharlieElement extends UIContainer<CharlieElement, CustomPanelAPI> {

    private final MonoColorRenderable background = new MonoColorRenderable(new Color(0x9A000000, true));
    private MultiPluginHandler plugin;

    public CharlieElement(MyPanel parent) {
        super(parent.u.createCustomPanel(parent.w(), parent.h(), new MultiPluginHandler()));
        parent.addComponent(this.u).inBL(0, 0);
        parent.addElement(this);

        this.plugin = ((MultiPluginHandler) this.u.getPlugin());
        this.plugin.add(new RenderableHandlerPlugin().addBelow(background));
        this.plugin.update(this.u);

        this.update();

        disable();
    }

    public CharlieElement(CustomPanelAPI customPanelAPI) {
        super(customPanelAPI);
    }

    public <T extends UIContainer<?, ?> & Openable> CharlieElement addOpenable(T o) {
        this.u.addComponent(o.u);
        this.addElement(o);
        o.setOnOpenClose(this::onOpenStateChanged);
        return this;
    }

    private void onOpenStateChanged(boolean isNowOpen) {
        if (isNowOpen) {
            enable();
        } else {
            disable();
        }
    }

    public CharlieElement enable() {
        this.background.render = true;
        return this;
    }

    public CharlieElement disable() {
        this.background.render = false;
        return this;
    }
}
