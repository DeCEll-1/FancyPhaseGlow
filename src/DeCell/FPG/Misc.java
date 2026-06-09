package DeCell.FPG;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;


import java.awt.Color;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Misc {
    public static void setColor(java.awt.Color c) {
        GL11.glColor4f(
                c.getRed() / 255f,
                c.getGreen() / 255f,
                c.getBlue() / 255f,
                c.getAlpha() / 255f
        );
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
