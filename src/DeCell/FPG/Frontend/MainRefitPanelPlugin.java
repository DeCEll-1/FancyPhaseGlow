package DeCell.FPG.Frontend;

import DeCell.FPG.Frontend.Backend.*;
import DeCell.FPG.Frontend.Backend.Components.*;
import DeCell.FPG.Frontend.Backend.Components.Combobox.ComboboxElement;
import DeCell.FPG.Frontend.Backend.Components.Combobox.MyCombobox;
import DeCell.FPG.Frontend.Backend.Plugins.PanelPlugin;
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

    @Override
    public void init(CustomPanelAPI _p) {
        MyPanel parent = new MyPanel(_p).addTo(UIElements);

        MyPanel refitWindowOpenerButtonPanel = new MyPanel.Builder(190, 25).build(parent).inBL(601, 40);

        MyButton panelOpeningButton = new MyButton.Builder("Modify Phase Effects", 190, 25, refitWindowOpenerButtonPanel)
                .setColors(new Color(0xDDDDDD), new Color(0x444444))
                .setStyle(Alignment.MID, CutStyle.TL_BR)
                .build();


        new DialougeButtonPanel.Builder(720, 640, panelOpeningButton).withCharlie().popup(UIElements)
                .addToInternalData(pair("ship", ship))
                .setOnUIOpen((panel, dialogue, _UIElements) ->
                {
                    Ship currShip = dialogue.getFromInternal("ship");

                    MyButton debugButton = new MyButton.Builder("Debug", 190, 24, panel)
                            .setStyle(Alignment.MID, CutStyle.TOP)
                            .position(s -> s.inBR(16, 4))
                            .build().inMid();

                    new MyCombobox.Builder(
                            190, 25,
                            new MyButton.Builder("Select Element")
                                    .setStyle(Alignment.MID, CutStyle.TOP),
                            panel
                    ).position(s -> s.inTL(26, 30))
                            .build()
                            .addItem(new ComboboxElement("balls"))
                            .addItem(new ComboboxElement("balls2"))
                            .addItem(new ComboboxElement("balls3"))
                            .addItem(new ComboboxElement("balls4"))
                            .setOnUpdate((cb, el) -> {
                            })
                    ;


                    new ColorPicker.Builder().withAlpha().build(panel).inBL(20, 20)
                            .addToInternalData(pair("debug_btn", debugButton))
                            .setOnColorChange(s -> {
                                MyButton dbgButton = s.getFromInternal("debug_btn");
                                dbgButton.setText(s.getColor().toString());

                            });

                });
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
