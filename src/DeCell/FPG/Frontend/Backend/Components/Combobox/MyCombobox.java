package DeCell.FPG.Frontend.Backend.Components.Combobox;

import DeCell.FPG.Frontend.Backend.UIContainer;
import DeCell.FPG.Frontend.Backend.Components.MyButton;
import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import DeCell.FPG.Frontend.Backend.Components.MyTooltip;
import DeCell.FPG.Frontend.Backend.Renderable.RenderableHandlerPlugin;
import DeCell.FPG.Frontend.Backend.UIElement;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static DeCell.FPG.Frontend.Backend.DataPair.pair;

public class MyCombobox extends UIContainer<MyCombobox, UIComponentAPI> {

    private MyButton button;
    private MyPanel panel;
    private MyPanel container;
    private boolean listOpen = false;

    public MyCombobox(MyButton _0, MyPanel _2) {
        super(_2.u);
        this.button = _0;
        this.panel = _2;
        button.setOnMouseDown(this::click);
        _2.addElement(this);
    }

    public MyCombobox setOnUpdate(BiConsumer<MyCombobox, ComboboxElement> onChange) {
        this.onChange = onChange;
        return this;
    }

    private void click(MyButton b) {
        if (!listOpen)
            openList();
        else
            closeList();
    }

    private List<ComboboxElement> elements = new ArrayList<>();
    protected BiConsumer<MyCombobox, ComboboxElement> onChange;

    public MyCombobox addItem(ComboboxElement e) {
        elements.add(e);
        return this;
    }

    private void openList() {
        listOpen = true;
        float w = Math.max(elements.stream().mapToInt(s -> s.text.length() * 16).max().orElse((int) button.w()), panel.w());
        float h = elements.size() * 26;

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
            new MyButton.Builder(element.text, w, itemHeight, elementTooltip).setStyle(Alignment.MID, style).build()
                    .setCustomData(element.data)
                    .addToInternalData(pair("index", i))
                    .setOnMouseDown(b -> {
                                setIndex(b.getFromInternal("index"));
                            }
                    ).addTo(UIElements).inTL(0, 0);
        }
    }

    public MyCombobox setIndex(int index) {
        if (index < 0 || index >= elements.size()) {
            return this;
        }
        ComboboxElement element = elements.get(index);
        if (button != null && button.u != null) {
            button.u.setText(element.text);
            button.u.setCustomData(element.data);
        }
        if (onChange != null) {
            onChange.accept(this, element);
        }
        if (listOpen) {
            closeList();
        }
        return this;
    }

    private void closeList() {
        listOpen = false;
        panel.u.removeComponent(container.u);
    }

    public static class Builder {
        private final float w;
        private final float h;
        //        private final MyButton button;
        private final MyPanel parent;
        private final MyButton.Builder button;

        public Builder(float w, float h, MyButton.Builder btnBuilder, MyPanel parent) {
            this.w = w;
            this.h = h;
            this.parent = new MyPanel.Builder(w, h).build(parent);
            if (!btnBuilder.havesParent())
                btnBuilder.setParent(parent);
            this.button = btnBuilder.setShape(w, h);
        }

        public Builder position(Consumer<UIElement<?, ?>> zaza) {
            zaza.accept(parent);
            button.position(zaza);
            return this;
        }

        public Builder modifyParent(Consumer<MyPanel> zaza) {
            zaza.accept(parent);
            return this;
        }

        public MyCombobox build() {
            return new MyCombobox(this.button.build(), this.parent);
        }
    }

}
