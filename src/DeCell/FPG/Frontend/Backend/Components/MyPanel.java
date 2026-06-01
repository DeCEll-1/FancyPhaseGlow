package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.AUIElement;
import DeCell.FPG.Frontend.Backend.Plugins.CPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

public class MyPanel extends AUIElement<MyPanel, CustomPanelAPI> {
    CPanelPlugin plugin;

    public MyPanel(float w, float h, CPanelPlugin p, MyPanel parent) {
        super(parent.u.createCustomPanel(w, h, p));
        this.plugin = p;
        initPlugin();
        parent.addComponent(u);
    }

    public MyPanel(float w, float h, CPanelPlugin p, CustomPanelAPI parent) {
        super(parent.createCustomPanel(w, h, p));
        this.plugin = p;
        initPlugin();
        parent.addComponent(u);
    }

    public MyPanel(float w, float h, CPanelPlugin p, UIPanelAPI parent) {
        super(Global.getSettings().createCustom(w, h, p));
        this.plugin = p;
        initPlugin();
        parent.addComponent(u);
    }

    public MyPanel(float w, float h, CPanelPlugin p) {
        super(Global.getSettings().createCustom(w, h, p));
        this.plugin = p;
        initPlugin();
    }

    public MyPanel(CustomPanelAPI underlying) {
        super(underlying);
    }

    private void initPlugin() {
        if (plugin != null)
            plugin.init(this.u);
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
        return u.addUIElement(_0.u);
    }

    public PositionAPI addUIElement(TooltipMakerAPI _0) {
        return u.addUIElement(_0);
    }

    public PositionAPI addComponent(MyPanel _0) {
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


}
