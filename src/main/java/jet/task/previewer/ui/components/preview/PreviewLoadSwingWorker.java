package jet.task.previewer.ui.components.preview;

import jet.task.previewer.api.DirectoryElement;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public abstract class PreviewLoadSwingWorker<T> extends SwingWorker<T, Void> {
    protected final DirectoryElement element;
    protected final PreviewComponent previewComponent;

    protected CancelReason cancelReason;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public PreviewLoadSwingWorker(@NotNull DirectoryElement element,
                                  @NotNull PreviewComponent previewComponent) {
        this.element = element;
        this.previewComponent = previewComponent;
    }

    @Override
    protected final T doInBackground() throws IOException {
        return element.consumeInputStream(inputStream -> {
            T t = consumeInputStream(inputStream);
            logger.info("Ready to preview {}", element.getName());
            return t;
        });
    }

    @NotNull
    protected abstract T consumeInputStream(InputStream inputStream) throws IOException;

    @Override
    protected final void done() {
        try {
            if (isCancelled()) {
                switch (cancelReason) {
                    case USER_CANCEL:
                        logger.debug("User cancelled preview");
                        previewComponent.userCancelledPreview();
                        break;
                    case PREVIEW_SOURCE_HAS_CHANGED:
                        logger.debug("Preview source has been changed");
                }
            } else {
                executionSucceeded(get());
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Cancellation state has been already checked", e);
        } catch (ExecutionException e) {
            executionFailed(e);
        }
    }

    protected abstract void executionFailed(ExecutionException e);

    protected abstract void executionSucceeded(T result);

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
