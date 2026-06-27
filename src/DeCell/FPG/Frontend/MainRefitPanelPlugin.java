package DeCell.FPG.Frontend;

import DeCell.FPG.FancyPhaseGlow;
import DeCell.FPG.Frontend.Backend.*;
import DeCell.FPG.Frontend.Backend.Components.*;
import DeCell.FPG.Frontend.Backend.Components.Combobox.ComboboxElement;
import DeCell.FPG.Frontend.Backend.Components.Combobox.MyCombobox;
import DeCell.FPG.Frontend.Backend.Plugins.PanelPlugin;
import DeCell.FPG.Frontend.Backend.Renderable.BorderRenderable;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.FPG.JavaSlop.ShaderJsonParsing.ShaderJsonModel;
import DeCell.FPG.JavaSlop.ShaderJsonParsing.ShaderUniformModel;
import DeCell.FPG.Reflection.InputEventAPICreator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.combat.entities.Ship;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static DeCell.FPG.Frontend.Backend.DataPair.pair;


public class MainRefitPanelPlugin extends PanelPlugin {
    public List<UIElement<?, ?>> ActiveUIElements = new ArrayList<>();
    private final List<UIElement<?, ?>> UIElements = new ArrayList<>();
    private final Ship ship;


    public MainRefitPanelPlugin(Ship ship) {
        this.ship = ship;
    }

    private static void onUIOpen(MyPanel parent, DialougeButtonPanel dialogue, List<UIElement<?, ?>> _UIElements) {
        Ship currShip = dialogue.getFromInternal("ship");

        MyCombobox cb = MyCombobox.Builder.build(
                190, 25,
                "Select Element",
                parent
        ).inTL(26, 30);

        for (ShaderJsonModel phaseShader : FancyPhaseGlow.PhaseShaders) {
            cb.addItem(new ComboboxElement(phaseShader.title, phaseShader));
        }

        cb.addToInternalData("currShip", currShip)
                .addToInternalData("parent", parent).update();
        cb.setOnChange((c, el) -> {
            Ship _currShip = c.getFromInternal("currShip");
            FancyPhaseGlow.setShaderForShip(_currShip, (ShaderJsonModel) el.data);

            MyPanel uniformsPanel = c.getFromInternal("uniformsPanel");
            MyPanel _parent = c.getFromInternal("parent");
            if (uniformsPanel != null)
                uniformsPanel.markForDeletion();

            uniformsPanel = new MyPanel.Builder(300, 600).setPlugin(
                    new RenderableHandlerPlugin()
                            .addBelow(new BorderRenderable(Global.getSettings().getSprite("fpg", "border2"))
                                    .setSlices(32) // TODO: make a static final for this border as its used in multiple places
                                    .setThickness(8)
                                    .setPadding(-8).setRenderInside(true)
                            )
            ).build(_parent).inRMid(20);
            c.addToInternalData("uniformsPanel", uniformsPanel);

            ShaderJsonModel selectedShader = FancyPhaseGlow.getShaderForShip(_currShip);

            MyPanel lastSibling = null;
            assert selectedShader != null;
            for (ShaderUniformModel uniform : selectedShader.uniforms) {
                if (!uniform.modifyable)
                    continue;
                MyPanel uniformContainer = uniform.createUniformModal(uniformsPanel, _currShip);

                if (lastSibling == null)
                    uniformContainer.inTMid(10);
                else
                    uniformContainer.belowMid(lastSibling.u, 10);

                lastSibling = uniformContainer;
            }
        });

        ShaderJsonModel curr = FancyPhaseGlow.getShaderForShip(currShip);
        if (curr != null)
            cb.setIndex(FancyPhaseGlow.PhaseShaders.indexOf(curr));
    }

    @Override
    public void init(UIContainer<?, CustomPanelAPI> _p) {
        MyPanel parent = new MyPanel(_p.u).addTo(UIElements).setIgnoreEvents(true);
        Rect zaza = parent.rect();

        MyPanel refitWindowOpenerButtonPanel = new MyPanel.Builder(190, 25).build(parent).inBL(606, 40);

        MyButton panelOpeningButton = new MyButton.Builder("Modify Phase Effects", 190, 25, refitWindowOpenerButtonPanel)
                .setColors(new Color(0xDDDDDD), new Color(0x444444))
                .setStyle(Alignment.MID, CutStyle.TL_BR)
                .build();

        new DialougeButtonPanel.Builder(720, 640, panelOpeningButton).withCharlie().build(UIElements)
                .addToInternalData(pair("ship", ship))
                .setOnUIOpen(MainRefitPanelPlugin::onUIOpen);
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
        // need to recreate as the game destroys mouse inputs for stuff like buttons after consuming them
        List<InputEventAPI> zaza = InputEventAPICreator.createImmediateEvents();
        for (UIElement<?, ?> element : ActiveUIElements) {
            element.processInput(zaza);
        }
    }
}
