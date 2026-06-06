package DeCell.FPG.Frontend.Backend;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.util.ArrayList;
import java.util.List;

public abstract class UIContainer<T extends UIElement<T, U>, U extends UIComponentAPI> extends UIElement<T, U> {

    protected final List<UIElement<?, ?>> activeUIElements = new ArrayList<>();
    protected final List<UIElement<?, ?>> UIElements = new ArrayList<>();

    public UIContainer(U u) {
        super(u);
    }

    public void addElement(UIElement<?, ?> element) {
        this.UIElements.add(element);
    }

    @Override
    public void advance(float amount) {
        for (UIElement<?, ?> element : activeUIElements) {
            element.advance(amount);
        }

        if (!UIElements.isEmpty()) {
            activeUIElements.addAll(UIElements);
            UIElements.clear();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (UIElement<?, ?> element : activeUIElements) {
            element.processInput(events);
        }
    }

    @Override
    public T update() {
        for (UIElement<?, ?> element : activeUIElements) {
            element.update();
        }

        return super.update();
    }
}
