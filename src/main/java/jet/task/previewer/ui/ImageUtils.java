package jet.task.previewer.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class ImageUtils {
    private ImageUtils() {
    }

    @Nullable
    public static ImageIcon createImageIcon(@NotNull String path, @Nullable String description) {
        java.net.URL imgURL = ImageUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            // todo log appropriately
            System.err.println("Couldn't find file: " + path);
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
