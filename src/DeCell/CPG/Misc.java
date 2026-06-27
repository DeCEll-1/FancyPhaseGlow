package DeCell.CPG;

import com.fs.starfarer.api.Global;
import org.lwjgl.opengl.GL11;


import java.awt.Color;
import java.util.Random;

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

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static Color getRandomColor(int alpha) {
        return new Color(
                (new Random().nextInt() & 0x00FFFFFF) | (0x55 << 24)
                , true);
    }

    public static Color getRandomColor() {
        return getRandomColor(0x55);
    }

    public static Color negativeColor(Color c) {
        return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
    }

    public static Color getBrightPlayerColor() {
        return Global.getSector().getPlayerFaction().getBrightUIColor();
    }

    public static Color getBasePlayerColor() {
        return Global.getSector().getPlayerFaction().getBaseUIColor();
    }

    public static Color getDarkPlayerColor() {
        return Global.getSector().getPlayerFaction().getDarkUIColor();
    }

    public static Color getTextColor() {
        return Global.getSettings().getColor("standardTextColor");
    }

    public static Color getButtonTextColor() {
        return Global.getSettings().getColor("buttonText");
    }

}
