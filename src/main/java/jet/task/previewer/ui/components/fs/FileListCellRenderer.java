package jet.task.previewer.ui.components.fs;

import jet.task.previewer.api.DirectoryElement;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/**
 * Renders file list element with its name and appropriate icon.
 */
public class FileListCellRenderer extends DefaultListCellRenderer {
    private final FileListView fileListView;

    public FileListCellRenderer() {
        fileListView = new FileListView();
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Object originalValue = value;
        if (value instanceof DirectoryElement) {
            value = ((DirectoryElement) value).getName();
        }
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (originalValue instanceof DirectoryElement) {
            setIcon(fileListView.getIcon((DirectoryElement) originalValue));
        }
        return component;
    }
}
