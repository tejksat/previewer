package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;
import java.nio.file.Path;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // todo refactor
        if (value instanceof DirectoryElement) {
            value = ((DirectoryElement) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
