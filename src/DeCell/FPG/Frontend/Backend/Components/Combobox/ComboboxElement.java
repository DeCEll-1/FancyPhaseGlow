package DeCell.FPG.Frontend.Backend.Components.Combobox;

public class ComboboxElement {
    public String text = "";
    public IComboboxData data;

    public ComboboxElement(String _0) {
        this.text = _0;
    }
    public ComboboxElement(IComboboxData _0) {
        this.data = _0;
    }
}
