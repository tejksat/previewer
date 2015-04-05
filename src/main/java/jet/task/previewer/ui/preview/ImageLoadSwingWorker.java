package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class ImageLoadSwingWorker extends PreviewLoadSwingWorker<Image> {
    public ImageLoadSwingWorker(@NotNull DirectoryElement<?> element, @NotNull PreviewComponent previewComponent) {
        super(element, previewComponent);
    }

    @Override
    protected Image consumeInputStream(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
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
}
