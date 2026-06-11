package DeCell.FPG.Frontend.Backend;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

import java.util.function.BiConsumer;

public abstract class BaseBuilder<T> {
    protected UIElement<?, ?> sibling;

    public UIElement<?, ?> getSibling() {
        return this.sibling;
    }

    public T setSibling(UIElement<?, ?> sblng) {
        this.sibling = sblng;
        return (T) this;
    }

    protected Vector2f position;

    public Vector2f getPosition() {
        return this.position;
    }

    public T setPositionData(Vector2f p) {
        this.position = p;
        return (T) this;
    }

    public T position(BiConsumer<UIElement<?, ?>, BaseBuilder<?>> zaza) {
        return (T) this;
    }
}
