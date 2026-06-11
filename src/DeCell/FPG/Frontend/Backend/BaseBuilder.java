package DeCell.FPG.Frontend.Backend;

import DeCell.FPG.Frontend.Backend.Components.Slider;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseBuilder<T> {
    protected UIElement<?, ?> sibling;

    public UIElement<?, ?> getSibling() {
        return this.sibling;
    }

    public T addSibling(UIElement<?, ?> sblng) {
        this.sibling = sblng;
        return (T) this;
    }

    public T position(BiConsumer<UIElement<?, ?>, BaseBuilder<T>> zaza) {
        return (T) this;
    }
}
