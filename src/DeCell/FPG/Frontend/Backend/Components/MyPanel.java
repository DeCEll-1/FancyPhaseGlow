package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.UIElement;
import DeCell.FPG.Frontend.Backend.Plugins.PanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

public class MyPanel extends UIContainer<MyPanel, CustomPanelAPI> {
    PanelPlugin plugin;

    //#region constructors
    public MyPanel(float w, float h, PanelPlugin p, MyPanel parent, boolean init) {
        super(parent.u.createCustomPanel(w, h, p));
        this.plugin = p;
        if (init) initPlugin();
        parent.addComponent(this);
    }

    public MyPanel(float w, float h, PanelPlugin p, CustomPanelAPI parent, boolean init) {
        super(parent.createCustomPanel(w, h, p));
        this.plugin = p;
        if (init) initPlugin();
        parent.addComponent(u);
    }

    public MyPanel(float w, float h, PanelPlugin p, UIPanelAPI parent, boolean init) {
        super(Global.getSettings().createCustom(w, h, p));
        this.plugin = p;
        if (init) initPlugin();
        parent.addComponent(u);
    }

    public MyPanel(float w, float h, PanelPlugin p, MyPanel parent) {
        this(w, h, p, parent, true);
    }

    public MyPanel(float w, float h, PanelPlugin p, CustomPanelAPI parent) {
        this(w, h, p, parent, true);
    }

    public MyPanel(float w, float h, PanelPlugin p, UIPanelAPI parent) {
        this(w, h, p, parent, true);
    }

    //#endregion

    public MyPanel(CustomPanelAPI underlying) {
        super(underlying);
    }

    public MyPanel initPlugin() {
        if (plugin != null)
            plugin.init(this.u);
        return this;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (plugin != null) {
            plugin.advance(amount);
            if (plugin.needsUpdate)
                this.update();
        }
    }

    public PositionAPI addUIElement(MyTooltip _0) {
        addElement(_0);
        return u.addUIElement(_0.u);
    }

    public PositionAPI addUIElement(TooltipMakerAPI _0) {
        return u.addUIElement(_0);
    }

    public PositionAPI addComponent(MyPanel _0) {
        addElement(_0);
        return u.addComponent(_0.u);
    }

    public PositionAPI addComponent(CustomPanelAPI _0) {
        return u.addComponent(_0);
    }

    @Override
    public MyPanel update() {
        if (plugin != null)
            plugin.update(u);
        return this;
    }

    public <T> T getPlugin() {
        return (T) this.plugin;
    }

    public static class Builder {
        private final float width;
        private final float height;

        private PanelPlugin plugin = null;
        private boolean init = true;

        public Builder(float width, float height) {
            this.width = width;
            this.height = height;
        }

        public Builder setPlugin(PanelPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder setInit(boolean init) {
            this.init = init;
            return this;
        }

        public MyPanel build(MyPanel parent) {
            return new MyPanel(width, height, plugin, parent, init);
        }

        public MyPanel build(CustomPanelAPI parent) {
            return new MyPanel(width, height, plugin, parent, init);
        }

        public MyPanel build(UIPanelAPI parent) {
            return new MyPanel(width, height, plugin, parent, init);
        }
    }


}
