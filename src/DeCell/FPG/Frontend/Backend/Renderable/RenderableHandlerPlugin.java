package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Plugins.PanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.util.ArrayList;
import java.util.List;

public class RenderableHandlerPlugin extends PanelPlugin {
    private List<PluginRenderable> renderBelows = new ArrayList<>();
    private List<PluginRenderable> renderAboves = new ArrayList<>();

    public RenderableHandlerPlugin() {
    }

    public RenderableHandlerPlugin addBelow(PluginRenderable _0) {
        renderBelows.add(_0);
        return this;
    }

    public List<PluginRenderable> getRenderBelows() {
        return renderBelows;
    }

    public RenderableHandlerPlugin addAbove(PluginRenderable _0) {
        renderAboves.add(_0);
        return this;
    }

    public List<PluginRenderable> getRenderAboves() {
        return renderAboves;
    }


    @Override
    public void init(CustomPanelAPI parent) {
        for (PluginRenderable $_ : renderBelows) {
            $_.init(parent);
        }

        for (PluginRenderable $_ : renderAboves) {
            $_.init(parent);
        }
    }

    @Override
    public void update(CustomPanelAPI parent) {
        for (PluginRenderable $_ : renderBelows) {
            $_.update(parent);
        }

        for (PluginRenderable $_ : renderAboves) {
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
            if ($_.render)
                $_.renderBelow(alphaMult);
        }
    }

    @Override
    public void render(float alphaMult) {
        if (updateNeeded())
            return;

        for (PluginRenderable $_ : renderAboves) {
            if ($_.render)
                $_.render(alphaMult);
        }
    }

    private boolean updateNeeded() {
        return renderBelows.stream().anyMatch(PluginRenderable::needsUpdate) || renderAboves.stream().anyMatch(PluginRenderable::needsUpdate);
    }
}
