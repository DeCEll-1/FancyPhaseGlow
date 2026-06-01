package DeCell.FPG.Frontend.Backend.Components;

import DeCell.FPG.Frontend.Backend.AUIElement;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class MyTooltip extends AUIElement<MyTooltip, TooltipMakerAPI> {
    public MyTooltip(TooltipMakerAPI underlying) {
        super(underlying);
    }

    public MyTooltip(float w, float h, boolean withScroller, MyPanel parent) {
        super(parent.u.createUIElement(w, h, withScroller));
        parent.addUIElement(u);
    }
    public MyTooltip(float w, float h, boolean withScroller, CustomPanelAPI parent) {
        super(parent.createUIElement(w, h, withScroller));
        parent.addUIElement(u);
    }

    public PositionAPI getPosition() {
        return u.getPosition();
    }

}
