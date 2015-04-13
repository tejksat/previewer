package jet.task.previewer.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Utility methods for working with files and streams.
 */
public class FileSystemUtils {
    private static Logger logger = LoggerFactory.getLogger(FileSystemUtils.class);

    private FileSystemUtils() {
    }

    public static void closeQuietly(@NotNull InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.debug("Failed to close input stream", e);
        }
    }

    @Nullable
    public static Path getUserHomePath() {
        String userHome = System.getProperty("user.home");
        return userHome == null ? null : FileSystems.getDefault().getPath(userHome);
    }

    public static String getFilenameExtension(@NotNull String filename) {
        int i = filename.lastIndexOf(".");
        if (i == -1 || i == filename.length() - 1) {
            return null;
        } else {
            return filename.substring(i + 1);
        }
    }
}
