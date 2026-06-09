package DeCell.FPG.Frontend.Backend;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

public abstract class UIElement<T extends UIElement<T, U>, U extends UIComponentAPI> {
    public final U u; // underlying

    protected Consumer<T> onMouseEnter;
    protected Consumer<T> onMouseExit;
    protected Consumer<T> onHover;

    protected boolean wasHovered = false;

    protected Dictionary<String, Object> internalData = new Hashtable<>();

    public T initInteralData(DataPair... entries) {
        for (DataPair entry : entries) {
            if (entry != null) {
                internalData.put(entry.key, entry.value);
            }
        }
        return (T) this;
    }

    public <Z> Z getFromInternal(String s) {
        return (Z) this.internalData.get(s);
    }

    public UIElement(U underlying) {
        this.u = underlying;
    }

    public void processInput(List<InputEventAPI> events) {
        if (u == null) return;

        boolean isCurrentlyHovered = false;

        if (this.rect().containsMouse()) {
            isCurrentlyHovered = true;
        }

        if (isCurrentlyHovered && onHover != null)
            onHover.accept((T) this);

        if ((isCurrentlyHovered && !wasHovered) && onMouseEnter != null)
            onMouseEnter.accept((T) this);
        else if ((!isCurrentlyHovered && wasHovered) && onMouseExit != null)
            onMouseExit.accept((T) this);


        wasHovered = isCurrentlyHovered;
    }

    public void advance(float amount) {
    }

    public T update() {

        return (T) this;
    }

    public T addTo(List<UIElement<?, ?>> l) {
        l.add(this);
        return (T) this;
    }

    public T setOnMouseEnter(Consumer<T> onMouseEnter) {
        this.onMouseEnter = onMouseEnter;
        return (T) this;
    }

    public T setOnHover(Consumer<T> onHover) {
        this.onHover = onHover;
        return (T) this;
    }

    public T setOnMouseExit(Consumer<T> onMouseExit) {
        this.onMouseExit = onMouseExit;
        return (T) this;
    }

    public PositionAPI getPosition() {
        return u.getPosition();
    }

    public Rect rect() {
        return new Rect(x(), y(), w(), h());
    }

    public float x() {
        return u.getPosition().getX();
    }

    public float y() {
        return u.getPosition().getY();
    }

    public float w() {
        return u.getPosition().getWidth();
    }

    public float h() {
        return u.getPosition().getHeight();
    }

    public T setLocation(float x, float y) {
        getPosition().setLocation(x, y);
        return (T) this;
    }

    public T setSize(float width, float height) {
        getPosition().setSize(width, height);
        return (T) this;
    }

    public T setXAlignOffset(float xAlignOffset) {
        getPosition().setXAlignOffset(xAlignOffset);
        return (T) this;
    }

    public T setYAlignOffset(float yAlignOffset) {
        getPosition().setYAlignOffset(yAlignOffset);
        return (T) this;
    }

//#region --- Parent Container Positioning ---

    public T inTL(float xPad, float yPad) {
        getPosition().inTL(xPad, yPad);
        return (T) this;
    }

    public T inTMid(float yPad) {
        getPosition().inTMid(yPad);
        return (T) this;
    }

    public T inTR(float xPad, float yPad) {
        getPosition().inTR(xPad, yPad);
        return (T) this;
    }

    public T inRMid(float xPad) {
        getPosition().inRMid(xPad);
        return (T) this;
    }

    public T inMid() {
        getPosition().inMid();
        return (T) this;
    }

    public T inBR(float xPad, float yPad) {
        getPosition().inBR(xPad, yPad);
        return (T) this;
    }

    public T inBMid(float yPad) {
        getPosition().inBMid(yPad);
        return (T) this;
    }

    public T inBL(float xPad, float yPad) {
        getPosition().inBL(xPad, yPad);
        return (T) this;
    }

    public T inLMid(float xPad) {
        getPosition().inLMid(xPad);
        return (T) this;
    }

    //#endregion

//#region --- Sibling Relative Positioning ---

    public T leftOfTop(UIComponentAPI sibling, float xPad) {
        getPosition().leftOfTop(sibling, xPad);
        return (T) this;
    }

    public T leftOfMid(UIComponentAPI sibling, float xPad) {
        getPosition().leftOfMid(sibling, xPad);
        return (T) this;
    }

    public T leftOfBottom(UIComponentAPI sibling, float xPad) {
        getPosition().leftOfBottom(sibling, xPad);
        return (T) this;
    }

    public T rightOfTop(UIComponentAPI sibling, float xPad) {
        getPosition().rightOfTop(sibling, xPad);
        return (T) this;
    }

    public T rightOfMid(UIComponentAPI sibling, float xPad) {
        getPosition().rightOfMid(sibling, xPad);
        return (T) this;
    }

    public T rightOfBottom(UIComponentAPI sibling, float xPad) {
        getPosition().rightOfBottom(sibling, xPad);
        return (T) this;
    }

    public T aboveLeft(UIComponentAPI sibling, float yPad) {
        getPosition().aboveLeft(sibling, yPad);
        return (T) this;
    }

    public T aboveMid(UIComponentAPI sibling, float yPad) {
        getPosition().aboveMid(sibling, yPad);
        return (T) this;
    }

    public T aboveRight(UIComponentAPI sibling, float yPad) {
        getPosition().aboveRight(sibling, yPad);
        return (T) this;
    }

    public T belowLeft(UIComponentAPI sibling, float yPad) {
        getPosition().belowLeft(sibling, yPad);
        return (T) this;
    }

    public T belowMid(UIComponentAPI sibling, float yPad) {
        getPosition().belowMid(sibling, yPad);
        return (T) this;
    }

    public T belowRight(UIComponentAPI sibling, float yPad) {
        getPosition().belowRight(sibling, yPad);
        return (T) this;
    }

    //#endregion
}
