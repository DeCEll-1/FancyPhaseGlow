package DeCell.FPG.Plugins;

import DeCell.FPG.FancyPhaseGlow;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.apache.log4j.Level;

public class ModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() {
        Global.getLogger(FancyPhaseGlow.class).log(Level.INFO, "FPG_Loaded");

        FancyPhaseGlow.Debug = Global.getSettings().getBoolean("fpg_debug");


    }

    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientScript(new CustomBanel());
    }


}
