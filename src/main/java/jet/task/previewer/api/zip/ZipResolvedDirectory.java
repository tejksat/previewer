package jet.task.previewer.api.zip;

import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.PathResolvedDirectory;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.fs.FileDirectoryResolver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public class ZipResolvedDirectory extends PathResolvedDirectory<ZipElement> {
    private final Path basePath;

    public ZipResolvedDirectory(@NotNull Path currentPath,
                                @NotNull List<ZipElement> directoryContent,
                                @NotNull Path basePath) {
        super(currentPath, directoryContent);
        this.basePath = basePath;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        Path parent = getCurrentPath().getParent();
        if (parent == null) {
            return FileDirectoryResolver.submit(basePath.getParent(), doneCallback);
        } else {
            return ZipDirectoryResolver.submit(parent, doneCallback, basePath);
        }
    }
}
