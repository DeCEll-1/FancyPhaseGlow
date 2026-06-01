package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Plugins.APluginRenderable;
import DeCell.FPG.Frontend.Backend.Rect;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import static org.lwjgl.opengl.GL11.*;

public class BorderRenderable extends APluginRenderable {

    private SpriteAPI borderSprite;     // The border texture (9-patch style recommended)

    // Individual side thicknesses (for more control)
    private float left, right, top, bottom;
    private float padding = 0;
    private boolean renderInside = false;

    public BorderRenderable(SpriteAPI borderSprite, float thickness) {
        this.borderSprite = borderSprite;
        this.left = this.right = this.top = this.bottom = thickness;
    }

    public BorderRenderable(SpriteAPI borderSprite, float left, float right, float top, float bottom) {
        this.borderSprite = borderSprite;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }


    @Override
    public void init(CustomPanelAPI parent) {
        super.init(parent);
    }

    @Override
    public void update(CustomPanelAPI parent) {
        super.update(parent);
    }

    @Override
    public void renderBelow(float alpha) {
        if (zone == null || borderSprite == null) return;

        float x = zone.x;
        float y = zone.y;
        float w = zone.w;
        float h = zone.h;

        // ignore the ai slop comments it shet the bed like 40 times ill clean them up eventually
        glEnable(GL_TEXTURE_2D);
        borderSprite.bindTexture();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

//        borderSprite.setAlphaMult(alpha);


        float texS = borderSprite.getTextureWidth(); // in decimal
        float texT = borderSprite.getTextureHeight(); // in decimal
        float texW = borderSprite.getWidth() / texT; // raw pixel width
        float texH = borderSprite.getHeight() / texT; // raw pixel height

        float borderTopTextureBorder = texT - (top / texH);
        float borderRightTextureBorder = texS - (right / texW);
        float borderBottomTextureBorder = bottom / texH;
        float borderLeftTextureBorder = left / texW;

        // zone.render(borderSprite);

        // Top Left
        new Rect(x + padding, y + h - top - padding, left, top)
                .render(new Rect(0, borderTopTextureBorder, borderLeftTextureBorder, texT));

        // Top Right
        new Rect(x + w - right - padding, y + h - top - padding, right, top)
                .render(new Rect(borderRightTextureBorder, borderTopTextureBorder, texS, texT));

        // Bottom Left
        new Rect(x + padding, y + padding, left, bottom)
                .render(new Rect(0, 0, borderLeftTextureBorder, borderBottomTextureBorder));

        // Bottom Right
        new Rect(x + w - right - padding, y + padding, right, bottom)
                .render(new Rect(borderRightTextureBorder, 0, texS, borderBottomTextureBorder));

        // Top Edge
        new Rect(x + left + padding, y + h - top - padding, w - left - right - (2 * padding), top)
                .render(new Rect(borderLeftTextureBorder, borderTopTextureBorder, borderRightTextureBorder, texT));

        // Bottom Edge
        new Rect(x + left + padding, y + padding, w - left - right - (2 * padding), bottom)
                .render(new Rect(borderLeftTextureBorder, 0, borderRightTextureBorder, borderBottomTextureBorder));

        // Left Edge
        new Rect(x + padding, y + bottom + padding, left, h - top - bottom - (2 * padding))
                .render(new Rect(0, borderBottomTextureBorder, borderLeftTextureBorder, borderTopTextureBorder));

        // Right Edge
        new Rect(x + w - right - padding, y + bottom + padding, right, h - top - bottom - (2 * padding))
                .render(new Rect(borderRightTextureBorder, borderBottomTextureBorder, texS, borderTopTextureBorder));

        if (renderInside)
            new Rect(x + left + padding, y + bottom + padding, w - left - right - (2 * padding), h - top - bottom - (2 * padding))
                    .render(new Rect(borderLeftTextureBorder, borderBottomTextureBorder, borderRightTextureBorder, borderTopTextureBorder));

//        GL11.glColor4f(1f, 1f, 1f, 1f);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public BorderRenderable setPadding(float s) {
        this.padding = s;
        return this;
    }

    public BorderRenderable setRenderInside(boolean s) {
        this.renderInside = s;
        return this;
    }
}