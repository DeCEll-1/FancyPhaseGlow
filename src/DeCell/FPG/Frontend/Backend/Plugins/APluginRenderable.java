package DeCell.FPG.Frontend.Backend.Plugins;

import DeCell.FPG.Frontend.Backend.Rect;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

public abstract class APluginRenderable {
    protected Rect zone;

    private void updateZone(PositionAPI p) {
        zone = new Rect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
    }

    public void init(CustomPanelAPI parent) {
        PositionAPI p = parent.getPosition();
        zone = new Rect(-1f, -1f, p.getWidth(), p.getHeight());
    }

    public boolean needsUpdate() {
        return zone.x == -1f && zone.y == -1f;
    }

    public void update(CustomPanelAPI parent) {
        updateZone(parent.getPosition());
    }

    public void render(float alpha) {
    }

    public void renderBelow(float alpha) {
    }
}
