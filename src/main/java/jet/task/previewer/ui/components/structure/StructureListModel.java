package jet.task.previewer.ui.components.structure;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by akoshevoy on 07.04.2015.
 */
public class StructureListModel extends AbstractListModel<DirectoryElement> {
    public static final Comparator<DirectoryElement> DEFAULT_DIRECTORY_ELEMENT_COMPARATOR = (o1, o2) -> {
        int compare = -Boolean.compare(o1.isDirectory(), o2.isDirectory());
        return compare != 0 ? compare : String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    };

    private ResolvedDirectory<?> currentDirectory;
    private List<DirectoryElement> sortedContent;

    public ResolvedDirectory<?> getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(ResolvedDirectory<?> currentDirectory) {
        int oldSize = getSize();
        clearCurrentDirectory();
        if (oldSize > 0) {
            fireIntervalRemoved(this, 0, oldSize - 1);
        }
        updateCurrentDirectory(currentDirectory);
        int newSize = getSize();
        if (newSize > 0) {
            fireIntervalAdded(this, 0, newSize);
        }
    }

    private void clearCurrentDirectory() {
        this.currentDirectory = null;
        this.sortedContent = null;
    }

    private void updateCurrentDirectory(ResolvedDirectory<?> currentDirectory) {
        if (currentDirectory == null) {
            // excessive
            clearCurrentDirectory();
        } else {
            List<DirectoryElement> sortedContent = new ArrayList<>();
            sortedContent.addAll(currentDirectory.getContent());
            sortedContent.sort(DEFAULT_DIRECTORY_ELEMENT_COMPARATOR);

            this.currentDirectory = currentDirectory;
            this.sortedContent = sortedContent;
        }
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public int getSize() {
        return sortedContent == null ? 0 : sortedContent.size();
    }

    @Override
    public DirectoryElement getElementAt(int index) {
        return sortedContent.get(index);
    }
}
