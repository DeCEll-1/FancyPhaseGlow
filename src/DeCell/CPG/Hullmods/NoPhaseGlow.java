package DeCell.CPG.Hullmods;

import DeCell.CPG.CustomizablePhaseGlow;
import DeCell.CPG.JavaSlop.ShaderJsonParsing.ShaderJsonModel;
import DeCell.CPG.Reflection.Reflections;
import DeCell.CPG.Shader;
import com.fs.graphics.Sprite;
import com.fs.graphics.util.B;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.combat.entities.Ship;
import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// this is a stupid name, fym no phase glow this handles all the damn jazz for rendering shit too bruh
public class NoPhaseGlow extends BaseHullMod {
    private static long startTime = System.currentTimeMillis();
    private static final String glow1Appendance = "_glow1";
    private static final String glow2Appendance = "_glow2";

    @Override
    public void init(HullModSpecAPI spec) {
        super.init(spec);
    }

    private static String getCompleteFilePathOfShipOverlay(ShipAPI ship, String appendance) {
        try {
            String filePathRegex = "^.*(data.*$)";
            String shipFilePath = ship.getHullSpec().getShipFilePath().replace('\\', '/');
            Matcher matcher = Pattern.compile(filePathRegex).matcher(shipFilePath);
            matcher.find();
            String match = matcher.group(1);

            String spritePath = new JSONObject(Global.getSettings().loadText(match)).getString("spriteName");

            String extensionRegex = "(^.*)(\\..*$)";
            Matcher matcher2 = Pattern.compile(extensionRegex).matcher(spritePath);
            matcher2.find();
            String match2 = matcher2.group(1) + appendance + matcher2.group(2);
            return match2;
        } catch (JSONException | IOException e) { // oh my god i fucking hate javas shitty ass try catch bs
            throw new RuntimeException(e);
        }
    }

    private static String getGlow1SpritePath(ShipAPI ship) {
        return getCompleteFilePathOfShipOverlay(ship, glow1Appendance);
    }

    private static String getGlow2SpritePath(ShipAPI ship) {
        return getCompleteFilePathOfShipOverlay(ship, glow2Appendance);
    }

