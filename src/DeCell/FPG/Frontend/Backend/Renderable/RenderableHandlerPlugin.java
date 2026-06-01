package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Plugins.APluginRenderable;
import DeCell.FPG.Frontend.Backend.Plugins.CPanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.util.ArrayList;
import java.util.List;

public class RenderableHandlerPlugin extends CPanelPlugin {
    private List<APluginRenderable> renderBelows = new ArrayList<>();
    private List<APluginRenderable> renders = new ArrayList<>();

    public RenderableHandlerPlugin() {
    }

    public RenderableHandlerPlugin addBelow(APluginRenderable _0) {
        renderBelows.add(_0);
        return this;
    }

    public RenderableHandlerPlugin addAbove(APluginRenderable _0) {
        renders.add(_0);
        return this;
    }


    @Override
    public void init(CustomPanelAPI parent) {
        for (APluginRenderable $_ : renderBelows) {
            $_.init(parent);
        }

        for (APluginRenderable $_ : renders) {
            $_.init(parent);
        }
    }

    @Override
    public void update(CustomPanelAPI parent) {
        for (APluginRenderable $_ : renderBelows) {
            $_.update(parent);
        }

        for (APluginRenderable $_ : renders) {
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
        for (APluginRenderable $_ : renderBelows) {
            $_.renderBelow(alphaMult);
        }
    }

    @Override
    public void render(float alphaMult) {
        if (updateNeeded())
            return;

        for (APluginRenderable $_ : renders) {
            $_.render(alphaMult);
        }
    }

    private boolean updateNeeded() {
        return renderBelows.stream().anyMatch(APluginRenderable::needsUpdate) || renders.stream().anyMatch(APluginRenderable::needsUpdate);
    }
}
