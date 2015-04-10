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
 * Created by akoshevoy on 03.04.2015.
 */
public class DirectoryResolverUtils {
    private DirectoryResolverUtils() {
    }

    public static FileResolvedDirectory resolveFileDirectory(Path path) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        List<FileElement> directoryContent = new ArrayList<>();
        for (Path child : directoryStream) {
            directoryContent.add(new FileElement(child));
        }
        return new FileResolvedDirectory(path, directoryContent);
    }

    public static ZipResolvedDirectory resolveZipDirectory(Path path, Path basePath) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
//        List<Path> directoryContent = StreamSupport.stream(directoryStream.spliterator(), false).collect(Collectors.toList());
        List<ZipElement> directoryContent = new ArrayList<>();
        for (Path child : directoryStream) {
            directoryContent.add(new ZipElement(child, basePath));
        }
        return new ZipResolvedDirectory(path, directoryContent, basePath);
    }

    public static boolean isZipFile(@NotNull Path element) {
        // todo may use isReadable()
        return Files.isRegularFile(element) && element.getFileName().toString().endsWith(FileResolvedDirectory.ZIP_FILE_EXTENSION);
    }
}