    private static Sprite getOverridenSpriteForTexture(String texPath, ShipAPI ship) {
        Sprite sprt = new Sprite(texPath) {
            //            @Override
            public void render(float x, float y) {
                if (this.texture == null)
                    return;

                // TODO: use unobfuscated names
                this.texture.Ø00000(); // bind texture
                ShaderJsonModel shaderData = CustomizablePhaseGlow.getShaderForShip(ship);

                if (shaderData != null && shaderData.getShader() != null) {
                    Shader shader = shaderData.getShader();
                    shader.bind();


                    shaderData.updateUniformValues(ship, this);

                    int texID = this.getTexture().ö00000();
                    float t = ship.getFullTimeDeployed();
                }


                boolean useClamp = true;
                if (useClamp) {
                    B.Ø00000(); // enable clamp
                }

                GL11.glPushMatrix();

//                GL11.glColor4ub(
//                        (byte) this.color.getRed(),
//                        (byte) this.color.getGreen(),
//                        (byte) this.color.getBlue(),
//                        (byte) ((int) ((float) this.color.getAlpha() * this.getAlphaMult()))
//                );

                GL11.glColor4ub(
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) ((int) ((float) this.color.getAlpha() * this.getAlphaMult() *
                                ((Number) CustomizablePhaseGlow.getShipProperty(ship, ShaderJsonModel.phaseAlphaMultKeyword)).floatValue()
                        ))
                );

                float centerX = this.getCenterX();
                float centerY = this.getCenterY();
                float offsetX = this.getOffsetX();
                float offsetY = this.getOffsetY();

                // Translate to position (centered)
                GL11.glTranslatef(x + offsetX, y + offsetY, 0.0F);

                // Handle custom center + rotation
                if (centerX != -1.0f && centerY != -1.0f) {
                    GL11.glTranslatef(this.width / 2.0f, this.height / 2.0f, 0.0f);
                    GL11.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
                    GL11.glTranslatef(-centerX, -centerY, 0.0f);
                } else {
                    // Default: rotate around center of sprite
                    GL11.glTranslatef(this.width / 2.0f, this.height / 2.0f, 0.0f);
                    GL11.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
                    GL11.glTranslatef(-this.width / 2.0f, -this.height / 2.0f, 0.0f);
                }

                GL11.glEnable(GL11.GL_TEXTURE_2D);      // was 3553
                GL11.glEnable(GL11.GL_BLEND);           // was 3042
                GL11.glBlendFunc(this.getBlendSrc(), this.getBlendDest());

                GL11.glBegin(GL11.GL_QUADS);            // was 7
                GL11.glTexCoord2f(this.texX, this.texY);
                GL11.glVertex2f(0.0f, 0.0f);

                GL11.glTexCoord2f(this.texX, this.texY + this.texHeight);
                GL11.glVertex2f(0.0f, this.height);

                GL11.glTexCoord2f(this.texX + this.texWidth, this.texY + this.texHeight);
                GL11.glVertex2f(this.width, this.height);

                GL11.glTexCoord2f(this.texX + this.texWidth, this.texY);
                GL11.glVertex2f(this.width, 0.0f);
                GL11.glEnd();

                GL11.glDisable(GL11.GL_BLEND);          // was 3042
                GL11.glPopMatrix();

                if (useClamp) {
                    B.Ò00000(); // disable clamp
                }
                if (shaderData != null && shaderData.getShader() != null) {
                    shaderData.getShader().unbind();
                }
            }
        };
//        float val = ((Number) FancyPhaseGlow.getShipProperty(ship, ShaderJsonModel.phaseAlphaMultKeyword)).floatValue();
//        sprt.setAlphaMult(val); // does not function as the game uses alpha mult for coils
        return sprt;
    }


    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (diffuseSpriteHandle == null) UpdateSpriteHandles(ship);
        Sprite tmp;

        Sprite customDiffuseSprite = getOverridenSpriteForTexture(getGlow1SpritePath(ship), ship);
        tmp = (Sprite) diffuseSpriteHandle.get(((Ship) ship).getPhaseCloak());

        customDiffuseSprite.setCenter(tmp.getCenterX(), tmp.getCenterY());
        customDiffuseSprite.setOffset(tmp.getOffsetX(), tmp.getOffsetY());
        diffuseSpriteHandle.set(((Ship) ship).getPhaseCloak(), customDiffuseSprite);


        Sprite customHighlightSprite = getOverridenSpriteForTexture(getGlow2SpritePath(ship), ship);
        tmp = (Sprite) highlightSpriteHandle.get(((Ship) ship).getPhaseCloak());

        customHighlightSprite.setCenter(tmp.getCenterX(), tmp.getCenterY());
        customHighlightSprite.setOffset(tmp.getOffsetX(), tmp.getOffsetY());
        highlightSpriteHandle.set(((Ship) ship).getPhaseCloak(), customHighlightSprite);
    }

    private static VarHandle diffuseSpriteHandle;
    private static VarHandle highlightSpriteHandle;


    private void UpdateSpriteHandles(ShipAPI ship) {
        try {
            Class<?> phaseCloakClass = ship.getPhaseCloak().getClass();

            for (Object field : phaseCloakClass.getDeclaredFields()) {
                Global.getLogger(CustomizablePhaseGlow.class).log(Level.INFO, Reflections.getFieldNameHandle.invoke(field));
                if (Reflections.getFieldTypeHandle.invoke(field) == Sprite.class) {
                    String fieldName = (String) Reflections.getFieldNameHandle.invoke(field);

                    if (diffuseSpriteHandle == null) {
                        diffuseSpriteHandle = MethodHandles.privateLookupIn(phaseCloakClass, MethodHandles.lookup()).findVarHandle(
                                phaseCloakClass,
                                fieldName,
                                Sprite.class
                        );
                    } else if (highlightSpriteHandle == null) {
                        highlightSpriteHandle = MethodHandles.privateLookupIn(phaseCloakClass, MethodHandles.lookup()).findVarHandle(
                                phaseCloakClass,
                                fieldName,
                                Sprite.class
                        );
                    }
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


}