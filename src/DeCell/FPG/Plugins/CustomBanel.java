package DeCell.FPG.Plugins;

import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import DeCell.FPG.Frontend.MainRefitPanelPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import static DeCell.FPG.Reflections.invokeMethod;

public class CustomBanel implements EveryFrameScript {
    private MyPanel customPanel;

    public void attachRefitOverlay() {
        CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        CoreUITabId currentTabId = campaignUI.getCurrentCoreTab();

        if (CoreUITabId.REFIT != currentTabId) {
            customPanel = null;
            return;
        }

        InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

        Object core = dialog == null
                ? invokeMethod("getCore", campaignUI)
                : invokeMethod("getCoreUI", dialog);

        UIPanelAPI refitTab = (UIPanelAPI) invokeMethod("getCurrentTab", core);
        if (customPanel != null) return;

        MainRefitPanelPlugin panelPlugin = new MainRefitPanelPlugin();
        float w = refitTab.getPosition().getWidth();
        float h = refitTab.getPosition().getHeight();
        customPanel = new MyPanel(w, h, panelPlugin, refitTab);

    }


    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        attachRefitOverlay();
    }
}
