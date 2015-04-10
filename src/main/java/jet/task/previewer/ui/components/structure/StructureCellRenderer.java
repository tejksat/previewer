package jet.task.previewer.ui.components.structure;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.ui.ImageUtils;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import java.awt.Component;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureCellRenderer extends DefaultListCellRenderer {
    private final ImageIcon directoryIcon;
    private final ImageIcon fileIcon;

    public StructureCellRenderer() {
        directoryIcon = ImageUtils.createImageIcon("/icons/fs/directory.png", "File icon");
        fileIcon = ImageUtils.createImageIcon("/icons/fs/file.png", "File icon");
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // todo refactor
        Object originalValue = value;
        if (value instanceof DirectoryElement) {
            value = ((DirectoryElement) value).getName();
        }
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        // todo generalize (use fileSystemView)
        if (originalValue instanceof DirectoryElement) {
            if (((DirectoryElement) originalValue).isDirectory()) {
                setIcon(directoryIcon);
            } else {
                setIcon(fileIcon);
            }
        }
        return component;
    }

}
