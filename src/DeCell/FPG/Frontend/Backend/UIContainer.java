package DeCell.FPG.Frontend.Backend;

import DeCell.FPG.Frontend.Backend.Components.MyPanel;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public abstract class UIContainer<T extends UIElement<T, U>, U extends UIComponentAPI> extends UIElement<T, U> {

    protected final List<UIElement<?, ?>> activeUIElements = new ArrayList<>();
    protected final List<UIElement<?, ?>> UIElements = new ArrayList<>();

    public List<UIElement<?, ?>> getUIElements() {
        return UIElements;
    }

    public UIContainer(U u) {
        super(u);
    }

    public void addElement(UIElement<?, ?> element) {
        this.UIElements.add(element);
    }

    public void addElementToFirst(UIElement<?, ?> element) {
        this.UIElements.add(0, element);
    }

    public void removeElement(UIElement<?, ?> element) {
        this.UIElements.remove(element);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        List<UIElement<?, ?>> tempList = new ArrayList<>(activeUIElements);

        for (UIElement<?, ?> element : tempList)
            if (element.isMarkedForDeletion()) {
                activeUIElements.remove(element);
            }

        for (UIElement<?, ?> element : activeUIElements) {
            element.advance(amount);
            if (this.isMarkedForDeletion())
                element.markForDeletion();
        }


        if (!UIElements.isEmpty()) {
            activeUIElements.addAll(0, UIElements);
            UIElements.clear();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (UIElement<?, ?> element : activeUIElements) {
            element.processInput(events);
        }
        super.processInput(events);
    }

    @Override
    public T update() {
        for (UIElement<?, ?> element : activeUIElements) {
            element.update();
        }

        return super.update();
    }


    public boolean tryRemoveComponent(UIElement<?, ?> comp) {

        try {
            ((MyPanel) this).u.removeComponent(comp.u);
            comp.markForDeletion();
        } catch (Exception e) {
            return false;
        }


        return true;
    }

}
