package jet.task.previewer.api.zip;

import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.PathElement;
import jet.task.previewer.api.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * ZIP archive element.
 */
public class ZipElement extends PathElement {
    private final Path basePath;

    public ZipElement(@NotNull Path path, @NotNull Path basePath) {
        super(path);
        this.basePath = basePath;
    }

    @Override
    public boolean canBeResolvedToDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        if (Files.isDirectory(path)) {
            return ZipDirectoryResolver.submit(path, doneCallback, basePath);
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + path);
        }
    }
}
