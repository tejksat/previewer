package jet.task.previewer.api.zip;

import jet.task.previewer.api.DirectoryResolverUtils;
import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.SwingWorkerResolver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Resolves directory in ZIP archive.
 */
public class ZipDirectoryResolver extends SwingWorkerResolver {
    private final Path path;
    private final Path basePath;

    private ZipDirectoryResolver(@NotNull Path path,
                                 @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback,
                                 @NotNull Path basePath) {
        super(doneCallback);
        this.path = path;
        this.basePath = basePath;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws IOException {
        return DirectoryResolverUtils.resolveZipDirectory(path, basePath);
    }

    public static ZipDirectoryResolver submit(@NotNull Path path,
                                              @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback,
                                              @NotNull Path basePath) {
        ZipDirectoryResolver worker = new ZipDirectoryResolver(path, doneCallback, basePath);
        worker.execute();
        return worker;
    }
}
