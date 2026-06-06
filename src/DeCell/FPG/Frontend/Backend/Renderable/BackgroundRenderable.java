package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Plugins.PluginRenderable;
import DeCell.FPG.Misc;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BackgroundRenderable extends PluginRenderable {
    Color c;
    public boolean render = true;

    public BackgroundRenderable(Color _c) {
        this.c = _c;
    }

    @Override
    public void renderBelow(float alpha) {
        if (zone == null)
            return;
        if (!render)
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
