package DeCell.FPG.Frontend.Backend.Components.Combobox;

import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.Components.MyButton;
import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import DeCell.FPG.Frontend.Backend.Components.MyTooltip;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MyCombobox extends UIContainer<MyCombobox, UIComponentAPI> {

    private MyButton button;
    private MyPanel panel;
    private MyPanel container;
    private boolean listOpen = false;

    public MyCombobox(MyButton _0, MyPanel _2) {
        super(_2.u);
        this.button = _0;
        this.panel = _2;
        button.setOnClick(this::click);
        _2.addElement(this);
    }

    public MyCombobox setOnUpdate(Consumer<ComboboxElement> onChange) {
        this.onChange = onChange;
        return this;
    }

    private void click(ButtonAPI b) {
        if (!listOpen)
            openList();
        else
            closeList();
    }

    private List<ComboboxElement> elements = new ArrayList<>();
    protected Consumer<ComboboxElement> onChange;

    public MyCombobox addItem(ComboboxElement e) {
        elements.add(e);
        return this;
    }

    private void openList() {
        listOpen = true;
        float w = Math.max(elements.stream().mapToInt(s -> s.text.length() * 16).max().orElse((int) button.w()), panel.w());
        float h = elements.size() * 26;
//        h = 500;

        container = new MyPanel(w, h,
                new RenderableHandlerPlugin()
                , panel)
                .inBL(5, -h).update();

        float itemHeight = 26;
        float paddedHeight = itemHeight + 2; // 2 pixel padding

        for (int i = 0; i < elements.size(); i++) {
            ComboboxElement element = elements.get(i);

            MyTooltip elementTooltip = new MyTooltip(w, itemHeight, false, container).inTL(0, i * paddedHeight + 2);
            CutStyle style = i == elements.size() - 1 ? CutStyle.BOTTOM : CutStyle.NONE;
            new MyButton(element.text, Alignment.MID, style, w, itemHeight, 0, elementTooltip).setCustomData(element.data)
                    .setOnClick(b -> {
                                button.u.setText(b.getText());
                                button.u.setCustomData(b.getCustomData());
                                onChange.accept(element);
                                closeList();
                            }
                    ).addTo(UIElements).inTL(0, 0);
        }
    }

    private void closeList() {
        listOpen = false;
        panel.u.removeComponent(container.u);
    }

}
