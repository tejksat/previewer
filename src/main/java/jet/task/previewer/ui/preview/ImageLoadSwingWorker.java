package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    @NotNull
    @Override
    protected Image consumeInputStream(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            logger.warn("Unsupported image format for file {}", element.getName());
            throw new UnsupportedImageFormatException();
        }
        return image;
    }

    @Override
    protected void executionFailed(ExecutionException e) {
        logger.warn("Failed to load image", e);
        previewComponent.imageLoadFailed();
    }

    @Override
    protected void executionSucceeded(Image result) {
        previewComponent.updateImage(result);
    }
}
