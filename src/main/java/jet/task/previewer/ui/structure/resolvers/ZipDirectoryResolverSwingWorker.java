package jet.task.previewer.ui.structure.resolvers;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class ZipDirectoryResolverSwingWorker extends ResolverSwingWorker {
    private final Path path;
    private final Path basePath;

    public ZipDirectoryResolverSwingWorker(@NotNull Path path,
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

    public static ZipDirectoryResolverSwingWorker executeNew(@NotNull Path path,
                                                             @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback,
                                                             @NotNull Path basePath) {
        ZipDirectoryResolverSwingWorker worker = new ZipDirectoryResolverSwingWorker(path, doneCallback, basePath);
        worker.execute();
        return worker;
    }
}
