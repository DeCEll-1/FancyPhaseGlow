package DeCell.CPG.Helpers;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

import static DeCell.CPG.Reflection.Reflections.invokeMethod;

public class UI {
    public static com.fs.starfarer.coreui.refit.U getRefitPanel() {
        return (com.fs.starfarer.coreui.refit.U) invokeMethod("getRefitPanel", getCurrentTab());
    }

    public static Object getCurrentTab() {
        CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

        Object core = dialog == null
                ? invokeMethod("getCore", campaignUI)
                : invokeMethod("getCoreUI", dialog);
        return invokeMethod("getCurrentTab", core);
    }
}
