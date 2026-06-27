package DeCell.CPG.JavaSlop.ShaderJsonParsing;

import DeCell.CPG.CustomizablePhaseGlow;
import DeCell.CPG.Frontend.Backend.Components.*;
import DeCell.CPG.Frontend.Backend.Components.Dialogues.ColorPickerV2Dialogue;
import DeCell.CPG.Frontend.Backend.Renderable.BorderRenderable;
import DeCell.CPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.CPG.Frontend.Backend.UIElement;
import DeCell.CPG.ShaderUniformManager;
import com.fs.graphics.Sprite;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.combat.entities.Ship;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static DeCell.CPG.Frontend.Backend.DataPair.pair;

public class ShaderUniformModel {
    public String name;
    public UniformType type;
    public ValueType valueType;
    public Object value;
    public float stepSize;
    public float min;
    public float max;
    public boolean modifyable;
    public String arrayLengthReferenceTarget = null;

    public ShaderUniformModel(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("Name");

            this.type = UniformType.fromString(jsonObject.getString("Type"));

            this.value = jsonObject.has("Value") ? jsonObject.get("Value") : null;

            if (value instanceof String)
                this.valueType = ValueType.fromString((String) value);
            else if (value instanceof JSONObject jsonValObj && jsonValObj.has("arrayLengthOf")) {
                this.valueType = ValueType.ARRAY_LENGTH;
                this.arrayLengthReferenceTarget = jsonValObj.getString("arrayLengthOf");
                this.value = 0;
            } else if (value == null)
                throw new NullPointerException("Shader paramater did not have a Value for:\n" + jsonObject);
            else
                this.valueType = ValueType.fromString("null");

            if (this.value instanceof JSONArray jsonArray) {
                if (jsonArray.length() == 0)
                    this.value = new float[0];
                else if (jsonArray.get(0) instanceof JSONArray) {
                    float[][] twoDArray = new float[jsonArray.length()][];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray innerArray = jsonArray.getJSONArray(i);
                        float[] innerFloatArray = new float[innerArray.length()];

                        for (int j = 0; j < innerArray.length(); j++)
                            innerFloatArray[j] = (float) innerArray.getDouble(j);

                        twoDArray[i] = innerFloatArray;
                    }
                    this.value = twoDArray;

                } else {
                    float[] floatArray = new float[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        floatArray[i] = (float) jsonArray.getDouble(i);
                    }
                    this.value = floatArray;
                }
            }

            this.modifyable = jsonObject.optBoolean("Modifyable", false);
            this.stepSize = (float) jsonObject.optDouble("StepSize", 1f);
            this.min = (float) jsonObject.optDouble("Min", 0f);
            this.max = (float) jsonObject.optDouble("Max", 1f);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public MyPanel createUniformModal(MyPanel parent, Ship ship) {
        MyPanel container = new MyPanel.Builder(280, 80).setPlugin(
                new RenderableHandlerPlugin().addBelow(
                        new BorderRenderable(Global.getSettings().getSprite("fpg", "border2"))
                                .setSlices(32)
                                .setThickness(8)
                                .setPadding(-8).setRenderInside(true)
                )
        ).build(parent);


        MyLabel text = new MyLabel
                .Builder(getLabelText(ship), 80, 24, container)
                .build()
                .inTL(2, 2);

        Vector2f modalSize = new Vector2f(20, 20);

        switch (this.type) {
            case INT, FLOAT -> modalSize = new Vector2f(280, 20);
            case VEC3 -> modalSize = new Vector2f(280, 180);
            case COL3_ARRAY -> modalSize = new Vector2f(280, 280);
        }

        MyButton openerButton = new MyButton.Builder("Edit", 60, 24, container).build().inBR(2, 2);
        if (this.type == UniformType.COL3) {
            new ColorPickerV2Dialogue().popup(
                    openerButton,
                    parent.getUIElements(),
                    CustomizablePhaseGlow.<Color>getShipProperty(ship, this.name),
                    colorPickerV2 -> {
                        ShipAPI _ship = colorPickerV2.<Ship>getFromInternal("currShip");
                        CustomizablePhaseGlow.setShipProperty(_ship, this.name, colorPickerV2.getColor());

                        MyLabel _text = colorPickerV2.getFromInternal("text");
                        _text.setText(this.getLabelText(_ship));
                    },
                    pair("currShip", ship),
                    pair("text", text)
            );
            return container;
        }

        DialougeButtonPanel modal = new DialougeButtonPanel.Builder(modalSize.x, modalSize.y, openerButton)
                .withCharlie().build(parent.getUIElements())
                .addToInternalData("currShip", ship)
                .addToInternalData("uniform", this)
                .addToInternalData("text", text)
                .showCloseButton(false)
                .setOnUIOpen(ShaderUniformModel::onModalOpen)
                .setOnUIClose(ShaderUniformModel::onModalClose);

        return container;
    }


