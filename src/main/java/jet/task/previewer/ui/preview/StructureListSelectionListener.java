package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import jet.task.previewer.ui.structure.StructureList;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;
import java.util.stream.StreamSupport;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class StructureListSelectionListener implements ListSelectionListener {
    private final StructureList structureList;
    private final PreviewComponent previewComponent;

    private PreviewLoadSwingWorker currentWorker;

    public StructureListSelectionListener(@NotNull StructureList structureList,
                                          @NotNull PreviewComponent previewComponent) {
        this.structureList = structureList;
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
        DirectoryElement selectedValue = structureList.getSelectedValue();
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
