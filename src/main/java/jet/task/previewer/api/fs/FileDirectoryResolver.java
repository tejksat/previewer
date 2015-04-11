package jet.task.previewer.api.fs;

import jet.task.previewer.api.DirectoryResolverUtils;
import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.SwingWorkerResolver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Resolves file directory specified by {@link Path}.
 */
public class FileDirectoryResolver extends SwingWorkerResolver {
    private final Path path;

    private FileDirectoryResolver(@NotNull Path path, @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.path = path;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws IOException {
        return DirectoryResolverUtils.resolveFileDirectory(path);
    }

    public static FileDirectoryResolver submit(@NotNull Path path,
                                               @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        FileDirectoryResolver worker = new FileDirectoryResolver(path, doneCallback);
        worker.execute();
        return worker;
    }
}