    public String getLabelText(ShipAPI ship) {
        return this.name + ": " + CustomizablePhaseGlow.getShipProperty(ship, this.name);
    }

    private static void onModalOpen(MyPanel parent, DialougeButtonPanel modal, List<UIElement<?, ?>> IUElements) {
        ShaderUniformModel uniform = modal.getFromInternal("uniform");
        Ship currShip = modal.getFromInternal("currShip");

        switch (uniform.type) {
            case INT, FLOAT -> {
                NumericUpDown nud = new NumericUpDown.Builder().build(190, parent).inLMid(0)
                        .setValue(CustomizablePhaseGlow.<Number>getShipProperty(currShip, uniform.name).doubleValue(), false)
                        .setStepSize(uniform.stepSize).setMinMax(uniform.min, uniform.max);
                MyButton acceptButton = new MyButton.Builder("Accept", 70, 20, parent).build().inRMid(0)
                        .addToInternalData("nud", nud)
                        .addToInternalData("currShip", currShip)
                        .addToInternalData("uniform", uniform)
                        .addOnMouseDown(btn -> {
                            double val = btn.<NumericUpDown>getFromInternal("nud").getValue();
                            CustomizablePhaseGlow.setShipProperty(
                                    btn.<Ship>getFromInternal("currShip"),
                                    btn.<ShaderUniformModel>getFromInternal("uniform").name,
                                    val);
                        });
                modal.setCloseButton(acceptButton);
            }
            case COL3_ARRAY -> { // TODO: uhhhh make a scrollbar, or dont
                generateColorList(parent, null, uniform, currShip);
            }
        }

    }

    private static void generateColorList(MyPanel parent, MyPanel container, ShaderUniformModel uniform, Ship currShip) {
        if (container != null) {
            container.markForDeletion();
        }
        container = new MyPanel.Builder(parent.rect().w, parent.rect().h).build(parent);
        float padding = 8f;
        float rectSize = (container.rect().w - (5 * padding)) / 4;

        Object val = getValOrDefault(uniform, currShip, null);

        // 1. Get your short array (e.g., size 3)
        Color[] originalColors = getColorsFromFloatMatrix((float[][]) val);
        int actualColorCount = originalColors.length;

        // 2. Expand the array container to full capacity (size 16), leaving the rest null
        Color[] colors = new Color[(int) Math.floor(uniform.max)];
        if (actualColorCount > 0) {
            System.arraycopy(originalColors, 0, colors, 0, Math.min(actualColorCount, colors.length));
        }

        for (int i = 0; i < uniform.max; i++) {
            if (i > actualColorCount) {
                break;
            }

            int row = (int) Math.floor(i / 4f);
            int col = i % 4;

            float y = padding + (row * (rectSize + padding));
            float x = padding + (col * (rectSize + padding));

            Color currColor = null;
            boolean empty = false;

            if (i < actualColorCount) {
                currColor = colors[i];
            } else {
                currColor = Color.black;
                empty = true;
            }

            MyButton colorButton = new MyButton.Builder(empty ? "+" : "", rectSize, rectSize, container)
                    .setColors(Color.white, currColor).build()
                    .inTL(x, y);

            final int index = i;

            // --- NEW: DELETION LOGIC FOR EXISTING COLORS ---
            if (!empty) {
                // Create a small 'X' delete button in the top-right corner of the color block
                MyButton deleteButton = new MyButton.Builder("X", 16, 16, container)
                        .setColors(Color.RED, Color.DARK_GRAY).build()
                        .inTL(x + rectSize - 18, y + 2); // Positioned slightly inside the color block

                deleteButton.addToInternalData("parent", parent)
                        .addToInternalData("container", container)
                        .addToInternalData("uniform", uniform)
                        .addToInternalData("currShip", currShip)
                        .addToInternalData("colors", originalColors) // Send the original trimmed array
                        .addToInternalData("indexToDelete", index)
                        .addOnMouseDown(btn -> {
                            MyPanel _parent = btn.getFromInternal("parent");
                            MyPanel _container = btn.getFromInternal("container");
                            ShaderUniformModel _uniform = btn.getFromInternal("uniform");
                            Ship _currShip = btn.getFromInternal("currShip");
                            Color[] _origColors = btn.getFromInternal("colors");
                            int _delIdx = btn.getFromInternal("indexToDelete");

                            // Build a brand new array that is 1 element shorter
                            Color[] compactedColors = new Color[_origColors.length - 1];

                            int destIdx = 0;
                            for (int srcIdx = 0; srcIdx < _origColors.length; srcIdx++) {
                                if (srcIdx == _delIdx) continue; // Skip the deleted element
                                compactedColors[destIdx++] = _origColors[srcIdx];
                            }

                            // Save the newly shrunk matrix back to the ship property
                            CustomizablePhaseGlow.setShipProperty(_currShip, _uniform.name, getFloatMatrixFromColors(compactedColors));

                            // Refresh UI
                            generateColorList(_parent, _container, _uniform, _currShip);
                        });
            }
            // ------------------------------------------------

            // Keep your existing Picker popup logic intact for when they click the main button
            if (!empty || i == actualColorCount) {
                new ColorPickerV2Dialogue().popup(
                        colorButton,
                        container.getUIElements(),
                        currColor,
                        (picker) -> {
                            MyPanel _parent = picker.getFromInternal("parent");
                            MyPanel _container = picker.getFromInternal("container");
                            ShaderUniformModel _uniform = picker.getFromInternal("uniform");
                            Ship _currShip = picker.getFromInternal("currShip");
                            Color[] _colors = picker.getFromInternal("colors");
                            int _i = picker.getFromInternal("i");

                            _colors[_i] = picker.getColor();

                            Color[] compactedColors = new Color[_i + 1 > actualColorCount ? _i + 1 : actualColorCount];
                            System.arraycopy(_colors, 0, compactedColors, 0, compactedColors.length);

                            CustomizablePhaseGlow.setShipProperty(_currShip, _uniform.name, getFloatMatrixFromColors(compactedColors));

                            generateColorList(_parent, _container, _uniform, _currShip);
                        },
                        pair("parent", parent),
                        pair("container", container),
                        pair("uniform", uniform),
                        pair("currShip", currShip),
                        pair("colors", colors),
                        pair("i", index)
                );
            }
        }
    }

