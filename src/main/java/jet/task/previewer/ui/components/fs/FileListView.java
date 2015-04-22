package jet.task.previewer.ui.components.fs;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.common.FileSystemUtils;
import jet.task.previewer.ui.ImageUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helps {@link FileListCellRenderer} show fancy icons.
 */
public class FileListView {
    private static final String[] IMAGE_FILES_EXTENSIONS = new String[]{"gif", "jpg", "png"};
    private static final String[] TEXT_FILES_EXTENSIONS = new String[]{"log", "ini", "txt"};
    private static final String ZIP_EXTENSION = "zip";

    private final ImageIcon directoryIcon;
    private final ImageIcon fileIcon;
    private final Map<String, ImageIcon> extensionIcons;

    public FileListView() {
        directoryIcon = ImageUtils.createImageIcon("/icons/fs/directory.png", "File icon");
        fileIcon = ImageUtils.createImageIcon("/icons/fs/file.png", "File icon");
        ImageIcon imageFileIcon = ImageUtils.createImageIcon("/icons/fs/image.png", "Image file icon");
        ImageIcon textFileIcon = ImageUtils.createImageIcon("/icons/fs/text.png", "Text file icon");
        ImageIcon archiveIcon = ImageUtils.createImageIcon("/icons/fs/archive.png", "Archive icon");
        Map<String, ImageIcon> map = new HashMap<>();
        if (imageFileIcon != null) {
            for (String imageExtension : IMAGE_FILES_EXTENSIONS) {
                map.put(imageExtension, imageFileIcon);
            }
        }
        if (textFileIcon != null) {
            for (String textExtension : TEXT_FILES_EXTENSIONS) {
                map.put(textExtension, textFileIcon);
            }
        }
        if (archiveIcon != null) {
            map.put(ZIP_EXTENSION, archiveIcon);
        }
        extensionIcons = Collections.unmodifiableMap(map);
    }

    public ImageIcon getIcon(@NotNull DirectoryElement directoryElement) {
        return !directoryElement.isFile() ? directoryIcon : getFileIcon(directoryElement);
    }

    private ImageIcon getFileIcon(@NotNull DirectoryElement directoryElement) {
        String filenameExtension = FileSystemUtils.getFilenameExtension(directoryElement.getName());
        return extensionIcons.containsKey(filenameExtension) ? extensionIcons.get(filenameExtension) : fileIcon;
    }
}
