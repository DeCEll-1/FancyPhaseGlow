package DeCell.FPG.Frontend;

import DeCell.FPG.Frontend.Backend.*;
import DeCell.FPG.Frontend.Backend.Components.Charlie.CharlieElement;
import DeCell.FPG.Frontend.Backend.Components.*;
import DeCell.FPG.Frontend.Backend.Components.Combobox.ComboboxElement;
import DeCell.FPG.Frontend.Backend.Components.Combobox.MyCombobox;
import DeCell.FPG.Frontend.Backend.Plugins.PanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static DeCell.FPG.Reflections.invokeMethod;


public class MainRefitPanelPlugin extends PanelPlugin {
    public List<UIElement<?, ?>> ActiveUIElements = new ArrayList<>();
    private final List<UIElement<?, ?>> UIElements = new ArrayList<>();

    public MainRefitPanelPlugin() {
    }

    @Override
    public void init(CustomPanelAPI _p) {
        MyPanel parent = new MyPanel(_p).addTo(UIElements);

        MyPanel refitWindowOpenerButtonPanel = new MyPanel(190, 25, null, parent).inBL(601, 40);

        MyTooltip refitWindowOpenerTooltip = new MyTooltip(190, 25, false, refitWindowOpenerButtonPanel);
        MyButton panelOpeningButton = new MyButton("Modify Phase Effects", new Color(0xDDDDDD), new Color(0x444444), Alignment.MID, CutStyle.TL_BR, 190, 25, 0f, refitWindowOpenerTooltip);

        CharlieElement charlie = new CharlieElement(parent);

        new OpenableButtonPanel(720, 640, panelOpeningButton, charlie).inTL(210, 50)
                .setOnUIOpen((panel, internalData, _UIElements) ->
                {
                    // evil
                    MyTooltip debugOpeningTooltip = new MyTooltip(190, 24, false, panel).inBR(16, 4);
                    MyButton debugButton = new MyButton("Debug", Alignment.MID, CutStyle.TOP, 190, 24, 0, debugOpeningTooltip).inMid();

                    MyPanel shaderSelectionContainer = new MyPanel(190, 25, null, panel).inTL(26, 30);
                    MyTooltip shaderSelectionTooltip = new MyTooltip(190, 25, false, shaderSelectionContainer);
                    new MyCombobox(new MyButton("Select Element", Alignment.MID, CutStyle.TOP, 190, 25, 0, shaderSelectionTooltip), shaderSelectionContainer)
                            .addItem(new ComboboxElement("balls"))
                            .addItem(new ComboboxElement("balls2"))
                            .addItem(new ComboboxElement("balls3"))
                            .addItem(new ComboboxElement("balls4"))
                            .setOnUpdate(el -> {
                            })
                    ;
                });
    }

    private static com.fs.starfarer.coreui.refit.U getRefitPanel() {
        CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

        Object core = dialog == null
                ? invokeMethod("getCore", campaignUI)
                : invokeMethod("getCoreUI", dialog);
        return (com.fs.starfarer.coreui.refit.U) invokeMethod("getRefitPanel", invokeMethod("getCurrentTab", core));
    }

    @Override
    public void advance(float amount) {
        for (UIElement<?, ?> element : ActiveUIElements) {
            element.advance(amount);
        }

        if (!UIElements.isEmpty()) {
            ActiveUIElements.addAll(UIElements);
            UIElements.clear();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (UIElement<?, ?> element : ActiveUIElements) {
            element.processInput(events);
        }
    }
}