    private static void onModalClose(DialougeButtonPanel modal) {
        ShaderUniformModel uniform = modal.getFromInternal("uniform");
        Ship currShip = modal.getFromInternal("currShip");
        MyLabel _text = modal.getFromInternal("text");
        _text.setText(uniform.getLabelText(currShip));
    }

    public void update(ShaderUniformManager s, ShipAPI ship, Sprite sprite) {
        if (Objects.equals(this.name, ShaderJsonModel.phaseAlphaMultKeyword))
            return;
        Object val = getValOrDefault(this, ship, sprite);
        boolean isEngine = isEngine(this);

        switch (this.type) {
            case INT -> {
                s.setInt(this.name, ((Number) val).intValue());
            }
            case FLOAT -> {
                if (val instanceof Double dob)
                    val = dob.floatValue();
                s.setFloat(this.name, ((Number) val).floatValue());
            }
            case COL3 -> {
                float[] zaza = (float[]) val;
                s.setColor3(this.name, new Color((int) zaza[0], (int) zaza[1], (int) zaza[2]));
            }
            case VEC3 -> {
                float[] zaza = (float[]) val;
                s.setVector3(this.name, new Vector3f(zaza[0], zaza[1], zaza[2]));
            }
            case COL3_ARRAY -> {
                Color[] colors = getColorsFromFloatMatrix((float[][]) val);

                s.setColor3Array(this.name, colors);
            }
            case VEC3_ARRAY -> {
                float[][] zaza = (float[][]) val;
                Vector3f[] vectors = new Vector3f[zaza.length];

                for (int i = 0; i < zaza.length; i++)
                    vectors[i] = new Vector3f(zaza[i][0], zaza[i][1], zaza[i][2]);

                s.setVector3Array(this.name, vectors);
            }
            case SAMPLER_2D -> {
                if (isEngine) {
                    s.setTexture(this.name, (int) val, GL13.GL_TEXTURE0);
                    return;
                }
                // TODO: implement custom textures
                throw new UnsupportedOperationException("Custom textures is not implemented yet");
            }
        }
    }

