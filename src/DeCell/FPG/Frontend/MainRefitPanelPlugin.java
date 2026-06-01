package DeCell.FPG.Frontend;

import DeCell.FPG.Frontend.Backend.*;
import DeCell.FPG.Frontend.Backend.Components.Combobox.ComboboxElement;
import DeCell.FPG.Frontend.Backend.Components.Combobox.MyCombobox;
import DeCell.FPG.Frontend.Backend.Components.MyButton;
import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import DeCell.FPG.Frontend.Backend.Components.MyTooltip;
import DeCell.FPG.Frontend.Backend.Plugins.CPanelPlugin;
import DeCell.FPG.Frontend.Backend.Plugins.LambdaUIPanelPlugin;
import DeCell.FPG.Frontend.Backend.Plugins.MultiPluginHandler;
import DeCell.FPG.Frontend.Backend.Renderable.BackgroundRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.BorderRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MainRefitPanelPlugin extends CPanelPlugin {
    public List<AUIElement<?, ?>> ActiveUIElements = new ArrayList<>();
    private final List<AUIElement<?, ?>> UIElements = new ArrayList<>();
    private boolean uiOpen = false;

    public MainRefitPanelPlugin() {
        System.out.println("New MainRefitPanelPlugin instance constructed! ID: " + System.identityHashCode(this));
    }

    @Override
    public void init(CustomPanelAPI _p) {
        MyPanel parent = new MyPanel(_p);

        MyPanel refitWindowOpenerButtonPanel = new MyPanel(190, 25, null, parent).addTo(UIElements);
        refitWindowOpenerButtonPanel.getPosition().inBL(601, 40);

        MyTooltip refitWindowOpenerTooltip = new MyTooltip(190, 25, false, refitWindowOpenerButtonPanel);
        new MyButton("Modify Phase Effects", null, new Color(0xDDDDDD), new Color(0x444444), Alignment.MID, CutStyle.TL_BR, 190, 25, 0f, refitWindowOpenerTooltip)
                .setOnClick((b) ->
                {
                    if (uiOpen)
                        return;
                    uiOpen = true;

//                    FancyPhaseGlow.Log("Opened Modify Phase Effects Menu");
                    // evil
                    MyPanel charlie = new MyPanel(parent.w(), parent.h(), new RenderableHandlerPlugin()
                            .addBelow(new BackgroundRenderable(new Color(0x9a000000, true))), parent).addTo(UIElements)
                            .inBL(0, 0); // its called charlie because hes evil

                    MyPanel mainEditingPanel = new MyPanel(720, 640, new MultiPluginHandler()
                            .add(new RenderableHandlerPlugin()
                                    .addBelow(
                                            new BorderRenderable(Global.getSettings().getSprite("fpg", "border2"), 32)
                                                    .setPadding(-8).setRenderInside(true))
                            ).add(new LambdaUIPanelPlugin()
                                    .onProcessInput(e ->
                                            e.forEach(InputEventAPI::consume)
                                    )
                            ), charlie).addTo(UIElements)
                            .inTL(210, 50);

                    MyTooltip exitTooltip = new MyTooltip(24, 24, false, mainEditingPanel).addTo(UIElements).inTR(26, 16);

                    new MyButton("X", null, 24, 24, 0, exitTooltip).
                            setOnClick(s -> {
                                parent.u.removeComponent(charlie.u);
                                uiOpen = false;
                            }).addTo(UIElements);

                    MyPanel shaderSelectionContainer = new MyPanel(190, 25, null, mainEditingPanel).addTo(UIElements).inTL(26, 30);

                    MyTooltip shaderSelectionTooltip = new MyTooltip(190, 25, false, shaderSelectionContainer).addTo(UIElements);

                    new MyCombobox(new MyButton("Select Element", null, Alignment.MID, CutStyle.TOP, 190, 25, 0, shaderSelectionTooltip).addTo(UIElements), shaderSelectionTooltip, shaderSelectionContainer)
                            .addItem(new ComboboxElement("balls"))
                            .addItem(new ComboboxElement("balls2"))
                            .addItem(new ComboboxElement("balls3"))
                            .addItem(new ComboboxElement("balls4"))
                            .addTo(UIElements)
                    ;


                }).addTo(UIElements);
    }

    @Override
    public void advance(float amount) {
        for (AUIElement<?, ?> element : ActiveUIElements) {
            element.advance(amount);
        }

        if (!UIElements.isEmpty()) {
            ActiveUIElements.addAll(UIElements);
            UIElements.clear();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (AUIElement<?, ?> element : ActiveUIElements) {
            element.processInput(events);
        }
    }
}
