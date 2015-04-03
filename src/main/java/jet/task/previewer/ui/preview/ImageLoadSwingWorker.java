package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class ImageLoadSwingWorker extends SwingWorker<Image, Void> {
    private final DirectoryElement<?> element;
    private final PreviewComponent previewComponent;

    private CancelReason cancelReason;

    public ImageLoadSwingWorker(@NotNull DirectoryElement<?> element, @NotNull PreviewComponent previewComponent) {
        this.element = element;
        this.previewComponent = previewComponent;
    }

    @Override
    protected Image doInBackground() throws IOException {
        return ImageIO.read(element.newInputStream());
    }

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

    @Override
    protected void done() {
        try {
            if (isCancelled()) {
                switch (cancelReason) {
                    case USER_CANCEL:
                        previewComponent.userCancelledPreview();
                        return;
                    case PREVIEW_SOURCE_HAS_CHANGED:
                        // todo do something or not?
                        return;
                }
            } else {
                Image image = get();
                previewComponent.updateImage(image);
            }
        } catch (InterruptedException e) {
            // todo why this could happen?
            e.printStackTrace();
            previewComponent.nothingToPreview();
        } catch (ExecutionException e) {
            e.printStackTrace();
            previewComponent.imageLoadFailed();
        }
    }

    public enum CancelReason {
        USER_CANCEL, PREVIEW_SOURCE_HAS_CHANGED
    }
}
