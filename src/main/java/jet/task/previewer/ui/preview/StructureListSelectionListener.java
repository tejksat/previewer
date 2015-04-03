package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import jet.task.previewer.ui.structure.StructureList;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class StructureListSelectionListener implements ListSelectionListener {
    private final StructureList structureList;
    private final PreviewComponent previewComponent;

    private ImageLoadSwingWorker currentWorker;

    public StructureListSelectionListener(@NotNull StructureList structureList,
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
        DirectoryElement selectedValue = structureList.getSelectedValue();
        if (selectedValue != null) {
            if (!selectedValue.isFile()) {
                previewComponent.nothingToPreview();
            } else {
                currentWorker = new ImageLoadSwingWorker(selectedValue, previewComponent);
                currentWorker.execute();
            }
        } else {
            previewComponent.nothingToPreview();
        }
    }
}
