package DeCell.FPG.JavaSlop.ShaderJsonParsing;

import DeCell.FPG.Frontend.Backend.Components.ColorPicker.ColorPickerV2;
import DeCell.FPG.Frontend.Backend.Components.Dialogues.ColorPickerV2Dialogue;
import DeCell.FPG.Frontend.Backend.Components.DialougeButtonPanel;
import DeCell.FPG.Frontend.Backend.Components.MyButton;
import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import DeCell.FPG.Frontend.Backend.Components.NumericUpDown;
import DeCell.FPG.Frontend.Backend.DataPair;
import DeCell.FPG.Frontend.Backend.UIElement;
import DeCell.FPG.ShaderUniformManager;
import com.fs.graphics.Sprite;
import com.fs.starfarer.api.combat.ShipAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static DeCell.FPG.Frontend.Backend.DataPair.pair;

public class ShaderUniformModel {
    public String name;
    public UniformType type;
    public ValueType valueType;
    public Object value;
    public boolean modifyable;

    public ShaderUniformModel(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("Name");

            this.type = UniformType.fromString(jsonObject.getString("Type"));

            this.value = jsonObject.has("Value") ? jsonObject.get("Value") : null;

            if (value instanceof String)
                this.valueType = ValueType.fromString((String) value);
            else if (value == null)
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

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public MyPanel createUniformModal(MyPanel parent) {
        MyPanel container = new MyPanel.Builder(280, 80).build(parent);

        Vector2f modalSize = new Vector2f(20, 20);

        MyButton openerButton = new MyButton.Builder("Edit", 60, 24, container).build().inBR(2, 2);

        switch (this.type) {
            case INT, FLOAT -> modalSize = new Vector2f(280, 20);
            case VEC3 -> modalSize = new Vector2f(280, 180);
        }

        if (this.type == UniformType.COL3) {
            new ColorPickerV2Dialogue().popup(
                    openerButton,
                    parent.getUIElements(),
                    colorPickerV2 -> colorPickerV2.
                            <MyPanel>getFromInternal("master")
                            .addToInternalData(this.name, colorPickerV2.getColor()),
                    pair("master", parent)
            );
            return container;
        }


        DialougeButtonPanel modal = new DialougeButtonPanel.Builder(modalSize.x, modalSize.y, openerButton)
                .withCharlie().build(parent.getUIElements())
                .addToInternalData("master", parent)
                .addToInternalData("uniform", this)
                .setOnUIOpen(ShaderUniformModel::onModalOpen);

        return container;
    }

    private static void onModalOpen(MyPanel parent, DialougeButtonPanel modal, List<UIElement<?, ?>> IUElements) {
        MyPanel master = modal.getFromInternal("master");
        ShaderUniformModel uniform = modal.getFromInternal("uniform");

        switch (uniform.type) {
            case INT, FLOAT -> {
                NumericUpDown nud = new NumericUpDown.Builder().build(200, parent).inLMid(0);
                new MyButton.Builder("Accept", 80, 20, parent).build().inRMid(0)
                        .addToInternalData("nud", nud)
                        .addToInternalData("master", master)
                        .addToInternalData("uniform", uniform)
                        .setOnMouseDown(btn -> {
                            double val = btn.<NumericUpDown>getFromInternal("nud").getValue();
                            btn.<MyPanel>getFromInternal("master")
                                    .addToInternalData(
                                            btn.<ShaderUniformModel>getFromInternal("uniform").name,
                                            val
                                    );
                        });
            }
            case COL3_ARRAY -> { // TODO: uhhhh make a scrollbar

            }
        }
    }

    public void update(ShaderUniformManager s, ShipAPI ship, Sprite sprite) {
        boolean isEngine = false;
        if (this.value instanceof String)
            isEngine = true; // currently the only way the value can be a string is if its an engine value

        Object val = isEngine ? getFromEngine(ship, sprite) : this.value;

        switch (this.type) {
            case INT -> {
                s.setInt(this.name, ((Integer) val));
            }
            case FLOAT -> {
                if (val instanceof Double dob)
                    val = dob.floatValue();
                s.setFloat(this.name, (Float) val);
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
                float[][] zaza = (float[][]) val;
                Color[] colors = new Color[zaza.length];

                for (int i = 0; i < zaza.length; i++)
                    colors[i] = new Color((int) zaza[i][0], (int) zaza[i][1], (int) zaza[i][2]);

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
