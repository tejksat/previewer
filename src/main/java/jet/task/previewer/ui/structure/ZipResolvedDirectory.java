package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.resolvers.FileDirectoryResolverSwingWorker;
import jet.task.previewer.ui.structure.resolvers.ZipDirectoryResolverSwingWorker;
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
            return FileDirectoryResolverSwingWorker.executeNew(basePath.getParent(), doneCallback);
        } else {
            return ZipDirectoryResolverSwingWorker.executeNew(parent, doneCallback, basePath);
        }
    }

/*
    @Override
    public boolean canBeResolvedToDirectory(@NotNull Path element) {
        return Files.isDirectory(element);
    }

    @Override
    public Future<ResolvedDirectory<?>> changeDirectory(@NotNull Path element,
                                                        @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        if (Files.isDirectory(element)) {
            SwingWorker<ResolvedDirectory<?>, Void> worker = new FileDirectoryResolverSwingWorker(element, doneCallback);
            worker.execute();
            return worker;
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + element);
        }
    }
*/
}
