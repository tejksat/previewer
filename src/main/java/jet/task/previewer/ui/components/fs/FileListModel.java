package jet.task.previewer.ui.components.fs;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Model for file list.
 *
 * @see FileList
 */
public class FileListModel extends AbstractListModel<DirectoryElement> {
    public static final Comparator<DirectoryElement> DEFAULT_DIRECTORY_ELEMENT_COMPARATOR = (o1, o2) -> {
        int compare = -Boolean.compare(o1.isDirectory(), o2.isDirectory());
        return compare != 0 ? compare : String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    };

    private ResolvedDirectory<?> currentDirectory;
    private List<DirectoryElement> sortedContent;

    /**
     * Returns current directory.
     *
     * @return current directory
     */
    public ResolvedDirectory<?> getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Disposes resources associated with resolved directory.
     */
    public void disposeCurrentDirectoryResources() {
        setCurrentDirectory(null, true);
    }

    /**
     * Changes current directory to the new one disposing resources previously.
     *
     * @param currentDirectory new current directory
     * @param disposeResources flag that determines if resources of previous current directory should be disposed
     */
    public void setCurrentDirectory(ResolvedDirectory<?> currentDirectory, boolean disposeResources) {
        int oldSize = getSize();
        clearCurrentDirectory(disposeResources);
        if (oldSize > 0) {
            fireIntervalRemoved(this, 0, oldSize - 1);
        }
        updateCurrentDirectory(currentDirectory, disposeResources);
        int newSize = getSize();
        if (newSize > 0) {
            fireIntervalAdded(this, 0, newSize);
        }
    }

    private void clearCurrentDirectory(boolean disposeResources) {
        if (currentDirectory != null && disposeResources) {
            currentDirectory.dispose();
        }
        currentDirectory = null;
        sortedContent = null;
    }

    private void updateCurrentDirectory(ResolvedDirectory<?> currentDirectory, boolean disposeResources) {
        if (currentDirectory == null) {
            // excessive
            clearCurrentDirectory(disposeResources);
        } else {
            List<DirectoryElement> sortedContent = new ArrayList<>();
            sortedContent.addAll(currentDirectory.getContent());
            sortedContent.sort(DEFAULT_DIRECTORY_ELEMENT_COMPARATOR);

            this.currentDirectory = currentDirectory;
            this.sortedContent = sortedContent;
        }
    }

    public boolean isEmpty() {
        return sortedContent == null || sortedContent.isEmpty();
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
