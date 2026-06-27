package DeCell.CPG.Plugins;

import DeCell.CPG.Frontend.Backend.Components.MyPanel;
import DeCell.CPG.Frontend.MainRefitPanelPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.combat.entities.Ship;
import com.fs.starfarer.coreui.refit.U;

import java.util.Objects;

import static DeCell.CPG.Helpers.UI.getCurrentTab;
import static DeCell.CPG.Helpers.UI.getRefitPanel;

public class CustomBanel implements EveryFrameScript {
    private MyPanel customPanel;
    private String shipID = "";

    public void attachRefitOverlay() {
        CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        CoreUITabId currentTabId = campaignUI.getCurrentCoreTab();

        if (CoreUITabId.REFIT != currentTabId) {
            customPanel = null;
            return;
        }

        UIPanelAPI refitTab = (UIPanelAPI) getCurrentTab();
        U refitPanel = getRefitPanel();
        Ship ship = refitPanel.getShipDisplay().getShip();
        boolean isPhaseShip = ship.getHullSpec().isPhase();
        String tmpShipID = ship.getId();
        if (!isPhaseShip || !Objects.equals(tmpShipID, shipID)) {
            if (customPanel != null)
                refitTab.removeComponent(customPanel.u);
            customPanel = null;
            shipID = tmpShipID;
            return;
        }
        if (customPanel != null) return;

        MainRefitPanelPlugin panelPlugin = new MainRefitPanelPlugin(ship);
        float w = refitTab.getPosition().getWidth();
        float h = refitTab.getPosition().getHeight();
        customPanel = new MyPanel.Builder(w, h).setPlugin(panelPlugin).setInit(false).build(refitTab)
                .inBL(0, 0).initPlugin();
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
