package jet.task.previewer.ui.preview;

import jet.task.previewer.model.Entry;
import org.jetbrains.annotations.NotNull;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.nio.file.Files;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class StructureListSelectionListener implements ListSelectionListener {
    private final JList<Entry> structureList;
    private final PreviewComponent previewComponent;

    private ImageLoadSwingWorker currentWorker;

    public StructureListSelectionListener(@NotNull JList<Entry> structureList,
                                          @NotNull PreviewComponent previewComponent) {
        this.structureList = structureList;
        this.previewComponent = previewComponent;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // todo do this conditionally?
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancelBecausePreviewSelectionChanged();
            currentWorker = null;
        }
        Entry selectedValue = structureList.getSelectedValue();
        if (selectedValue != null) {
            if (Files.isDirectory(selectedValue.getPath())) {
                previewComponent.nothingToPreview();
            } else {
                currentWorker = new ImageLoadSwingWorker(selectedValue.getPath(), previewComponent);
                currentWorker.execute();
            }
        } else {
            previewComponent.nothingToPreview();
        }
    }
}