    private static Color[] getColorsFromFloatMatrix(float[][] val) {
        float[][] zaza = val;
        Color[] colors = new Color[zaza.length];

        for (int i = 0; i < zaza.length; i++)
            colors[i] = new Color((int) zaza[i][0], (int) zaza[i][1], (int) zaza[i][2]);
        return colors;
    }

    private static float[][] getFloatMatrixFromColors(Color[] colors) {
        if (colors == null) return null;

        float[][] val = new float[colors.length][3];

        for (int i = 0; i < colors.length; i++) {
            if (colors[i] != null) {
                val[i][0] = colors[i].getRed();   // R
                val[i][1] = colors[i].getGreen(); // G
                val[i][2] = colors[i].getBlue();  // B
            }
        }
        return val;
    }

    private static Object getValOrDefault(ShaderUniformModel uniform, ShipAPI ship, Sprite sprite) {
        boolean isEngine = isEngine(uniform);
        if (uniform.arrayLengthReferenceTarget != null)
            return CustomizablePhaseGlow.getShipProperty(ship, "arrLen_" + uniform.name);

        Object val = isEngine ? uniform.getFromEngine(ship, sprite) : CustomizablePhaseGlow.getShipProperty(ship, uniform.name);
        if (val == null || (!uniform.modifyable && uniform.type != UniformType.SAMPLER_2D && !(uniform.value instanceof String)))
            val = uniform.value;

        return val;
    }

    private static boolean isEngine(ShaderUniformModel u) {
        boolean isEngine = false;
        if (u.value instanceof String)
            isEngine = true; // currently the only way the value can be a string is if its an engine value
        return isEngine;
    }

    private Object getFromEngine(ShipAPI ship, Sprite sprite) {
        if (Objects.equals(this.valueType.jsonValue, ValueType.OTHER.jsonValue))
            throw new IllegalArgumentException("Tried to get from engine for non-engine value");


//        if (this.type != this.valueType.type)
//            throw new IllegalArgumentException("Uniform type and engine variable type must be the same");

        switch (this.valueType) {
            case TIME -> {
                return ship.getFullTimeDeployed();
            }
            case FLUX -> {
                return ship.getFluxLevel();
            }
            case HULL -> {
                return ship.getHitpoints();
            }
            case PHASE_TEXTURE -> {
                if (sprite == null || sprite.getTexture() == null) return 0;
                return sprite.getTexture().ö00000(); // TODO: fix this to not use obfuscated names
            }
            case TEX_WIDTH -> {
                return sprite != null ? sprite.getTexWidth() : 1.0f;
            }
            case TEX_HEIGHT -> {
                return sprite != null ? sprite.getTexHeight() : 1.0f;
            }
            default -> throw new IllegalStateException("Unexpected value: " + this.valueType);
        }
    }

    public enum ValueType {
        TIME("time", UniformType.FLOAT),
        FLUX("flux", UniformType.FLOAT),
        HULL("hull", UniformType.FLOAT),
        PHASE_TEXTURE("phase_texture", UniformType.SAMPLER_2D),
        TEX_WIDTH("texWidth", UniformType.FLOAT),
        TEX_HEIGHT("texHeight", UniformType.FLOAT),
        ARRAY_LENGTH("array_length", UniformType.INT),
        OTHER("other", null);

        private final String jsonValue;
        private final UniformType type;

        ValueType(String jsonVal, UniformType type) {
            this.jsonValue = jsonVal;
            this.type = type;
        }

        public static ValueType fromString(String text) {
            if (Objects.equals(text, "null"))
                return ValueType.OTHER;
            for (ValueType type : ValueType.values()) {
                if (type.jsonValue.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown value string: " + text);
        }
    }

    public enum UniformType {
        INT("int"),
        FLOAT("float"),
        COL3("col3"),
        VEC3("vec3"),
        COL3_ARRAY("col3[]"),
        VEC3_ARRAY("vec3[]"),
        SAMPLER_2D("sampler2D");

        private final String jsonValue;

        UniformType(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public String getJsonValue() {
            return jsonValue;
        }

        public static UniformType fromString(String text) {
            for (UniformType type : UniformType.values()) {
                if (type.jsonValue.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown uniform type: " + text);
        }
    }

    @Override
    public String toString() {
        return "ShaderUniformModel{" +
                "\nname='" + name + '\'' +
                "\n, type=" + type +
                "\n, valueType=" + valueType +
                "\n, value=" + value +
                "\n, modifyable=" + modifyable +
                "\n}\n";
    }
}
