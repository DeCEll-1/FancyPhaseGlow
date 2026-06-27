package DeCell.CPG.JavaSlop.ShaderJsonParsing;

import DeCell.CPG.CustomizablePhaseGlow;
import DeCell.CPG.Shader;
import com.fs.graphics.Sprite;
import com.fs.starfarer.api.combat.ShipAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShaderJsonModel {
    public String title;
    public String id;
    public String fragmentShaderPath;
    public String vertexShaderPath; //?
    public List<ShaderUniformModel> uniforms;
    private Shader shader;
    // what this is is, its jank, i needed the phae alpha mult as a customisable variable and the easiest way to do that is by putting
    // it as a uniform
    public static final String phaseAlphaMultKeyword = "_phasecoil_alpha_mult";
    private String alphaUniformJson = "\n" +
            "                {\n" +
            "                    \"Name\": \"" + phaseAlphaMultKeyword + "\",\n" +
            "                    \"Type\": \"float\",\n" +
            "                    \"Value\": 1,\n" +
            "                    \"Modifyable\": true,\n" +
            "                    \"Min\": 0,\n" +
            "                    \"Max\": 1,\n" +
            "                    \"StepSize\": 0.05\n" +
            "                },";

    public ShaderJsonModel(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("Title");
            this.id = jsonObject.getString("ID");
            this.fragmentShaderPath = jsonObject.getString("FragmentShaderPath");

            this.vertexShaderPath = jsonObject.optString("VertexShaderPath", "data/shaders/fpg/main.vert");

            this.uniforms = new ArrayList<>();
            if (jsonObject.has("Uniforms")) {
                this.uniforms.add(new ShaderUniformModel(new JSONObject(alphaUniformJson)));
                JSONArray uniformsArray = jsonObject.getJSONArray("Uniforms");
                for (int i = 0; i < uniformsArray.length(); i++) {
                    this.uniforms.add(new ShaderUniformModel(uniformsArray.getJSONObject(i)));
                }
            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public ShaderUniformModel getUniformFromName(String name) {
        for (ShaderUniformModel uniform : this.uniforms) {
            if (Objects.equals(uniform.name, name))
                return uniform;
        }
        return null;
    }

    public Shader compile() {
        if (shader == null)
            shader = Shader.fromFile(vertexShaderPath, fragmentShaderPath, title);
        return shader;
    }

    public Shader getShader() {
        return shader;
    }

    public void updateUniformValues(ShipAPI ship, Sprite sprite) {
        for (ShaderUniformModel uniform : uniforms) {
            if (uniform.valueType == ShaderUniformModel.ValueType.ARRAY_LENGTH) {
                Object targetArrayData = CustomizablePhaseGlow.getShipProperty(ship, uniform.arrayLengthReferenceTarget);

                if (targetArrayData != null) {
                    int length = 0;

                    if (targetArrayData instanceof float[][] multiDimArray) {
                        length = multiDimArray.length;
                    } else if (targetArrayData instanceof float[] singleDimArray) {
                        length = singleDimArray.length;
                    } else if (targetArrayData instanceof Object[] objArray) {
                        length = objArray.length;
                    }

                    CustomizablePhaseGlow.setShipProperty(ship, "arrLen_" + uniform.name, length, false);
                }
            }

            uniform.update(shader.getUniformManager(), ship, sprite);
        }
    }

    public void dispose() {
        this.shader.dispose();
    }

    @Override
    public String toString() {
        return "ShaderJsonModel{" +
                "\ntitle='" + title + '\'' +
                "\n, id='" + id + '\'' +
                "\n, fragmentShaderPath='" + fragmentShaderPath + '\'' +
                "\n, vertexShaderPath='" + vertexShaderPath + '\'' +
                "\n, shader=" + shader +
                (CustomizablePhaseGlow.Debug ? ", uniforms=" + uniforms : ", uniforms=" + uniforms.size()) +
                "\n}\n";
    }
}
