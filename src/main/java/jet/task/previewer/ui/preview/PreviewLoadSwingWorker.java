package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import jet.task.previewer.ui.engine.InputStreamConsumer;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingWorker;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public abstract class PreviewLoadSwingWorker<T> extends SwingWorker<T, Void> {
    protected final DirectoryElement<?> element;
    protected final PreviewComponent previewComponent;

    protected CancelReason cancelReason;

    public PreviewLoadSwingWorker(@NotNull DirectoryElement<?> element, @NotNull PreviewComponent previewComponent) {
        this.element = element;
        this.previewComponent = previewComponent;
    }

    @Override
    protected final T doInBackground() throws IOException {
        return element.consumeInputStream(new InputStreamConsumer<T>() {
            @Override
            public T accept(@NotNull InputStream inputStream) throws IOException {
                return consumeInputStream(inputStream);
            }
        });
    }

    protected abstract T consumeInputStream(InputStream inputStream) throws IOException;

    public void cancelBecausePreviewSelectionChanged() {
        if (!isCancelled()) {
            cancelReason = CancelReason.PREVIEW_SOURCE_HAS_CHANGED;
            cancel(true);
        }
    }

    public void userCancelsPreview() {
        if (!isCancelled()) {
            cancelReason = CancelReason.USER_CANCEL;
            cancel(true);
        }
    }

    public enum CancelReason {
        USER_CANCEL, PREVIEW_SOURCE_HAS_CHANGED
    }
}
