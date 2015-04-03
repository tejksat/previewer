package jet.task.previewer.ui.structure.resolvers;

import jet.task.previewer.ui.structure.FileElement;
import jet.task.previewer.ui.structure.FileResolvedDirectory;
import jet.task.previewer.ui.structure.ZipElement;
import jet.task.previewer.ui.structure.ZipResolvedDirectory;
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

    public static ZipResolvedDirectory resolveZipDirectory(Path path) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
//        List<Path> directoryContent = StreamSupport.stream(directoryStream.spliterator(), false).collect(Collectors.toList());
        List<ZipElement> directoryContent = new ArrayList<>();
        for (Path child : directoryStream) {
            directoryContent.add(new ZipElement(child));
        }
        return new ZipResolvedDirectory(path, directoryContent);
    }

    public static boolean isZipFile(@NotNull Path element) {
        // todo may use isReadable()
        return Files.isRegularFile(element) && element.getFileName().toString().endsWith(FileResolvedDirectory.ZIP_FILE_EXTENSION);
    }
}
