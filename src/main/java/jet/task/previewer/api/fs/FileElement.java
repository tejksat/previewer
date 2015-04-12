package jet.task.previewer.api.fs;

import jet.task.previewer.api.DirectoryResolverUtils;
import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.PathElement;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.zip.ZipResolver;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * Regular file or directory.
 */
public class FileElement extends PathElement {
    public FileElement(@NotNull Path path) {
        super(path);
    }

    @Override
    public boolean canBeResolvedToDirectory() {
        return Files.isDirectory(path) || DirectoryResolverUtils.isZipFile(path);
    }

    @Override
    public Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        if (Files.isDirectory(path)) {
            return FileDirectoryResolver.submit(path, doneCallback);
        } else if (DirectoryResolverUtils.isZipFile(path)) {
            return ZipResolver.submit(path, doneCallback);
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + path);
        }
    }
}
