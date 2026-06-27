package DeCell.FPG;

import DeCell.FPG.JavaSlop.ShaderJsonParsing.ShaderJsonModel;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.apache.log4j.Priority;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
// i would global static these if java had that feature

public class FancyPhaseGlow {
    public static boolean Debug = false;
    public static boolean DebugUI = false;
    public static boolean DebugUIHighlightCharlie = true;

    public static List<ShaderJsonModel> PhaseShaders = new ArrayList<>();
    public final static String FPGPrefix = "FPG_";
    public final static String ShipPrefix = "ShaderShip_";

    public static String getKeyFromShip(ShipAPI ship) {
        return FPGPrefix + ShipPrefix + ship.getFleetMemberId();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getShipDataMap(ShipAPI ship) {
        String key = getKeyFromShip(ship);
        Map<String, Object> persistentData = Global.getSector().getPersistentData();

        if (!persistentData.containsKey(key) || !(persistentData.get(key) instanceof HashMap<?, ?>)) {
            persistentData.put(key, new HashMap<String, Object>());
        }

        Map<String, Object> data = (Map<String, Object>) persistentData.get(key);
        return data;
    }

    public static ShaderJsonModel getShaderForShip(ShipAPI ship) {
        Map<String, Object> shipData = getShipDataMap(ship);

        String shaderId = (String) shipData.get("shaderId");
        if (shaderId == null) return null;

        for (ShaderJsonModel phaseShader : PhaseShaders) {
            if (Objects.equals(phaseShader.id, shaderId))
                return phaseShader;
        }
        return null;
    }

    public static void setShaderForShip(ShipAPI ship, ShaderJsonModel shaderJsonModel) {
        setShipProperty(ship, "shaderId", shaderJsonModel.id);
    }

    public static void setShipProperty(ShipAPI ship, String name, Object val) {
        setShipProperty(ship, name, val, true);
    }

    public static void setShipProperty(ShipAPI ship, String name, Object val, boolean log) {
        getShipDataMap(ship).put(name, val);
        if (Debug && log)
            Log("Updated property of " + getKeyFromShip(ship) + " with " + val + " for " + name);
    }

    public static <T> T getShipProperty(ShipAPI ship, String name) {
        Object prop = getShipDataMap(ship).get(name);
        if (prop == null) {
            prop = getShaderForShip(ship).getUniformFromName(name).value;
            setShipProperty(ship, name, prop);
        }

//        if (Debug)
//            Log("Got ship property of " + getKeyFromShip(ship) + " with " + prop);

        return (T) prop;
    }

    public static void Log(String s) {
        Global.getLogger(FancyPhaseGlow.class).log(Priority.INFO, s);
    }

    public static void LogWarn(String s) {
        Global.getLogger(FancyPhaseGlow.class).log(Priority.WARN, s);
    }

    public static void LogErr(String s) {
        Global.getLogger(FancyPhaseGlow.class).log(Priority.ERROR, s);
    }

    public static void UpdateShaders() {
        for (ShaderJsonModel phaseShader : PhaseShaders) {
            phaseShader.dispose();
        }
        PhaseShaders.clear();
        try {
            JSONObject master = new JSONObject();

            for (ModSpecAPI modSpecAPI : Global.getSettings().getModManager().getAvailableModsCopy()) {
                try {
                    String modJson = "";
                    try {
                        modJson = Global.getSettings().loadText("data/shaders/fpg/PhaseShaders.json", modSpecAPI.getId());
                        // when life gives you bad apis you try and catch
                    } catch (Exception e) {continue;}
                    // yes this regex is bad for sanitising properly as it does not take into account the slashes in strings,
                    // however for the purposes of this it does not matter
                    String sanitizedJson = modJson.replaceAll("//.*", "");

                    // Now parse the sanitized string safely
                    JSONObject currentModObj = new JSONObject(sanitizedJson);

                    Iterator<?> keys = currentModObj.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        Object value = currentModObj.get(key);

                        master.put(key, value);
                    }
                } catch (Exception e) {
                    LogErr("Error loading shader JSON for mod: " + modSpecAPI.getId());
                    throw e;
                }
            }

            JSONArray masterArray = master.getJSONArray("Shaders");
            for (int i = 0; i < masterArray.length(); i++) {
                ShaderJsonModel zaza = new ShaderJsonModel(masterArray.getJSONObject(i));
                if (zaza.fragmentShaderPath.startsWith("./"))
                    zaza.fragmentShaderPath = zaza.fragmentShaderPath.replace("./", "data/shaders/fpg/");
                if (zaza.vertexShaderPath.startsWith("./"))
                    zaza.vertexShaderPath = zaza.vertexShaderPath.replace("./", "data/shaders/fpg/");

                zaza.compile();
                PhaseShaders.add(zaza);
            }

        } catch (JSONException e) { // İ HATE YOU JAVA, İ HATE YOU
            throw new RuntimeException("IRONED WITH HATE, FUELED BY SPITE\n" + e.getMessage());
        }
    }

    public static class Patterns {
        public final static Pattern NUMBER_ONLY = Pattern.compile("-?[0-9]*");
        public final static Pattern DECIMAL_ONLY = Pattern.compile("-?[0-9]*\\.?[0-9]*");

        public static Pattern decimalWithMaxDecimalPlaces(int decimalPlaces) {
            if (decimalPlaces == 0)
                return NUMBER_ONLY;
            return Pattern.compile("-?[0-9]*\\.?[0-9]{0," + decimalPlaces + "}");
        }
    }
}
