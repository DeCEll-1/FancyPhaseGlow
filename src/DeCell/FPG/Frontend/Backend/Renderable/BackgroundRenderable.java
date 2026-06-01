package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Plugins.APluginRenderable;
import DeCell.FPG.Frontend.Backend.Rect;
import DeCell.FPG.Misc;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BackgroundRenderable extends APluginRenderable {
    Color c;

    public BackgroundRenderable(Color _c) {
        this.c = _c;
    }

    @Override
    public void renderBelow(float alpha) {
        if (zone == null)
            return;
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Misc.setColor(c);

        zone.render();

        GL11.glColor4f(1f, 1f, 1f, 1f); // reset color
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
