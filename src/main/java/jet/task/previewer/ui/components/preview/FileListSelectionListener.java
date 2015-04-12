package jet.task.previewer.ui.components.preview;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.ui.components.fs.FileList;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;
import java.util.stream.StreamSupport;

/**
 * Listens for selection changes in file list and dispatches selected file to preview component.
 */
public class FileListSelectionListener implements ListSelectionListener {
    private final FileList fileList;
    private final PreviewComponent previewComponent;

    private PreviewLoadSwingWorker currentWorker;

    public FileListSelectionListener(@NotNull FileList fileList,
                                     @NotNull PreviewComponent previewComponent) {
        this.fileList = fileList;
        this.previewComponent = previewComponent;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // todo do this conditionally?
        if (e.getValueIsAdjusting()) {
            return;
        }
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancelBecausePreviewSelectionChanged();
            currentWorker = null;
        }
        DirectoryElement selectedValue = fileList.getSelectedValue();
        if (selectedValue != null) {
            if (!selectedValue.isFile()) {
                previewComponent.nothingToPreview();
            } else {
                if (hasImageExtension(selectedValue)) {
                    previewComponent.loadingPreview();
                    currentWorker = new ImageLoadSwingWorker(selectedValue, previewComponent);
                    currentWorker.execute();
                } else if (hasTextExtension(selectedValue)) {
                    previewComponent.loadingPreview();
                    currentWorker = new TextLoadSwingWorker(selectedValue, previewComponent);
                    currentWorker.execute();
                } else {
                    previewComponent.nothingToPreview();
                }
            }
        } else {
            previewComponent.nothingToPreview();
        }
    }

    private static boolean hasImageExtension(DirectoryElement element) {
        return hasOneOfExtensions(element.getName(), ".gif", ".jpg", ".png");
    }

    private boolean hasTextExtension(DirectoryElement element) {
        return hasOneOfExtensions(element.getName(), ".txt", ".ini");
    }

    private static boolean hasOneOfExtensions(String name, String... extensions) {
        name = name.toLowerCase();
        return StreamSupport.stream(Arrays.asList(extensions).spliterator(), false).anyMatch(name::endsWith);
    }
}
