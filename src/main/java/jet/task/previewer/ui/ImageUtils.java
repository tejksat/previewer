package jet.task.previewer.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for loading images.
 */
public class ImageUtils {
    private final static Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    private ImageUtils() {
    }

    @Nullable
    public static ImageIcon createImageIcon(@NotNull String path, @Nullable String description) {
        java.net.URL imgURL = ImageUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            logger.error("Couldn't find image icon file: {}", path);
            return null;
        }
    }

    @Nullable
    public static Image createImage(@NotNull String path) {
        ImageIcon imageIcon = createImageIcon(path, null);
        return imageIcon != null ? imageIcon.getImage() : null;
    }

    @NotNull
    public static List<Image> createImages(String... paths) {
        ArrayList<Image> images = new ArrayList<>();
        for (String path : paths) {
            Image image = createImage(path);
            if (image != null) {
                images.add(image);
            }
        }
        return images;
    }
}
