package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.resolvers.ZipDirectoryResolverSwingWorker;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
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
            return ZipDirectoryResolverSwingWorker.executeNew(path, doneCallback, basePath);
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + path);
        }
    }
}
