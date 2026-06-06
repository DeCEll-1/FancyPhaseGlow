package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Plugins.PluginRenderable;
import DeCell.FPG.Frontend.Backend.Plugins.PanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.util.ArrayList;
import java.util.List;

public class RenderableHandlerPlugin extends PanelPlugin {
    private List<PluginRenderable> renderBelows = new ArrayList<>();
    private List<PluginRenderable> renders = new ArrayList<>();

    public RenderableHandlerPlugin() {
    }

    public RenderableHandlerPlugin addBelow(PluginRenderable _0) {
        renderBelows.add(_0);
        return this;
    }

    public RenderableHandlerPlugin addAbove(PluginRenderable _0) {
        renders.add(_0);
        return this;
    }


    @Override
    public void init(CustomPanelAPI parent) {
        for (PluginRenderable $_ : renderBelows) {
            $_.init(parent);
        }

        for (PluginRenderable $_ : renders) {
            $_.init(parent);
        }
    }

    @Override
    public void update(CustomPanelAPI parent) {
        for (PluginRenderable $_ : renderBelows) {
            $_.update(parent);
        }

        for (PluginRenderable $_ : renders) {
            $_.update(parent);
        }
        this.needsUpdate = false;
    }

    @Override
    public void advance(float amount) {
        if (updateNeeded())
            this.needsUpdate = true;
    }

    @Override
    public void renderBelow(float alphaMult) {
        if (updateNeeded())
            return;

        super.renderBelow(alphaMult);
        for (PluginRenderable $_ : renderBelows) {
            $_.renderBelow(alphaMult);
        }
    }

    @Override
    public void render(float alphaMult) {
        if (updateNeeded())
            return;

        for (PluginRenderable $_ : renders) {
            $_.render(alphaMult);
        }
    }

    private boolean updateNeeded() {
        return renderBelows.stream().anyMatch(PluginRenderable::needsUpdate) || renders.stream().anyMatch(PluginRenderable::needsUpdate);
    }
}
