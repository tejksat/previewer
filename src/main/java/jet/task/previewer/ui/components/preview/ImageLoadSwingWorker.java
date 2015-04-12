package jet.task.previewer.ui.components.preview;

import jet.task.previewer.api.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Loads image in background and updates preview component according to result.
 */
public class ImageLoadSwingWorker extends PreviewLoadSwingWorker<Image> {
    public ImageLoadSwingWorker(@NotNull DirectoryElement element, @NotNull PreviewComponent previewComponent) {
        super(element, previewComponent);
    }

    @NotNull
    @Override
    protected Image consumeInputStream(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            logger.warn("Unsupported image format of file [{}]", element.getName());
            throw new UnsupportedImageFormatException();
        }
        return image;
    }

    @Override
    protected void executionFailed(ExecutionException e) {
        logger.warn("Failed to load image [{}]", element.getName(), e);
        previewComponent.imageLoadFailed();
    }

    @Override
    protected void executionSucceeded(Image result) {
        previewComponent.updateImagePreview(result);
    }
}
