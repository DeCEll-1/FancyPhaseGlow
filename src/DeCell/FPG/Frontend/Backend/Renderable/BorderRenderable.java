package DeCell.FPG.Frontend.Backend.Renderable;

import DeCell.FPG.Frontend.Backend.Rect;
import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.UIElement;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import static org.lwjgl.opengl.GL11.*;

public class BorderRenderable extends PluginRenderable {

    private SpriteAPI borderSprite;     // The border texture (9-patch style recommended)

    // the texture thickness widths
    private float leftSlice, rightSlice, topSlice, bottomSlice;

    // the actual border width
    private float leftThickness, rightThickness, topThickness, bottomThickness;

    private float padding = 0;
    private boolean renderInside = false;

    public BorderRenderable(SpriteAPI borderSprite) {
        this.borderSprite = borderSprite;
    }

    public BorderRenderable(SpriteAPI borderSprite, float left, float right, float top, float bottom) {
        this.borderSprite = borderSprite;
        this.leftSlice = left;
        this.rightSlice = right;
        this.topSlice = top;
        this.bottomSlice = bottom;
    }

    public BorderRenderable setSlices(float slice) {
        this.leftSlice = this.rightSlice = this.topSlice = this.bottomSlice = slice;
        return this;
    }

    public BorderRenderable setSlices(float left, float top, float right, float bottom) {
        this.leftSlice = left;
        this.topSlice = top;
        this.rightSlice = right;
        this.bottomSlice = bottom;
        return this;
    }

    public BorderRenderable setThickness(float thickness) {
        leftThickness = rightThickness = topThickness = bottomThickness = thickness;
        return this;
    }

    public BorderRenderable setThickness(float left, float top, float right, float bottom) {
        this.leftThickness = left;
        this.topThickness = top;
        this.rightThickness = right;
        this.bottomThickness = bottom;
        return this;
    }

    @Override
    public void init(UIContainer<? extends UIElement<?, CustomPanelAPI>, CustomPanelAPI> parent) {
        super.init(parent);
    }

    @Override
    public void update(UIContainer<? extends UIElement<?, CustomPanelAPI>, CustomPanelAPI> parent) {
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

        float borderTopTextureBorder = texT - (topSlice / texH);
        float borderRightTextureBorder = texS - (rightSlice / texW);
        float borderBottomTextureBorder = bottomSlice / texH;
        float borderLeftTextureBorder = leftSlice / texW;


        // Top Left
        new Rect(x + padding, y + h - topThickness - padding, leftThickness, topThickness) // TODO: cache these
                .render(new Rect(0, borderTopTextureBorder, borderLeftTextureBorder, texT));

        // Top Right
        new Rect(x + w - rightThickness - padding, y + h - topThickness - padding, rightThickness, topThickness)
                .render(new Rect(borderRightTextureBorder, borderTopTextureBorder, texS, texT));

        // Bottom Left
        new Rect(x + padding, y + padding, leftThickness, bottomThickness)
                .render(new Rect(0, 0, borderLeftTextureBorder, borderBottomTextureBorder));

        // Bottom Right
        new Rect(x + w - rightThickness - padding, y + padding, rightThickness, bottomThickness)
                .render(new Rect(borderRightTextureBorder, 0, texS, borderBottomTextureBorder));

        // Top Edge
        new Rect(x + leftThickness + padding, y + h - topThickness - padding, w - leftThickness - rightThickness - (2 * padding), topThickness)
                .render(new Rect(borderLeftTextureBorder, borderTopTextureBorder, borderRightTextureBorder, texT));

        // Bottom Edge
        new Rect(x + leftThickness + padding, y + padding, w - leftThickness - rightThickness - (2 * padding), bottomThickness)
                .render(new Rect(borderLeftTextureBorder, 0, borderRightTextureBorder, borderBottomTextureBorder));

        // Left Edge
        new Rect(x + padding, y + bottomThickness + padding, leftThickness, h - topThickness - bottomThickness - (2 * padding))
                .render(new Rect(0, borderBottomTextureBorder, borderLeftTextureBorder, borderTopTextureBorder));

        // Right Edge
        new Rect(x + w - rightThickness - padding, y + bottomThickness + padding, rightThickness, h - topThickness - bottomThickness - (2 * padding))
                .render(new Rect(borderRightTextureBorder, borderBottomTextureBorder, texS, borderTopTextureBorder));

        if (renderInside) // TODO: make this a seperate texture as it scales like a bitch with this setup
            new Rect(x + leftThickness + padding, y + bottomThickness + padding, w - leftThickness - rightThickness - (2 * padding), h - topThickness - bottomThickness - (2 * padding))
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