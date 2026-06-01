package DeCell.FPG.Frontend.Backend.Plugins;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import java.util.ArrayList;
import java.util.List;

public class MultiPluginHandler extends CPanelPlugin {
    private List<CPanelPlugin> plugins = new ArrayList<>();

    public MultiPluginHandler() {
    }

    public MultiPluginHandler add(CPanelPlugin _0) {
        plugins.add(_0);
        return this;
    }

    // --- CustomPanelAPI specific methods ---
    @Override
    public void init(CustomPanelAPI parent) {
        for (CPanelPlugin plugin : plugins) {
            plugin.init(parent);
        }
    }

    @Override
    public void update(CustomPanelAPI parent) {
        for (CPanelPlugin plugin : plugins) {
            plugin.update(parent);
        }

        this.needsUpdate = false;
    }

    // --- Overridden methods from CustomUIPanelPlugin ---

    @Override
    public void positionChanged(PositionAPI position) {
        for (CPanelPlugin plugin : plugins) {
            plugin.positionChanged(position);
        }
    }

    @Override
    public void renderBelow(float alphaMult) {
        for (CPanelPlugin plugin : plugins) {
            plugin.renderBelow(alphaMult);
        }
    }

    @Override
    public void render(float alphaMult) {
        for (CPanelPlugin plugin : plugins) {
            plugin.render(alphaMult);
        }
    }

    @Override
    public void advance(float amount) {
        for (CPanelPlugin plugin : plugins) {
            plugin.advance(amount);
        }

        if (plugins.stream().anyMatch(s -> s.needsUpdate))
            this.needsUpdate = true;
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (CPanelPlugin plugin : plugins) {
            plugin.processInput(events);
        }
    }

    @Override
    public void buttonPressed(Object buttonId) {
        for (CPanelPlugin plugin : plugins) {
            plugin.buttonPressed(buttonId);
        }
    }
}
