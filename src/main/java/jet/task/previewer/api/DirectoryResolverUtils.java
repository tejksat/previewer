package jet.task.previewer.api;

import jet.task.previewer.api.fs.FileElement;
import jet.task.previewer.api.fs.FileResolvedDirectory;
import jet.task.previewer.api.zip.ZipElement;
import jet.task.previewer.api.zip.ZipResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for resolving fs and zip directories contents.
 */
public class DirectoryResolverUtils {
    private DirectoryResolverUtils() {
    }

    public static FileResolvedDirectory resolveFileDirectory(@NotNull Path path) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        List<FileElement> directoryContent = new ArrayList<>();
        for (Path child : directoryStream) {
            directoryContent.add(new FileElement(child));
        }
        return new FileResolvedDirectory(path, directoryContent);
    }

    public static ZipResolvedDirectory resolveZipDirectory(@NotNull Path path, @NotNull Path basePath) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        List<ZipElement> directoryContent = new ArrayList<>();
        for (Path child : directoryStream) {
            directoryContent.add(new ZipElement(child, basePath));
        }
        return new ZipResolvedDirectory(path, directoryContent, basePath);
    }

    public static boolean isZipFile(@NotNull Path element) {
        return Files.isRegularFile(element) && hasZipExtension(element.getFileName().toString());
    }

    public static boolean hasZipExtension(@NotNull String pathname) {
        return pathname.toLowerCase().endsWith(FileResolvedDirectory.ZIP_FILE_EXTENSION);
    }
}
