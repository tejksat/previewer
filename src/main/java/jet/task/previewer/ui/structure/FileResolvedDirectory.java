package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.resolvers.FileDirectoryResolverSwingWorker;
import jet.task.previewer.ui.structure.resolvers.PreResolvedResolverSwingWorker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public class FileResolvedDirectory extends PathResolvedDirectory<FileElement> {
    public static final String ZIP_FILE_EXTENSION = ".zip";
    public static final String ROOT_ZIP_FOLDER = "/";

    public FileResolvedDirectory(@NotNull Path currentPath,
                                 @NotNull List<FileElement> directoryContent) {
        super(currentPath, directoryContent);
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        Path parent = getCurrentPath().getParent();
        if (parent == null) {
            PreResolvedResolverSwingWorker worker = new PreResolvedResolverSwingWorker(new RootsResolvedDirectory(), doneCallback);
            worker.execute();
            return worker;
        } else {
            return FileDirectoryResolverSwingWorker.executeNew(parent, doneCallback);
        }
    }

/*
    @Override
    public boolean canBeResolvedToDirectory(@NotNull Path element) {
        return Files.isDirectory(element) || DirectoryResolverUtils.isZipFile(element);
    }

    @Override
    public Future<ResolvedDirectory<?>> changeDirectory(@NotNull Path element,
                                                        @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        if (Files.isDirectory(element)) {
            return FileDirectoryResolverSwingWorker.executeNew(element, doneCallback);
        } else if (DirectoryResolverUtils.isZipFile(element)) {
            return ZipResolverSwingWorker.executeNew(element, doneCallback);
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + element);
        }
    }

*/
}
