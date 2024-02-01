import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ButtonGroupManager {
    List<ButtonGroupID> groups = new ArrayList<>();
    public ButtonGroupManager() {

    }
    public int getUnusedID() {
        int id = 0;
        for (; getGroup(id) != null; id ++);
        return id;
    }
    public ButtonGroupID addGroup() {
        ButtonGroupID group = new ButtonGroupID(getUnusedID());
        groups.add(group);
        return group;
    }
    public void addToGroup(int id, AbstractButton button) {
        this.getGroup(id).add(button);
    }
    public ButtonGroupID getGroup(AbstractButton button) {
        for (ButtonGroupID group : groups) {
            if (group.containsButton(button)) return group;
        }
        return null;
    }
    public ButtonGroupID getGroup(int id) {
        for (ButtonGroupID group : groups) {
            if (group.getId() == id) {
                return group;
            }
        }
        return null;
    }
}
