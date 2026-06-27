package DeCell.CPG.Frontend.Backend.Components.Dialogues;

import DeCell.CPG.Frontend.Backend.Components.MyButton;
import DeCell.CPG.Frontend.Backend.DataPair;
import DeCell.CPG.Frontend.Backend.UIElement;

import java.util.List;
import java.util.function.Consumer;

public interface Dialogueable<T, I> {
    // "T" is the type that will be given when the dialogue closes
    // "I" is the type that will be the initial value of the dialogue
    public void popup(MyButton btn, List<UIElement<?, ?>> UIElements, I initial, Consumer<T> onClose, DataPair... externalData);
}
