import javax.swing.*;
import java.util.List;

public class ButtonGroupID extends ButtonGroup {
    private int id;
    public ButtonGroupID(int id) {
        super();
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
    public boolean containsButton(AbstractButton b) {
        for (AbstractButton button : getButtons()) {
            if (b == button) return true;
        }
        return false;
    }
    public List<AbstractButton> getButtons() {
        return this.buttons.stream().toList();
    }
}
