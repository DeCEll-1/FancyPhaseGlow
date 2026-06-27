package DeCell.CPG.Plugins;

import DeCell.CPG.CustomizablePhaseGlow;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.apache.log4j.Level;

public class ModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() {
        Global.getLogger(CustomizablePhaseGlow.class).log(Level.INFO, "FPG_Loaded");

        CustomizablePhaseGlow.Debug = Global.getSettings().getBoolean("fpg_debug");
        CustomizablePhaseGlow.DebugUI = Global.getSettings().getBoolean("fpg_debug_ui");
        CustomizablePhaseGlow.DebugUIHighlightCharlie = Global.getSettings().getBoolean("fpg_debug_ui_highlight_charlie");


    }

    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientScript(new CustomBanel());
        CustomizablePhaseGlow.UpdateShaders();
    }


}
