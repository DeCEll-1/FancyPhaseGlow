package DeCell.FPG.Frontend.Backend;

import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Rect {
    public float x;
    public float y;
    public float w;
    public float h;

    public Rect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    // texCoordinates
    public void render(Rect tc) {
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        // Bottom Left
        GL11.glTexCoord2f(tc.x, tc.y);
        GL11.glVertex2f(x, y);
        // Bottom Right
        GL11.glTexCoord2f(tc.w, tc.y);
        GL11.glVertex2f(x + w, y);
        // Top Left
        GL11.glTexCoord2f(tc.x, tc.h);
        GL11.glVertex2f(x, y + h);
        // Top Right
        GL11.glTexCoord2f(tc.w, tc.h);
        GL11.glVertex2f(x + w, y + h);
        GL11.glEnd();
    }

    public void render() {
        this.render(new Rect(0, 0, 1, 1));
    }

    // does not handle enables and disables
    public void render(SpriteAPI s) {
        this.render(new Rect(0, 0, s.getTextureWidth(), s.getTextureHeight()));
    }
}
