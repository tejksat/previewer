package jet.task.previewer.ui.structure.resolvers;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.FileResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class ZipResolverSwingWorker extends ResolverSwingWorker {
    private final Path element;

    public ZipResolverSwingWorker(@NotNull Path element,
                                  @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.element = element;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws Exception {
        FileSystem fileSystem = FileSystems.newFileSystem(element, null);
        Path path = fileSystem.getPath(FileResolvedDirectory.ROOT_ZIP_FOLDER);
        return DirectoryResolverUtils.resolveZipDirectory(path, element);
    }

    public static ZipResolverSwingWorker executeNew(@NotNull Path path,
                                                    @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        ZipResolverSwingWorker worker = new ZipResolverSwingWorker(path, doneCallback);
        worker.execute();
        return worker;
    }
}
