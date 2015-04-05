package jet.task.previewer.ui.structure.resolvers;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class FileDirectoryResolverSwingWorker extends ResolverSwingWorker {
    private final Path path;

    public FileDirectoryResolverSwingWorker(@NotNull Path path, @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.path = path;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws IOException {
        return DirectoryResolverUtils.resolveFileDirectory(path);
    }

    public static FileDirectoryResolverSwingWorker executeNew(@NotNull Path path,
                                                              @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        FileDirectoryResolverSwingWorker worker = new FileDirectoryResolverSwingWorker(path, doneCallback);
        worker.execute();
        return worker;
    }
}
