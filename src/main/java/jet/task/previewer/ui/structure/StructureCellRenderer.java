package jet.task.previewer.ui.structure;

import jet.task.previewer.model.Entry;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, ((Entry) value).getName(), index, isSelected, cellHasFocus);
    }
}
